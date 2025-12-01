package org.example.editorapp.ProgressLabel;

import javafx.application.Platform;

public class ProgresoSimulado {

    private ProgressLabel label=new ProgressLabel();
    private volatile boolean running = true;

    /**
     * Constructor para ProgresoSimulado.
     * Asocia una instancia de ProgressLabel para mostrar el progreso.
     * @param label La ProgressLabel que se actualizará con el progreso simulado.
     */
    public ProgresoSimulado(ProgressLabel label) {
        this.label = label;
    }

    /**
     * Inicia la simulación de progreso en un nuevo hilo.
     * El progreso se actualiza incrementalmente hasta un 90% para simular una tarea en curso,
     * y las actualizaciones de la UI se realizan en el hilo de la aplicación JavaFX.
     * Establece el estado de la etiqueta de progreso a WORKING.
     */
    public void start() {
        label.setProgress(0);
        label.setText(String.valueOf(State.WORKING));

        new Thread(() -> {
            double progress = 0;

            while (running && progress < 0.9) {
                progress += 0.02;

                double p = progress;
                Platform.runLater(() -> label.setProgress(p));

                try {
                    Thread.sleep(150);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }

    /**
     * Marca la simulación de progreso como completada.
     * Detiene el hilo de progreso, establece el progreso al 100% y cambia el estado de la etiqueta a FINISHED.
     */
    public void finish() {
        running = false;
        Platform.runLater(() -> {
            label.setProgress(1.0);
            label.setText(String.valueOf(State.FINISHED));
        });
    }

    /**
     * Marca la simulación de progreso como fallida.
     * Detiene el hilo de progreso, reinicia el progreso a 0 y cambia el estado de la etiqueta a ERROR.
     */
    public void fail() {
        running = false;
        Platform.runLater(() -> {
            label.setProgress(0);
            label.setText(String.valueOf(State.ERROR));
        });
    }

    /**
     * Reinicia el estado de la barra de progreso a IDLE (inactiva) y restablece el progreso a 0.
     * Esto se ejecuta en un nuevo hilo para no bloquear la UI mientras se asegura que el
     * progreso anterior ha terminado de procesarse.
     */
    public void restart() {
        new Thread(() -> {
            running = false; // Detener hilo actual

            try {
                Thread.sleep(500); // Pequeña espera para asegurar que finish() ya se ejecute
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                label.setProgress(0);
                label.setText(String.valueOf(State.IDLE));
            });
        }).start();
    }

}

