package org.example.editorapp.Nui;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.json.JSONObject;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.function.Consumer;


public class NuiListener implements NuiListenerInterface {

    private Consumer<String> commandCallback;
    private static final String MODEL_PATH = "C:\\Users\\gomez\\IdeaProjects\\EditorApp\\Vosk";
    private Model model;
    private volatile boolean listening = false;

    public NuiListener() {
        try {
            // Carga el modelo de Vosk. La ruta debe ser la carpeta que contiene los archivos del modelo.
            this.model = new Model(MODEL_PATH);
        } catch (IOException e) {
            System.err.println("Error al cargar el modelo de Vosk. Asegúrate de que la carpeta 'model' exista en la raíz del proyecto y contenga el modelo descargado.");
            System.err.println("Consulta README_VOSK.md para más detalles.");
            // e.printStackTrace();
        }
    }

    @Override
    public void setCommandCallback(Consumer<String> callback) {
        this.commandCallback = callback;
    }

    @Override
    public void startListening() {
        if (model == null) {
            System.err.println("NuiListener: El modelo no está cargado. No se puede iniciar la escucha.");
            return;
        }
        if (listening) {
            System.out.println("NuiListener: Ya se está escuchando.");
            return;
        }

        listening = true;
        new Thread(this::listen).start();
    }

    private void listen() {
        try (Recognizer recognizer = new Recognizer(model, 16000)) {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Línea de audio no soportada.");
                listening = false;
                return;
            }

            try (TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info)) {
                microphone.open(format);
                microphone.start();

                System.out.println("NuiListener: Escuchando... Di un comando.");

                byte[] buffer = new byte[4096];
                int bytesRead;

                while (listening) {
                    bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        String result = recognizer.getResult();
                        // El resultado final no estará vacío
                        if (result != null && !result.isEmpty()) {
                            JSONObject jsonResult = new JSONObject(result);
                            String command = jsonResult.optString("text", "");
                            if (!command.isEmpty()) {
                                System.out.println("NuiListener: Comando reconocido: '" + command + "'");
                                if (commandCallback != null) {
                                    commandCallback.accept(command);
                                }
                                // Detener la escucha después de un comando exitoso
                                listening = false;
                            }
                        }
                    }
                }

                microphone.stop();
                System.out.println("NuiListener: Escucha detenida.");

            } catch (LineUnavailableException e) {
                System.err.println("Micrófono no disponible.");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("Ocurrió un error durante el reconocimiento de voz.");
            e.printStackTrace();
        } finally {
            listening = false;
        }
    }

    // Método para detener la escucha externamente si fuera necesario
    public void stopListening() {
        listening = false;
    }
}
