
package org.example.editorapp;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpan;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

/**
 * Clase de utilidad para convertir entre CommonMark y el formato de texto con estilo
 * utilizado en el InlineCssTextArea.
 *
 * <p><b>Dependencia Requerida:</b></p>
 * <p>Para que esta clase funcione, necesitas añadir la siguiente dependencia a tu archivo `build.gradle`:</p>
 * <pre>
 * dependencies {
 *     implementation 'org.commonmark:commonmark:0.21.0'
 *     // ... otras dependencias
 * }
 * </pre>
 *
 * <p><b>Uso:</b></p>
 * <p>Para cargar un archivo CommonMark en el editor:</p>
 * <pre>
 * String markdown = // ... lee el contenido del archivo .md
 * CommonMarkConverter.applyCommonMark(markdown, textArea);
 * </pre>
 *
 * <p>Para guardar el contenido del editor como CommonMark:</p>
 * <pre>
 * String markdown = CommonMarkConverter.toCommonMark(textArea);
 * // ... guarda el string markdown en un archivo .md
 * </pre>
 */
public class CommonMarkConverter {

    /**
     * Aplica el formato de un texto en CommonMark a un InlineCssTextArea.
     *
     * @param commonMarkText El texto en formato CommonMark.
     * @param textArea El área de texto donde se aplicará el formato.
     */
    public static void applyCommonMark(String commonMarkText, InlineCssTextArea textArea) {
        textArea.clear();
        Parser parser = Parser.builder().build();
        Node document = parser.parse(commonMarkText);

        document.accept(new AbstractVisitor() {
            @Override
            public void visit(Text text) {
                textArea.appendText(text.getLiteral());
                super.visit(text);
            }

            @Override
            public void visit(Emphasis emphasis) {
                int start = textArea.getLength();
                super.visit(emphasis);
                int end = textArea.getLength();
                StyleSpans<String> spans = textArea.getStyleSpans(start, end);
                StyleSpansBuilder<String> builder = new StyleSpansBuilder<>();
                for (StyleSpan<String> span : spans) {
                    String style = span.getStyle();
                    if (!style.contains("-fx-font-style: italic")) {
                        style = style + (style.isEmpty() ? "" : ";") + "-fx-font-style: italic";
                    }
                    builder.add(style, span.getLength());
                }
                textArea.setStyleSpans(start, builder.create());
            }

            @Override
            public void visit(StrongEmphasis strongEmphasis) {
                int start = textArea.getLength();
                super.visit(strongEmphasis);
                int end = textArea.getLength();
                StyleSpans<String> spans = textArea.getStyleSpans(start, end);
                StyleSpansBuilder<String> builder = new StyleSpansBuilder<>();
                for (StyleSpan<String> span : spans) {
                    String style = span.getStyle();
                    if (!style.contains("-fx-font-weight: bold")) {
                        style = style + (style.isEmpty() ? "" : ";") + "-fx-font-weight: bold";
                    }
                    builder.add(style, span.getLength());
                }
                textArea.setStyleSpans(start, builder.create());
            }

            @Override
            public void visit(Paragraph paragraph) {
                // Agrega un salto de línea después de cada párrafo, excepto el último.
                super.visit(paragraph);
                if (paragraph.getNext() != null) {
                    textArea.appendText("\n");
                }
            }
        });
    }

    /**
     * Convierte el contenido de un InlineCssTextArea a una cadena en formato CommonMark.
     *
     * <p><b>Nota:</b> Esta es una implementación de demostración y puede no manejar
     * correctamente estilos anidados o complejos. Para una solución más robusta,
     * sería necesario un análisis de rangos de estilo más sofisticado.</p>
     *
     * @param textArea El área de texto con el contenido a convertir.
     * @return Una cadena de texto en formato CommonMark.
     */
    public static String toCommonMark(InlineCssTextArea textArea) {
        StringBuilder markdown = new StringBuilder();
        String text = textArea.getText();
        String lastStyle = "";

        for (int i = 0; i < text.length(); i++) {
            String currentStyle = textArea.getStyleAtPosition(i);
            boolean isBold = currentStyle.contains("-fx-font-weight: bold");
            boolean wasBold = lastStyle.contains("-fx-font-weight: bold");
            boolean isItalic = currentStyle.contains("-fx-font-style: italic");
            boolean wasItalic = lastStyle.contains("-fx-font-style: italic");

            // Fin de la negrita
            if (wasBold && !isBold) {
                markdown.append("**");
            }
            // Fin de la cursiva
            if (wasItalic && !isItalic) {
                markdown.append("*");
            }

            // Inicio de la negrita
            if (isBold && !wasBold) {
                markdown.append("**");
            }
            // Inicio de la cursiva
            if (isItalic && !wasItalic) {
                markdown.append("*");
            }

            markdown.append(text.charAt(i));
            lastStyle = currentStyle;
        }

        // Cierra cualquier estilo que quede abierto al final del texto
        if (lastStyle.contains("-fx-font-weight: bold")) {
            markdown.append("**");
        }
        if (lastStyle.contains("-fx-font-style: italic")) {
            markdown.append("*");
        }

        return markdown.toString();
    }
}
