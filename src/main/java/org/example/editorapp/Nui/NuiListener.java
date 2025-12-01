package org.example.editorapp.Nui;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.example.editorapp.EditorController;

public class NuiListener extends VBox implements NuiListenerInterface {
    EditorController dc=  new EditorController();


    @Override
    public void onCommand(NuiCommand cmd, String payload) {
        switch (cmd) {
            case ABRIR_DOCUMENTO -> abrirDocumento();
            case GUARDAR_DOCUMENTO -> guardarDocumento();

            case APLICAR_NEGRITA -> aplicarNegrita();
            case APLICAR_CURSIVA -> aplicarCursiva();

        }

    }

    private void aplicarCursiva() {
    }

    private void aplicarNegrita() {
    }

    private void guardarDocumento() {
    }

    private void abrirDocumento() {
    }

    private void mostrarEnBarraEstado(String s) {
    }
}
