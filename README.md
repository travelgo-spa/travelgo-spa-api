# TravelGo SPA - API de Gestión de Viajes

### ¿Qué tiene el proyecto?
* **Arquitectura CSR**: Todo está ordenado por controladores, servicios y repositorios para que el código sea fácil de leer.
* **Entidades**: Manejamos Paquetes de Viaje, Usuarios y Reservas usando Lombok para que no haya código de más.
* **Seguridad**: Está configurado para permitir pruebas rápidas desde Postman sin bloqueos de tokens por ahora.

### Cómo hacerlo correr
1. Abre el proyecto en VS Code.
2. Asegúrate de tener Java 17 o superior.
3. Dale a "Run" en `BibliotecaApplication.java`.
4. El servidor levantará en el puerto `8081`.

### 🔍 Endpoints principales
* `GET /api/packages`: Para ver todos los tours disponibles.
* `POST /api/users`: Para registrar un nuevo cliente.
* `GET /h2-console`: Para ver la base de datos
