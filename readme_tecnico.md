# Documentación Técnica - Componente de Voz (Nui)

Este documento describe la arquitectura técnica y el funcionamiento del paquete `org.example.editorapp.Nui`, responsable de las funcionalidades de reconocimiento de voz en EditorApp.

## 1. Arquitectura General

El componente de voz está diseñado para desacoplar la lógica de reconocimiento de la lógica de la interfaz de usuario (UI). La interacción sigue el siguiente flujo:

**`EditorController`** (UI) → **`NuiController`** (Facade) → **`NuiListener`** (Lógica de Voz y Threading) → **Vosk SDK** (Reconocimiento)

Cuando `NuiListener` reconoce un texto, el flujo de datos se invierte usando callbacks (funciones Lambda):

**`NuiListener`** → Llama al `Consumer<String>` callback → **`EditorController`** (Ejecuta la acción en el hilo de la UI)

Esta arquitectura permite que el reconocimiento de voz, que es una operación intensiva y bloqueante (I/O de audio), se ejecute en un hilo separado sin congelar la interfaz de usuario.

---

## 2. El Directorio `Nui`

Ubicación: `src/main/java/org/example/editorapp/Nui/`

Este paquete encapsula toda la lógica de interacción con el motor de reconocimiento de voz Vosk.

### NuiListener.java

Es la clase principal y el núcleo del sistema.

- **Responsabilidades**:
    - **Cargar el Modelo Vosk**: En su constructor, carga el modelo de lenguaje desde una ruta local (`C:\Users\gomez\IdeaProjects\EditorApp\Vosk`).
    - **Gestionar Hilos (Threading)**: Cada vez que se inicia una escucha (`startListening` o `startDictation`), crea y lanza un nuevo `Thread` para manejar la captura de audio y el reconocimiento. Esto es fundamental para no bloquear el hilo de la aplicación JavaFX.
    - **Captura de Audio**: Utiliza `javax.sound.sampled` para abrir una línea con el micrófono, capturar el audio en el formato requerido por Vosk (16000 Hz, 16-bit, mono) y pasarlo al reconocedor.
    - **Distinguir Modos de Escucha**:
        1.  **Modo Comando Único**: Se activa con `startListening()`. Después de reconocer una frase, invoca el `commandCallback`, y la escucha se detiene automáticamente.
        2.  **Modo Dictado Continuo**: Se activa con `startDictation()`. Reconoce texto de forma continua e invoca el `dictationCallback` por cada fragmento detectado. Continúa hasta que se llama a `stopDictation()`.
    - **Comunicación con la UI**: Utiliza dos `Consumer<String>` (callbacks) para enviar el texto reconocido de vuelta al `EditorController`.

### NuiController.java

Actúa como una **fachada (Facade)** o un puente entre `EditorController` y `NuiListener`.

- **Responsabilidades**:
    - **Desacoplamiento**: Evita que `EditorController` tenga que conocer los detalles de implementación de `NuiListener`. `EditorController` solo interactúa con esta clase.
    - **Inicialización**: Crea una instancia de `NuiListener`.
    - **Gestión de Callbacks**: Proporciona el método `initializeListener` que permite al `EditorController` "registrar" sus propios métodos como callbacks para los eventos de reconocimiento de voz.

### NuiListenerInterface.java

Es una interfaz que define el contrato que `NuiListener` debe cumplir.

- **Propósito**: Define los métodos públicos esenciales para controlar el oyente de voz (`startListening`, `stopDictation`, etc.) y para establecer los callbacks. Esto permite, en teoría, intercambiar la implementación de `NuiListener` por otra sin tener que cambiar el código en `NuiController`.

### NuiCommand.java

Es una enumeración (`enum`) que contiene una lista de posibles comandos de voz.

- **Estado Actual**: **Legacy / No utilizado**. Aunque presente, la implementación actual no utiliza este `enum`. En su lugar, el `NuiListener` envía el texto reconocido como un `String` crudo al `EditorController`, que es donde se realiza la comparación y se decide qué acción tomar. Podría ser reutilizado en el futuro para un sistema de comandos más robusto.

---

## 3. Integración en `EditorController`

`EditorController` es el responsable de conectar la UI con el sistema de voz y de actuar sobre los resultados.

### Métodos Clave

#### `initialize()`
Aquí se establece la conexión inicial.
```java
@FXML
public void initialize() {
    // ... otros inicializadores ...
    nuiController = new NuiController();
    nuiController.initializeListener(this::executeVoiceCommand, this::dictationCallBack);
}
```
- Se crea una instancia de `NuiController`.
- Se le pasan dos referencias a métodos del propio `EditorController`: 
    1.  `this::executeVoiceCommand`: Será el callback para los comandos únicos.
    2.  `this::dictationCallBack`: Será el callback para el dictado continuo.

#### `handleVoiceCommand()` y `dictationToggleMethod()`
Son los métodos de acción de los botones de la UI que inician los procesos de escucha.
```java
@FXML
private void handleVoiceCommand() {
    logVosk.setText("Escuchando comando...");
    nuiController.listenForCommand(); // Inicia la escucha de comando único
}

@FXML
protected void dictationToggleMethod() {
    if(!isDictationRunning) {
        // ... (cambia estilo del botón) ...
        nuiController.startDictation(); // Inicia el dictado continuo
    } else {
        // ... (revierte estilo del botón) ...
        nuiController.stopDictation(); // Detiene el dictado
    }
}
```

#### `executeVoiceCommand(String command)`
Este es el **callback** para comandos únicos. Recibe el texto reconocido desde `NuiListener`.
```java
private void executeVoiceCommand(String command) {
    String normalizedCommand = command.replaceAll("\\s+", "").toLowerCase();

    Platform.runLater(() -> {
        logVosk.setText("Escucha finalizada. Comando: " + command);
        switch (normalizedCommand) {
            case "negrita":
                onBold();
                break;
            // ... otros casos ...
        }
    });
}
```
- **`Platform.runLater()`**: Es fundamental. El callback es invocado desde el hilo de `NuiListener`. Todas las actualizaciones de la UI (como cambiar el texto de `logVosk` o aplicar un estilo) **deben** ser envueltas en `Platform.runLater()` para asegurar que se ejecuten en el hilo de la aplicación JavaFX, evitando errores de concurrencia.

#### `dictationCallBack(String TranscribedText)`
Este es el **callback** para el dictado continuo.
```java
@FXML
private void dictationCallBack(String TranscribedText) {
    Platform.runLater(() -> {
        if (TranscribedText != null && !TranscribedText.isEmpty()) {
            logVosk.setText("Detectado: " + TranscribedText);
            textArea.appendText(TranscribedText + " ");
        }
    });
}
```
- Al igual que `executeVoiceCommand`, utiliza `Platform.runLater()` para actualizar de forma segura la etiqueta `logVosk` y añadir el texto transcrito al `textArea`.
