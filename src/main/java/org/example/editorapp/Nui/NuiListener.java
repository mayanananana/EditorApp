package org.example.editorapp.Nui;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.json.JSONObject;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.function.Consumer;

public class NuiListener implements NuiListenerInterface {

    private Consumer<String> commandCallback;
    private Consumer<String> dictationCallback;
    private static final String MODEL_PATH = "C:\\Users\\gomez\\IdeaProjects\\EditorApp\\Vosk";
    private Model model;
    private volatile boolean listening = false;
    private Thread listeningThread;

    public NuiListener() {
        try {
            this.model = new Model(MODEL_PATH);
        } catch (IOException e) {
            System.err.println("Error al cargar el modelo de Vosk. Asegúrate de que la carpeta 'model' exista en la raíz del proyecto y contenga el modelo descargado.");
            System.err.println("Consulta README_VOSK.md para más detalles.");
        }
    }

    @Override
    public void setCommandCallback(Consumer<String> callback) {
        this.commandCallback = callback;
    }

    @Override
    public void setDictationCallback(Consumer<String> callback) {
        this.dictationCallback = callback;
    }

    @Override
    public void startListening() {
        if (model == null || listening) return;
        listening = true;
        listeningThread = new Thread(this::listenForCommand);
        listeningThread.start();
    }

    @Override
    public void startDictation() {
        if (model == null || listening) return;
        listening = true;
        listeningThread = new Thread(this::dictate);
        listeningThread.start();
    }

    @Override
    public void stopDictation() {
        listening = false;
        if (listeningThread != null) {
            listeningThread.interrupt();
        }
    }

    private void listenForCommand() {
        // This method is for single command recognition
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            setupMicrophone(recognizer, false);
        } catch (Exception e) {
            System.err.println("Ocurrió un error durante el reconocimiento de un solo comando.");
            e.printStackTrace();
        } finally {
            listening = false;
        }
    }

    private void dictate() {
        // This method is for continuous dictation
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            setupMicrophone(recognizer, true);
        } catch (Exception e) {
            System.err.println("Ocurrió un error durante el dictado.");
            e.printStackTrace();
        } finally {
            listening = false;
        }
    }

    private void setupMicrophone(Recognizer recognizer, boolean isDictation) {
        AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.err.println("Línea de audio no soportada.");
            return;
        }

        try (TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info)) {
            microphone.open(format);
            microphone.start();
            System.out.println(isDictation ? "Dictado iniciado..." : "Escuchando comando...");

            byte[] buffer = new byte[4096];
            int bytesRead;

            while (listening) {
                bytesRead = microphone.read(buffer, 0, buffer.length);
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    String result = recognizer.getResult();
                    if (result != null && !result.isEmpty()) {
                        String text = new JSONObject(result).optString("text", "");
                        if (!text.isEmpty()) {
                            if (isDictation) {
                                if (dictationCallback != null) dictationCallback.accept(text + " ");
                            } else {
                                if (commandCallback != null) commandCallback.accept(text);
                                stopDictation(); // Stop after one command
                            }
                        }
                    }
                }
            }
            microphone.stop();
            System.out.println("Escucha finalizada.");

        } catch (LineUnavailableException e) {
            System.err.println("Micrófono no disponible.");
            e.printStackTrace();
        }
    }
}
