package org.example.editorapp.Nui;

import java.util.function.Consumer;

public class NuiController {

    private NuiListener listener;

    public void initializeListener(Consumer<String> commandCallback) {
        // NuiListener ahora carga el modelo en su constructor.
        this.listener = new NuiListener();
        this.listener.setCommandCallback(commandCallback);
    }

    public void listenForCommand() {
        if (listener != null) {
            listener.startListening();
        } else {
            System.err.println("NuiController: El Listener no está inicializado. No se puede escuchar por un comando.");
        }
    }

    public void stopListening() {
        if (listener != null) {
            listener.stopListening();
        }
    }
}
