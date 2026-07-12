# TravelGo - Ecosistema de Microservicios

### Módulos del proyecto
* **travelgo-gateway** (puerto `8080`): API Gateway (Spring Cloud Gateway), único punto de entrada del ecosistema.
* **travelgo-spa-api** (puerto `8081`): paquetes turísticos, reservas y autenticación (JWT).
* **travelgo-user-service** (puerto `8082`): gestión de usuarios.

### ¿Qué tiene el proyecto?
* **Arquitectura CSR**: Controller / Service / Repository separados en cada microservicio.
* **Persistencia**: SQLite + Flyway (migraciones versionadas, `ddl-auto=validate`).
* **Seguridad**: JWT obligatorio en la mayoría de endpoints (`SecurityConfig`). Rutas públicas: `/auth/**`, `GET /api/packages/**`, Swagger y `/actuator/health`.
* **Comunicación entre servicios**: `travelgo-spa-api` consulta a `travelgo-user-service` vía Feign Client, a través del Gateway.

### Cómo levantar el ecosistema completo
Requiere Java 17+. Levanta los 3 módulos en este orden (cada uno con `mvn spring-boot:run` o desde su clase `*Application.java`):
1. `travelgo-user-service` → puerto `8082`
2. `travelgo-spa-api` → puerto `8081`
3. `travelgo-gateway` → puerto `8080`

Todo el tráfico externo debe pasar por el Gateway (`http://localhost:8080`); los puertos `8081`/`8082` son internos.

### Endpoints principales (a través del Gateway, puerto 8080)
* `POST /auth/login`: autenticación, retorna JWT.
* `GET /api/packages`, `GET /api/packages/{id}`, `GET /api/packages/search`, `POST /api/packages`, `PUT /api/packages/{id}`, `DELETE /api/packages/{id}`.
* `GET /api/reservations`, `GET /api/reservations/user/{userId}`, `POST /api/reservations`, `DELETE /api/reservations/{id}`.
* `GET /api/users`, `GET /api/users/{id}`, `POST /api/users`, `DELETE /api/users/{id}`.
* `GET /doc/swagger-ui.html`: documentación interactiva de `travelgo-spa-api`.

## Levantar todo con Docker (alternativa recomendada)

   Con Docker Desktop instalado, desde la raíz del repo:
```powershell
   docker compose up --build
```
   Esto compila y levanta los 10 microservicios juntos, en el orden correcto, sin necesidad de abrir 10 terminales a mano. El Gateway queda disponible en `http://localhost:8080`.