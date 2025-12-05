package org.example.editorapp.Nui;

import java.util.function.Consumer;

public class NuiController {

    private NuiListener listener;

    public void initializeListener(Consumer<String> commandCallback, Consumer<String> dictationCallback) {
        this.listener = new NuiListener();
        this.listener.setCommandCallback(commandCallback);
        this.listener.setDictationCallback(dictationCallback);
    }

    public void listenForCommand() {
        if (listener != null) {
            listener.startListening();
        } else {
            System.err.println("NuiController: El Listener no está inicializado.");
        }
    }

    public void startDictation() {
        if (listener != null) {
            listener.startDictation();
        } else {
            System.err.println("NuiController: El Listener no está inicializado.");
        }
    }

    public void stopDictation() {
        if (listener != null) {
            listener.stopDictation();
        }
    }
}
