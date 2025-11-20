package org.example.editorapp.models;

import javafx.application.Platform;

public class ProgresoSimulado {

    private ProgressLabel label=new ProgressLabel();
    private volatile boolean running = true;

    public ProgresoSimulado(ProgressLabel label) {
        this.label = label;
    }

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

    public void finish() {
        running = false;
        Platform.runLater(() -> {
            label.setProgress(1.0);
            label.setText(String.valueOf(State.FINISHED));
        });
    }

    public void fail() {
        running = false;
        Platform.runLater(() -> {
            label.setProgress(0);
            label.setText(String.valueOf(State.ERROR));
        });
    }

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

