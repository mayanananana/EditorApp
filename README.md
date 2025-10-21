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