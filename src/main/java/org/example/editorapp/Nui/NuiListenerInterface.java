package org.example.editorapp.Nui;

import java.util.function.Consumer;

public interface NuiListenerInterface {
    void setCommandCallback(Consumer<String> callback);
    void startListening();
}
