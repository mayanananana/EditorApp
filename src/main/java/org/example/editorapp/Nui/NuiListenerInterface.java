package org.example.editorapp.Nui;

import java.util.function.Consumer;

public interface NuiListenerInterface {
    void setCommandCallback(Consumer<String> callback);
    void startListening();

    void setDictationCallback(Consumer<String> callback);
    void startDictation();
    void stopDictation();
}
