package org.example.editorapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación EditorApp.
 * Esta clase es el punto de entrada de la aplicación JavaFX.
 * Se encarga de cargar la interfaz de usuario desde el archivo FXML,
 * configurar la escena y mostrar la ventana principal.
 */
public class EditorMain extends Application {

    /**
     * El método principal para una aplicación JavaFX.
     * Este método es llamado por el framework de JavaFX después de que el método `launch()` es invocado.
     * Configura y muestra la ventana principal (Stage).
     *
     * @param stage El contenedor principal o ventana, proporcionado por el framework de JavaFX.
     */
    @Override
    public void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(EditorMain.class.getResource("Editor.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            // En caso de que el archivo FXML no se pueda cargar, se lanza una excepción.
            throw new RuntimeException(e);
        }
        stage.setTitle("Editor de Texto");
        stage.setScene(scene);
        stage.show();

    }

    /**
     * El punto de entrada principal para todas las aplicaciones Java.
     * Este método simplemente llama a `launch()`, que es el método de `Application`
     * que inicia el ciclo de vida de la aplicación JavaFX.
     *
     * @param args Argumentos de la línea de comandos (no se utilizan en esta aplicación).
     */
    public static void main(String[] args) {
        launch();
    }
}