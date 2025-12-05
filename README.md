# EditorApp - Manual de Usuario Básico

Bienvenido a EditorApp, un editor de texto simple con funcionalidades básicas de formato y manipulación de texto.

## Características Principales

La interfaz de usuario de EditorApp consta de un área de texto principal y una barra de herramientas superior con varios botones:

*   **B** (Negrita): Aplica o quita el formato de negrita al texto seleccionado.
*   **I** (Cursiva): Aplica o quita el formato de cursiva al texto seleccionado.
*   **AA** (Mayúsculas): Convierte el texto seleccionado (o todo el texto si no hay selección) a mayúsculas.
*   **aa** (Minúsculas): Convierte el texto seleccionado (o todo el texto si no hay selección) a minúsculas.
*   **123** (Contar): Muestra un cuadro de diálogo con estadísticas del texto, incluyendo el número de caracteres (con y sin espacios) y el número de palabras.
*   **🔍** (Buscar y Reemplazar): Abre una ventana para buscar texto dentro del editor y reemplazarlo si se desea.
*   **↩** (Deshacer): Deshace la última acción realizada en el editor.
*   **⇅** (Invertir Texto): Invierte el orden de los caracteres del texto actual en el editor. Si se pulsa de nuevo, revierte el texto a su estado original.

En la parte inferior del área de texto, encontrarás botones adicionales:

*   **Limpiar**: Borra todo el contenido del área de texto.
*   **Copiar**: Copia el texto seleccionado (o todo el texto si no hay selección) al portapapeles, manteniendo el formato.
*   **Pegar**: Pega el contenido del portapapeles en la posición actual del cursor.

## Funcionalidades de Voz

EditorApp integra reconocimiento de voz para ofrecer control por manos libres y dictado en tiempo real.

*   **🎤 (Comando de Voz)**:
    *   **Función**: Escucha un único comando de voz y lo ejecuta.
    *   **Uso**: Presiona el botón del micrófono (🎤). La etiqueta de estado en la parte inferior indicará "Escuchando comando...". Di un comando y la aplicación lo ejecutará.
    *   **Comandos Disponibles**:
        *   `negrita`: Aplica o quita el estilo de negrita.
        *   `cursiva`: Aplica o quita el estilo de cursiva.
        *   `mayúsculas`: Convierte la selección a mayúsculas.
        *   `minúsculas`: Convierte la selección a minúsculas.
        *   `guardar`: Guarda el documento actual.
        *   `abrir`: Abre un nuevo documento.
        *   `copiar`: Copia el texto seleccionado.
        *   `pegar`: Pega texto desde el portapapeles.
        *   `deshacer`: Deshace la última acción.
        *   `rehacer`: Rehace la última acción.
    *   **Feedback**: La etiqueta de estado te confirmará el comando reconocido.

*   **REC (Dictado Continuo)**:
    *   **Función**: Transcribe tu voz a texto de forma continua en el editor.
    *   **Uso**:
        1.  Presiona el botón **REC** para iniciar. El botón se volverá rojo y la etiqueta de estado mostrará "Dictado iniciado...".
        2.  Habla de forma clara. El texto que la aplicación vaya reconociendo ("Detectado: ...") aparecerá en la etiqueta de estado y se añadirá al final del documento en el área de texto.
        3.  Vuelve a presionar el botón **REC** para detener el dictado. El botón volverá a su color original.

## Cómo Ejecutar la Aplicación

Para ejecutar EditorApp, necesitarás tener Java Development Kit (JDK) instalado en tu sistema. Esta aplicación utiliza Gradle para la gestión de dependencias y la construcción.

Sigue estos pasos:

1.  **Clonar el Repositorio** (si aún no lo has hecho):
    ```bash
    git clone <https://github.com/mayanananana/EditorApp.git>
    cd EditorApp
    ```

2.  **Compilar y Ejecutar con Gradle**:
    Abre una terminal en la raíz del proyecto (`EditorApp/`) y ejecuta el siguiente comando:
    
    **En Windows:**
    ```bash
    .\gradlew run
    ```
    
    **En macOS/Linux:**
    ```bash
    ./gradlew run
    ```

    Esto compilará la aplicación y la iniciará. La ventana del editor debería aparecer en tu pantalla.

¡Esperamos que disfrutes usando EditorApp!