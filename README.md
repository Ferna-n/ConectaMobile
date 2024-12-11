# ConectaMobile

ConectaMobile es una aplicación de mensajería moderna desarrollada en Android Studio utilizando Java. Su objetivo principal es facilitar la comunicación en tiempo real entre usuarios, integrando tecnologías como Firebase y MQTT para garantizar la conectividad y la estabilidad del sistema.

## Características Principales

- **Mensajería en tiempo real**: Intercambio instantáneo de mensajes entre usuarios utilizando el protocolo MQTT.
- **Autenticación segura**: Manejo de usuarios autenticados mediante Firebase Authentication.
- **Almacenamiento de datos**: Uso de Firebase Realtime Database para la persistencia de los mensajes y datos de los usuarios.
- **Gestor de perfiles**: Posibilidad de ver, editar y cargar una foto de perfil mediante Firebase Storage.
- **Interfaz amigable**: Diseño intuitivo y moderno para mejorar la experiencia del usuario.

## Tecnologías Utilizadas

- **Android Studio**: IDE principal para el desarrollo.
- **Java**: Lenguaje de programación principal.
- **Firebase**: Proveedor de servicios backend.
  - Authentication
  - Realtime Database
  - Storage
- **HiveMQ**: Bróker MQTT para la comunicación en tiempo real.
- **Picasso**: Librería para la carga de imágenes.

## Estructura del Proyecto

```
ConectaMobile/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/conectamobile/
│   │   │   │   ├── activities/  # Actividades principales (ChatActivity, ProfileActivity)
│   │   │   │   ├── adapters/    # Adaptadores para RecyclerViews
│   │   │   │   ├── models/      # Clases modelo (Message, User)
│   │   │   │   ├── utils/       # Clases utilitarias
│   │   │   ├── res/             # Recursos de la aplicación
│   │   │   │   ├── layout/      # Diseño de interfaces
│   │   │   │   ├── drawable/    # Recursos gráficos
├── build.gradle
├── README.md
```

## Instalación y Configuración

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/tu-usuario/ConectaMobile.git
   ```

2. **Abrir en Android Studio**:
   - Importar el proyecto en Android Studio.

3. **Configurar Firebase**:
   - Crear un proyecto en Firebase.
   - Agregar la aplicación de Android en Firebase y descargar el archivo `google-services.json`.
   - Colocar el archivo en la carpeta `app/`.
   - Habilitar Authentication, Realtime Database y Storage en la consola de Firebase.

4. **Configurar las reglas de Firebase**:
   ```json
   {
     "rules": {
       ".read": "auth != null",
       ".write": "auth != null"
     }
   }
   ```

5. **Ejecutar la aplicación**:
   - Conectar un dispositivo o iniciar un emulador.
   - Ejecutar el proyecto desde Android Studio.

## Funcionalidades

### Mensajería en Tiempo Real
Los usuarios pueden enviar y recibir mensajes en tiempo real utilizando el protocolo MQTT. Los mensajes también se almacenan en Firebase Realtime Database para garantizar la persistencia de los datos.

### Gestor de Perfiles
Los usuarios pueden:
- Editar su nombre y correo electrónico.
- Subir una foto de perfil, que se almacena en Firebase Storage.

### Seguridad
- Firebase Authentication asegura que solo los usuarios registrados tengan acceso a la aplicación.
- Reglas de Firebase Database y Storage protegen los datos de los usuarios.

## Requisitos del Sistema

- Android 6.0 (API 23) o superior.
- Conexión a Internet para la sincronización de datos.

## Licencia

Este proyecto está bajo la licencia MIT. Para más información, consulta el archivo LICENSE en este repositorio.

## Contribuciones

Las contribuciones son bienvenidas. Por favor, sigue los siguientes pasos:
1. Haz un fork del repositorio.
2. Crea una rama para tu funcionalidad (`git checkout -b feature/nueva-funcionalidad`).
3. Realiza un pull request describiendo los cambios realizados.

---

Este archivo README fue creado para proporcionar una descripción clara y profesional del proyecto ConectaMobile. Si tienes alguna sugerencia, no dudes en compartirla.

