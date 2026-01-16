package org.example.editorapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;

import org.example.editorapp.CommonMark.CommonMarkConverter;
import org.example.editorapp.Nui.NuiController;
import org.example.editorapp.ProgressLabel.ProgresoSimulado;
import org.example.editorapp.ProgressLabel.ProgressLabel;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * Controlador para la interfaz de usuario del editor definida en Editor.fxml.
 * Contiene toda la lógica para manejar las interacciones del usuario, como clics en botones,
 * y para manipular el contenido y los estilos del área de texto.
 */
public class EditorController{

    private Stage findReplaceStage;
    private int lastFindIndex = 0;

    private boolean isDictationRunning;

    @FXML
    private InlineCssTextArea textArea;

    @FXML
    private Label wordsLabel;

    @FXML
    private Label charsLabel;

    @FXML
    private Label charsNoSpacesLabel;

    @FXML
    private Button copyBtn;

    @FXML
    private Button voiceCommandButton;

    // Botón para invertir el texto del área de texto
    @FXML
    private Button invertBtn;
    
    private NuiController nuiController;
    

    @FXML
    private ProgressLabel progresslabel;

    @FXML
    private Label logVosk; // TODO implementar en la interfaz

    @FXML
    private ToggleButton dictationToggleButton;



    // TODO cambiar color de boton cuando esta presionado

    // Almacena el texto original antes de ser invertido
    private String originalText;
    // Indica si el texto actual en el área de texto está invertido
    private boolean isInverted = false;

    /**
     * Se ejecuta al pulsar el botón de Negrita.
     * Aplica o remueve el estilo de negrita al texto seleccionado.
     */
    @FXML
    protected void onBold() {
        toggleStyle("-fx-font-weight: bold");
    }

    /**
     * Se ejecuta al pulsar el botón de Cursiva.
     * Aplica o remueve el estilo de cursiva al texto seleccionado.
     */
    @FXML
    protected void onItalic() {
        toggleStyle("-fx-font-style: italic");
    }

    /**
     * Se ejecuta al pulsar el botón de Mayúsculas (AA).
     * Convierte el texto seleccionado (o todo el texto) a mayúsculas.
     */
    @FXML
    protected void onUppercase() {
        transformSelection(true);
    }

    /**
     * Se ejecuta al pulsar el botón de Minúsculas (aa).
     * Convierte el texto seleccionado (o todo el texto) a minúsculas.
     */
    @FXML
    protected void onLowercase() {
        transformSelection(false);
    }

    @FXML
    protected void dictationToggleMethod() {
        if(!isDictationRunning) {
            dictationToggleButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            nuiController.startDictation();
            isDictationRunning = true;
            logVosk.setText("Dictado iniciado...");
            System.out.println("Iniciando dictado");
        } else {
            dictationToggleButton.setStyle(""); // Revert to default style
            nuiController.stopDictation();
            isDictationRunning = false;
            logVosk.setText("Dictado finalizado.");
            System.out.println("Finalizando dictado");
        }
    }

    /**
     * Se ejecuta al pulsar el botón de Copiar.
     * Copia el texto seleccionado al portapapeles en dos formatos: texto plano y HTML.
     * El formato HTML preserva los estilos de formato (negrita, cursiva, color).
     */
    @FXML
    protected void onCopy() {
        System.out.println("onCopy called");

        // Obtener los límites de selección (o todo el texto si no hay selección)
        IndexRange selection = textArea.getSelection();
        int start = selection.getLength() > 0 ? selection.getStart() : 0;
        int end = selection.getLength() > 0 ? selection.getEnd() : textArea.getLength();

        // Obtener el texto seleccionado
        String plainText = textArea.getText(start, end);

        // Construir el HTML con estilos
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><body><p>");

        for (int i = start; i < end; i++) {
            String ch = textArea.getText(i, i + 1);
            String style = textArea.getStyleAtPosition(i);

            if (style == null) style = "";

            // Determinar formato HTML según estilos CSS
            boolean bold = style.contains("font-weight: bold");
            boolean italic = style.contains("font-style: italic");
            String color = null;

            if (style.contains("fx-fill:")) {
                // ejemplo: -fx-fill: #ff0000;
                int idx = style.indexOf("fx-fill:");
                color = style.substring(idx + 8).split("[;]")[0].trim();
            }

            // Abrir etiquetas según estilo
            if (bold) htmlBuilder.append("<b>");
            if (italic) htmlBuilder.append("<i>");
            if (color != null) htmlBuilder.append("<span style=\"color:").append(color).append("\">");

            // Escapar caracteres HTML
            htmlBuilder.append(ch
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;"));

            // Cerrar etiquetas en orden inverso
            if (color != null) htmlBuilder.append("</span>");
            if (italic) htmlBuilder.append("</i>");
            if (bold) htmlBuilder.append("</b>");
        }

        htmlBuilder.append("</p></body></html>");
        String htmlText = htmlBuilder.toString();

        // Copiar tanto texto plano como HTML al portapapeles
        ClipboardContent content = new ClipboardContent();
        content.putString(plainText);
        content.putHtml(htmlText);
        Clipboard.getSystemClipboard().setContent(content);

        // Feedback visual del botón
        copyBtn.setText("¡Copiado!");
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> copyBtn.setText("Copiar"));
        pause.play();
    }


    /**
     * Se ejecuta al pulsar el botón de Pegar.
     * Pega el contenido del portapapeles en la posición actual del cursor.
     */
    @FXML
    protected void onPaste() {
        System.out.println("onPaste called");
        textArea.paste();
    }

    /**
     * Se ejecuta al pulsar el botón de Limpiar.
     * Borra todo el contenido del área de texto.
     */
    @FXML
    protected void onClear() {
        textArea.clear();
    }

    /**
     * Método auxiliar para aplicar o quitar un estilo CSS al texto seleccionado.
     * Si el estilo ya está presente, lo quita. Si no, lo añade.
     * @param styleToToggle El string del estilo CSS a aplicar/quitar (ej. "-fx-font-weight: bold").
     */
    private void toggleStyle(String styleToToggle) {
        IndexRange selection = textArea.getSelection();
        if (selection.getLength() == 0) {
            return;
        }

        StyleSpans<String> styleSpans = textArea.getStyleSpans(selection.getStart(), selection.getEnd());

        // Determine if the style is present across the entire selection
        boolean styleIsPresent = styleSpans.stream()
                .allMatch(span -> span.getStyle().contains(styleToToggle));

        StyleSpansBuilder<String> builder = new StyleSpansBuilder<>();
        for (StyleSpan<String> span : styleSpans) {
            String style = span.getStyle();
            String newStyle;

            if (styleIsPresent) {
                // Remove the style
                newStyle = style.replace(styleToToggle, "");
            } else {
                // Add the style if it's not already there
                if (!style.contains(styleToToggle)) {
                    newStyle = style + (style.isEmpty() ? "" : "; ") + styleToToggle;
                } else {
                    newStyle = style;
                }
            }

            // Clean up the style string
            newStyle = newStyle.replaceAll(";;", ";").replaceAll("\\s+;", ";").replaceAll(";\\s+", ";").trim();
            if (newStyle.startsWith(";")) {
                newStyle = newStyle.substring(1);
            }
            if (newStyle.endsWith(";")) {
                newStyle = newStyle.substring(0, newStyle.length() - 1);
            }

            builder.add(newStyle, span.getLength());
        }

        textArea.setStyleSpans(selection.getStart(), builder.create());
    }


    /**
     * Método para capturar atajos de teclado en el textArea.
     */
    private void catchShortcuts(){
        textArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.isShortcutDown() && event.getCode() == KeyCode.S){
                onSave();
                System.out.println("Atajo para guardar");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.V){
                onPaste();
                System.out.println("Atajo para pegar");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.C){
                onCopy();
                System.out.println("Atajo para copiar");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.B){
                onBold();
                System.out.println("Atajo para añadir negrita a una selección");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.I){
                onItalic();
                System.out.println("Atajo para añadir cursiva/itálica a una selección");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.Z){
                onUndo();
                System.out.println("Atajo para deshacer");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.Y){
                onRedo();
                System.out.println("Atajo para rehacer");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.D){
                dictationToggleMethod();
                System.out.println("Atajo para empezar dictado");
            } else if(event.isShortcutDown() && event.getCode() == KeyCode.L){
                handleVoiceCommand();
                System.out.println("Atajo para ejecutar un comando de voz");
            }


            event.consume();
        });
    }

    /**
     * Método auxiliar para transformar el texto a mayúsculas o minúsculas.
     * @param toUpperCase 'true' para convertir a mayúsculas, 'false' para minúsculas.
     */
    private void transformSelection(boolean toUpperCase) {
        IndexRange selection = textArea.getSelection();
        if (selection.getLength() > 0) {
            String selectedText = textArea.getSelectedText();
            String newText = toUpperCase ? selectedText.toUpperCase() : selectedText.toLowerCase();
            textArea.replaceText(selection.getStart(), selection.getEnd(), newText);
        } else {
            String currentText = textArea.getText();
            String newText = toUpperCase ? currentText.toUpperCase() : currentText.toLowerCase();
            textArea.replaceText(0, textArea.getLength(), newText);
        }
    }



    /**
     * Se llama automáticamente después de que se carga el archivo FXML.
     * Se utiliza para configurar listeners y estados iniciales. Aquí, establece un listener
     * para actualizar los contadores de palabras y caracteres en tiempo real mientras el usuario escribe.
     */
    @FXML
    public void initialize() {
        textArea.setWrapText(true);
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateCounts();
        });
        updateCounts(); // Initial count

        catchShortcuts();

        // Initialize NUI controller
        nuiController = new NuiController();
        nuiController.initializeListener(this::executeVoiceCommand, this::dictationCallBack);
    }


    /**
     * Calcula y actualiza las etiquetas que muestran el número de palabras, caracteres
     * (con y sin espacios). Se diseñó para dar feedback inmediato al usuario sobre la
     * longitud de su texto.
     */
    private void updateCounts() {
        String text = textArea.getText();
        int charCountWithSpaces = text.length();
        int charCountWithoutSpaces = text.replaceAll("\\s", "").length();
        String[] words = text.trim().split("\\s+");
        int wordCount = text.trim().isEmpty() ? 0 : words.length;

        //to-do ver si hay alguna manera de que no se vea la etiqueta entera
        wordsLabel.setText("Palabras: " + wordCount);
        charsLabel.setText("Caracteres: " + charCountWithSpaces);
        charsNoSpacesLabel.setText("Caracteres sin espacios: " + charCountWithoutSpaces);
    }


    /**
     * Se ejecuta al pulsar el botón de Deshacer.
     * Revierte la última acción realizada en el editor.
     */
    @FXML
    protected void onUndo() {
        textArea.undo();
    }

//TODO hacer este botón en el editor
    /**
     * Se ejecuta al pulsar el botón de Redo.
     * Rehace la última acción revertida en el editor.
     */
    @FXML
    protected void onRedo(){
        textArea.redo();
    }

    /**
     * Se ejecuta al pulsar el botón de Invertir.
     * Invierte el texto del área de texto o lo revierte a su estado original.
     */
    @FXML
    protected void onInvert() {
        if (!isInverted) {
            originalText = textArea.getText();
            String reversedText = new StringBuilder(originalText).reverse().toString();
            textArea.replaceText(reversedText);
            isInverted = true;
        } else {
            textArea.replaceText(originalText);
            isInverted = false;
        }
    }

    /**
     * Se ejecuta al pulsar el botón de Búsqueda (lupa).
     * Abre una ventana no modal para buscar y reemplazar texto.
     * Si la ventana ya está abierta, la trae al frente.
     */
    @FXML
    protected void onSearch() {
        if (findReplaceStage != null && findReplaceStage.isShowing()) {
            findReplaceStage.toFront();
            return;
        }

        findReplaceStage = new Stage();
        findReplaceStage.setTitle("Buscar y Reemplazar");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField findField = new TextField();
        findField.setPromptText("Buscar...");
        TextField replaceField = new TextField();
        replaceField.setPromptText("Reemplazar con...");

        grid.add(new Label("Buscar:"), 0, 0);
        grid.add(findField, 1, 0);
        grid.add(new Label("Reemplazar:"), 0, 1);
        grid.add(replaceField, 1, 1);

        Button findNextBtn = new Button("Buscar Siguiente");
        Button replaceBtn = new Button("Reemplazar");
        Button replaceAllBtn = new Button("Reemplazar Todo");

        Label messageLabel = new Label();

        findNextBtn.setOnAction(e -> findNext(findField.getText(), messageLabel));
        replaceBtn.setOnAction(e -> replaceNext(findField.getText(), replaceField.getText(), messageLabel));
        replaceAllBtn.setOnAction(e -> replaceAll(findField.getText(), replaceField.getText(), messageLabel));

        HBox hbButtons = new HBox(10);
        hbButtons.getChildren().addAll(findNextBtn, replaceBtn, replaceAllBtn);
        grid.add(hbButtons, 1, 2);
        grid.add(messageLabel, 1, 3);

        Scene scene = new Scene(grid);
        findReplaceStage.setScene(scene);
        findReplaceStage.show();

        findReplaceStage.setOnCloseRequest(e -> lastFindIndex = 0);

        findField.textProperty().addListener((obs, oldText, newText) -> {
            lastFindIndex = 0; // Reinicia posición de búsqueda
            if (newText.isEmpty()) {
                messageLabel.setText("");
            } else {
                String text = textArea.getText();
                Pattern pattern = Pattern.compile(Pattern.quote(newText), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                int count = 0;
                while (matcher.find()) {
                    count++;
                }
                if (count == 1) {
                    messageLabel.setText("1 coincidencia encontrada.");
                } else {
                    messageLabel.setText(count + " coincidencias encontradas.");
                }
            }
        });
    }

    /**
     *
     * Permite guardar un archivo en un directorio personalizado
     */
    @FXML
    protected void saveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo");

        // Filtros para archivos de texto y Markdown
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt");
        FileChooser.ExtensionFilter mdFilter = new FileChooser.ExtensionFilter("Markdown (*.md)", "*.md");
        fileChooser.getExtensionFilters().addAll(txtFilter, mdFilter);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter fw = new FileWriter(file)) {
                String contentToSave;
                // Comprueba la extensión seleccionada para guardar
                if (fileChooser.getSelectedExtensionFilter() == mdFilter) {
                    contentToSave = CommonMarkConverter.toCommonMark(textArea);
                } else {
                    contentToSave = textArea.getText();
                }
                fw.write(contentToSave);
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Archivo guardado correctamente en: " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo guardar el archivo: " + e.getMessage());
            }
        }
    }

    /**
     * Permite abrir archivos en el editor
     */
    @FXML
    protected void onOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        // Filtros de extensión actualizados
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de texto y Markdown", "*.txt", "*.md"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );

        ProgresoSimulado sim= new ProgresoSimulado(progresslabel);
        sim.start();
        File file = fileChooser.showOpenDialog(textArea.getScene().getWindow());
        if (file != null) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                if (file.getName().endsWith(".md")) {
                    CommonMarkConverter.applyCommonMark(content, textArea);
                    sim.finish();
                sim.restart();}
                else {
                    textArea.replaceText(content);
                    sim.finish();
                    sim.restart();
                }
            } catch (IOException e) {
                sim.fail();
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir el archivo: " + e.getMessage());
            }
        }


    }

    @FXML
    protected void onSave() {
        // For now, just call onSaveAs.
        onSaveAs();
    }

    @FXML
    protected void onSaveAs() {
        ProgresoSimulado sim = new ProgresoSimulado(progresslabel);
        sim.start();
        saveFile((Stage) textArea.getScene().getWindow());
        sim.finish();
        sim.restart();
    }

    /**
     * Muestra una alerta en pantalla.
     * @param alertType El tipo de alerta (información, error, etc.).
     * @param title El título de la ventana de alerta.
     * @param message El mensaje a mostrar.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Busca la siguiente ocurrencia del término de búsqueda.
     * @param searchTerm El texto a buscar.
     * @param messageLabel La etiqueta donde mostrar mensajes al usuario.
     */
    private void findNext(String searchTerm, Label messageLabel) {
        if (searchTerm.isEmpty()) return;

        String text = textArea.getText();
        Pattern pattern = Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        if (matcher.find(lastFindIndex)) {
            textArea.selectRange(matcher.start(), matcher.end());
            lastFindIndex = matcher.end();
            messageLabel.setText("");
        } else {
            lastFindIndex = 0; // Reinicia para la siguiente búsqueda
            messageLabel.setText("No más coincidencias.");
        }
    }

    /**
     * Reemplaza la selección actual si coincide con el término de búsqueda y busca la siguiente.
     * @param searchTerm El texto a buscar.
     * @param replacement El texto por el que reemplazar.
     * @param messageLabel La etiqueta para mostrar mensajes.
     */
    private void replaceNext(String searchTerm, String replacement, Label messageLabel) {
        if (searchTerm.isEmpty()) return;

        String selectedText = textArea.getSelectedText();
        if (selectedText.equalsIgnoreCase(searchTerm)) {
            textArea.replaceSelection(replacement);
            lastFindIndex = textArea.getSelection().getEnd();
        }
        findNext(searchTerm, messageLabel);
    }

    /**
     * Reemplaza todas las ocurrencias del término de búsqueda en el documento.
     * @param searchTerm El texto a buscar.
     * @param replacement El texto por el que reemplazar.
     * @param messageLabel La etiqueta para mostrar mensajes.
     */
        private void replaceAll(String searchTerm, String replacement, Label messageLabel) {
            if (searchTerm.isEmpty()) return;
    
            String text = textArea.getText();
            Pattern pattern = Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
    
            int count = 0;
            // Reemplaza todo de inicio a fin apara no tener conflicto con los indices
            java.util.List<IndexRange> ranges = new java.util.ArrayList<>();
            while(matcher.find()) {
                ranges.add(new IndexRange(matcher.start(), matcher.end()));
                count++;
            }
    
            for (int i = ranges.size() - 1; i >= 0; i--) {
                IndexRange range = ranges.get(i);
                textArea.replaceText(range, replacement);
            }
    
            if (count > 0) {
                messageLabel.setText("Se realizaron " + count + " reemplazos.");
            } else {
                messageLabel.setText("No se encontraron coincidencias.");
            }
            lastFindIndex = 0;
        }


    /**
     * Se invoca al pulsar el botón de comando de voz.
     * Inicia el proceso de escucha de comandos de voz a través del NuiController.
     */
    @FXML
    private void handleVoiceCommand() {
        System.out.println("handleVoiceCommand called");
        logVosk.setText("Escuchando comando...");
        voiceCommandButton.setStyle("-fx-background-color: red;");
        nuiController.listenForCommand();
    }

    /**
     * Ejecuta una acción en el editor basada en un comando de texto.
     * Este método es el callback para el NuiListener.
     * @param command El comando de texto reconocido (p. ej., "toggleBold").
     */
    private void executeVoiceCommand(String command) {
        // Normaliza el comando recibido: elimina espacios y lo convierte a minúsculas.
        String normalizedCommand = command.replaceAll("\\s+", "").toLowerCase();


        // Asegurarse de que la UI se actualiza en el hilo de la aplicación de JavaFX
        Platform.runLater(() -> {
            voiceCommandButton.setStyle("-fx-background-color: white;");
            logVosk.setText("Escucha finalizada. Comando: " + command);
            switch (normalizedCommand) {
                case "negrita":
                case "aplicarnegrita":
                    System.out.println("Executing command: onBold");
                    onBold();
                    break;
                case "cursiva":
                case "aplicarcursiva":
                    System.out.println("Executing command: onItalic");
                    onItalic();
                    break;
                case "mayúsculas":
                case "mayúscula":
                case "convertiramáyusculas":
                    System.out.println("Executing command: onUppercase");
                    onUppercase();
                    break;
                case "minúsculas":
                case "minúscula":
                case "convertiraminusculas":
                    System.out.println("Executing command: onLowercase");
                    onLowercase();
                    break;
                case "guardar":
                case "guardardocumento":
                    System.out.println("Executing command: onSave");
                    onSave();
                    break;
                case "abrir":
                case "abrirdocumento":
                    System.out.println("Executing command: onOpen");
                    onOpen();
                    break;
                case "copiar":
                    System.out.println("Executing command: onCopy");
                    onCopy();
                    break;
                case "pegar":
                    System.out.println("Executing command: onPaste");
                    onPaste();
                    break;
                case "deshacer":
                    System.out.println("Executing command: onUndo");
                    onUndo();
                    break;
                case "rehacer":
                    System.out.println("Executing command: onRedo");
                    onRedo();
                    break;
                case "limpiar":
                    System.out.println("Executing command: onClear");
                    onClear();
                    break;
                default:
                    logVosk.setText("Comando no reconocido: " + command);
                    System.out.println("Comando no existente: " + command);
                    break;
            }
        });

    }

    @FXML
    private void dictationCallBack(String TranscribedText) {
        System.out.println("Dictation callback received: " + TranscribedText);
        Platform.runLater(() -> {
            if (TranscribedText != null && !TranscribedText.isEmpty()) {
                logVosk.setText("Detectado: " + TranscribedText);
                textArea.appendText(TranscribedText + " ");
            }
        });
    }
}



