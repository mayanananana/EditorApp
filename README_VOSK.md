# Configuración del Reconocimiento de Voz con Vosk

Para que la funcionalidad de comandos por voz funcione, es necesario descargar y configurar un **modelo de lenguaje** de Vosk. Sigue estos pasos:

## 1. Descargar el Modelo de Lenguaje

La aplicación está configurada para usar un modelo de lenguaje en español. Se recomienda usar un modelo pequeño para empezar.

- **Enlace de descarga**: [vosk-model-small-es-0.42.zip](https://alphacephei.com/vosk/models/vosk-model-small-es-0.42.zip) (aproximadamente 45 MB)

Haz clic en el enlace para descargar el archivo `.zip`.

## 2. Crear el Directorio del Modelo

Una vez descargado el archivo, necesitas colocar su contenido en el lugar correcto dentro del proyecto.

1.  Ve a la raíz de este proyecto (`EditorApp`).
2.  Crea una nueva carpeta y llámala exactamente `model`.
3.  Descomprime el contenido del archivo `vosk-model-small-es-0.42.zip` que descargaste **dentro** de la carpeta `model`.

Al final, la estructura de tu proyecto debería verse así:

```
EditorApp/
├── model/
│   ├── am/
│   ├── conf/
│   └── graph/
│   └── ... (otros archivos y carpetas del modelo)
├── src/
├── build.gradle
└── ... (el resto de los archivos del proyecto)
```

## 3. Permisos del Micrófono

La primera vez que uses la función de voz, es posible que tu sistema operativo te pida permiso para que la aplicación acceda a tu micrófono. Asegúrate de concederle el permiso.

---

Una vez completados estos pasos, la aplicación podrá cargar el modelo, escuchar tu voz a través del micrófono y transcribir tus comandos.