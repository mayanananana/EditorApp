# Explicación del Método `toggleStyle`

## Explicación en Lenguaje Natural

Imagina que `toggleStyle` es como un interruptor de luz para el formato del texto que seleccionas (por ejemplo, para ponerlo en **negrita**).

1.  **Revisa si has seleccionado texto.** Si no has seleccionado nada, no hace nada.
2.  **Analiza tu selección.** Mira el texto que has resaltado y se pregunta: "¿Está *todo* este texto ya en negrita?".
3.  **Toma una decisión (el "interruptor"):**
    *   **Si la respuesta es SÍ** (todo el texto ya está en negrita), entonces "apaga la luz": le quita la negrita a toda la selección.
    *   **Si la respuesta es NO** (parte del texto no está en negrita, o nada lo está), entonces "enciende la luz": pone todo el texto seleccionado en negrita.
4.  **Aplica los cambios.** Una vez que decide qué hacer, aplica ese cambio a todo el texto que habías seleccionado, y lo ves reflejado en el editor.

En resumen, funciona como un botón de "todo o nada": o aplica un estilo a toda tu selección o se lo quita a toda tu selección, dependiendo de cómo se encuentre el texto al principio.

## Explicación en Lenguaje Técnico

El método `toggleStyle` implementa una lógica de interruptor para aplicar o remover un estilo CSS específico en el rango de texto seleccionado dentro de un control `InlineCssTextArea` de RichTextFX.

1.  **Obtención del Rango de Selección:**
    *   Primero, obtiene el `IndexRange` de la selección actual. Si la longitud de este es 0 (no hay texto seleccionado), el método termina su ejecución prematuramente para optimizar.

2.  **Análisis de Estilos Existentes (`StyleSpans`):**
    *   Se invoca a `textArea.getStyleSpans(start, end)` para obtener un objeto `StyleSpans<String>`. Esta es una estructura de datos inmutable que representa una secuencia de segmentos de texto (`StyleSpan`), donde cada segmento tiene un estilo CSS uniforme.
    *   Se utiliza un `stream` sobre estos `StyleSpans` con el operador terminal `allMatch`. Se evalúa si cada `StyleSpan` en la selección ya contiene la cadena del estilo a alternar (`styleToToggle`). El resultado booleano se almacena en `styleIsPresent`. Este paso es crucial, ya que determina si la operación será de adición o de eliminación del estilo.

3.  **Construcción de Nuevos Estilos (`StyleSpansBuilder`):**
    *   Se inicializa un `StyleSpansBuilder<String>`, que es una clase de utilidad mutable para construir un nuevo objeto `StyleSpans`.
    *   Se itera sobre cada `StyleSpan` del `StyleSpans` original.
    *   **Lógica de Alternancia (Toggle):**
        *   Si `styleIsPresent` es `true`, el nuevo estilo (`newStyle`) se genera eliminando la subcadena `styleToToggle` del estilo actual del `span`.
        *   Si `styleIsPresent` es `false`, se añade `styleToToggle` al estilo actual del `span`, asegurándose de no duplicarlo si ya existiera en ese `span` particular (caso de selecciones con estilos mixtos) y añadiendo un separador `;` si es necesario.
    *   **Normalización del CSS:** Se realiza una limpieza de la cadena `newStyle` para eliminar posibles punto y coma duplicados (`;;`), espacios en blanco redundantes y asegurar que no empiece o termine con un punto y coma. Esto mantiene la sintaxis del CSS limpia.
    *   Finalmente, el `newStyle` calculado y la longitud del `span` original se añaden al `StyleSpansBuilder`.

4.  **Aplicación de los Estilos Modificados:**
    *   Una vez que el bucle ha procesado todos los `spans`, se llama a `builder.create()` para generar el nuevo objeto `StyleSpans` inmutable.
    *   Este objeto se pasa a `textArea.setStyleSpans(selection.getStart(), newStyleSpans)`, que reemplaza los estilos del rango de la selección original por los nuevos, actualizando así la presentación visual del texto en la interfaz de usuario.
