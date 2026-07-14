# TravelGo

Este es el proyecto de TravelGo, una agencia de viajes armada como un ecosistema de microservicios con Spring Boot. La idea de fondo es simple: en vez de tener una sola aplicación gigante que hace todo, dividimos las responsabilidades en varios servicios chicos e independientes, cada uno con su propia base de datos, que se comunican entre sí cuando lo necesitan.

Todo el tráfico de afuera entra por un solo lugar (el Gateway), que se encarga de redirigir cada petición al microservicio que corresponde.

## Los 10 microservicios

| Microservicio | Puerto | Qué hace |
|---|---|---|
| `travelgo-gateway` | 8080 | Punto de entrada único. No tiene lógica propia, solo redirige el tráfico a los demás servicios. |
| `travelgo-user-service` | 8082 | Maneja los usuarios (crear, buscar, borrar). |
| `travelgo-spa-api` | 8081 | El servicio más grande: paquetes turísticos, reservas, y el login (JWT). |
| `travelgo-review-service` | 8083 | Reseñas de paquetes. Un usuario puede calificar un paquete del 1 al 5 y dejar un comentario. |
| `travelgo-destination-service` | 8084 | Catálogo de destinos (ciudades/países) que la agencia ofrece. |
| `travelgo-wishlist-service` | 8085 | Lista de deseos: cada usuario puede guardar paquetes que le interesan para más adelante. |
| `travelgo-payment-service` | 8086 | Registra los pagos hechos por una reserva. |
| `travelgo-notification-service` | 8087 | Guarda las notificaciones que se le mandan a un usuario (y si ya las leyó o no). |
| `travelgo-promotion-service` | 8088 | Códigos de descuento, con fecha de vencimiento. |
| `travelgo-support-service` | 8089 | Tickets de soporte al cliente, con un flujo de estados (abierto → en progreso → cerrado). |

## Cómo está armado cada uno

Todos siguen la misma estructura por dentro (Controller → Service → Repository), y todos usan:
- SQLite como base de datos, cada uno con la suya propia (no comparten datos entre sí).
- Flyway para las migraciones, así el esquema de la base de datos queda versionado y no hay que crear las tablas a mano.
- Bean Validation para validar lo que llega en los requests.
- Un manejador de errores centralizado que devuelve siempre el mismo formato de respuesta cuando algo falla.
- Swagger para poder ver y probar los endpoints desde el navegador (`/doc/swagger-ui.html` en cada uno).

Algunos se hablan entre sí usando Feign (la forma en que un microservicio le pregunta algo a otro por HTTP, sin tener que escribir el código de la llamada a mano):
- `travelgo-review-service` le pregunta a `spa-api` si el paquete existe antes de guardar una reseña.
- `travelgo-wishlist-service` hace lo mismo antes de agregar algo a la wishlist.
- `travelgo-spa-api` le pregunta a `travelgo-user-service` si el usuario existe antes de crear una reserva.
- Todas estas llamadas pasan por el Gateway, igual que si vinieran de afuera.

## Persistencia y modelo de datos

Cada microservicio maneja su propia base de datos, sin compartir tablas entre sí. La única relación JPA real de todo el proyecto es entre `Reservation` y `TravelPackage` (dentro de `spa-api`), porque son las únicas dos entidades que viven en la misma base de datos:
- `Reservation` tiene un `@ManyToOne` hacia `TravelPackage`.
- `TravelPackage` tiene el lado inverso, un `@OneToMany` hacia sus reservas, para poder navegar en ambos sentidos.

Todo lo demás que cruza microservicios (por ejemplo, `Review.packageId` o `Reservation.userId`) es solo un número (`Long`), validado en tiempo real contra el otro servicio vía Feign — no puede existir una foreign key real entre bases de datos separadas.

## Las reglas de negocio de cada uno

- **spa-api**: el precio de un paquete no puede pasar los $10.000.000. Una reserva necesita que el usuario exista (se valida contra `user-service`) y que el paquete exista de verdad (relación JPA real, no solo un número suelto).
- **user-service**: no puede haber dos usuarios con el mismo username o el mismo email.
- **review-service**: la calificación tiene que ser entre 1 y 5, y un mismo usuario no puede reseñar el mismo paquete dos veces.
- **destination-service**: no puede haber dos destinos con el mismo nombre.
- **wishlist-service**: no se puede agregar el mismo paquete dos veces a la wishlist de un usuario.
- **payment-service**: el monto tiene que ser positivo, y no se puede pagar dos veces la misma reserva.
- **promotion-service**: no puede haber dos códigos de promoción iguales, y un código vencido no se puede usar.
- **support-service**: el estado de un ticket solo puede avanzar (abierto → en progreso → cerrado), nunca puede retroceder.

## Cómo levantar todo

### Opción 1: con Docker (recomendado, un solo comando)

Necesitas Docker Desktop instalado. Desde la raíz del repositorio:

```powershell
docker compose up --build
```

Esto compila las 10 imágenes desde el código fuente y levanta los 10 contenedores juntos, en el orden correcto, sin tener que abrir 10 terminales a mano. El Gateway queda disponible en `http://localhost:8080`, y cada microservicio también es accesible directo en su puerto individual (8081 a 8089) para pruebas aisladas.

Cada microservicio tiene su propio `Dockerfile` (multi-stage: compila con Maven y corre con un JRE liviano), y el `docker-compose.yml` en la raíz orquesta los 10 contenedores, asignándoles nombre de host interno para que se comuniquen entre sí.

### Opción 2: manual, sin Docker

Necesitas Java 17 y Maven. Cada microservicio es un proyecto independiente, así que hay que levantarlos todos por separado (una terminal por cada uno), corriendo `mvn spring-boot:run` adentro de cada carpeta.

El orden importa un poco, para que las llamadas entre servicios no fallen al inicio:

1. `travelgo-user-service`
2. `travelgo-spa-api`
3. `travelgo-review-service`
4. `travelgo-destination-service`
5. `travelgo-wishlist-service`
6. `travelgo-payment-service`
7. `travelgo-notification-service`
8. `travelgo-promotion-service`
9. `travelgo-support-service`
10. `travelgo-gateway` (este va al final, porque redirige hacia los demás)

Una vez que todos están arriba, todo el tráfico de afuera entra por el Gateway: `http://localhost:8080`. Los demás puertos (8081 a 8089) son para uso interno o para probar cada servicio suelto mientras se desarrolla.

## Endpoints principales (a través del Gateway)

- `POST /auth/login` → login, devuelve un JWT.
- `GET/POST/PUT/DELETE /api/packages` → paquetes turísticos.
- `GET/POST/DELETE /api/reservations` → reservas.
- `GET/POST/DELETE /api/users` → usuarios.
- `GET/POST/DELETE /api/reviews` → reseñas.
- `GET/POST/DELETE /api/destinations` → destinos.
- `GET/POST/DELETE /api/wishlist` → lista de deseos.
- `GET/POST/DELETE /api/payments` → pagos.
- `GET/POST/PATCH /api/notifications` → notificaciones.
- `GET/POST/DELETE /api/promotions` → códigos de descuento.
- `GET/POST/PATCH /api/support-tickets` → tickets de soporte.

Cada microservicio tiene su propio Swagger corriendo en `http://localhost:<puerto>/doc/swagger-ui.html` (por ejemplo, el de `spa-api` sería `http://localhost:8081/doc/swagger-ui.html`).

## Pruebas unitarias

Los 10 microservicios tienen pruebas unitarias de la capa Service (JUnit 5 + Mockito), y `travelgo-review-service` además tiene pruebas de la capa Controller (MockMvc), como referencia de cómo extenderlo al resto. Todos tienen Jacoco configurado para medir cobertura real.

- `spa-api`: 44 tests.
- Los 7 microservicios nuevos: 43 tests adicionales.
- `travelgo-review-service`: 91% de cobertura total (Controller y Service al 100%).

Para correr los tests y generar el reporte de cobertura de un microservicio:

```powershell
cd <nombre-del-microservicio>
mvn test
```

El reporte navegable queda en `target/site/jacoco/index.html`.

## Decisiones de limpieza reciente

Durante el desarrollo se detectó que `spa-api` tenía su propia gestión de usuarios (Controller, Service, Repository y entidad `User`) que nunca se usaba realmente: el Gateway nunca la enrutaba (solo enruta `/api/users/**` hacia `user-service`), y la validación real de usuarios siempre se hizo vía Feign contra `user-service`. Se eliminó ese código duplicado para dejar un solo lugar responsable de usuarios en todo el ecosistema.

## Lo que falta / lo que sabemos que no está terminado

Siendo honestos, esto lo armamos priorizando tener los 10 microservicios funcionando con lógica real antes que perfeccionar todo:

- No hay despliegue en la nube (Railway/Render) todavía; Docker Compose corre todo en local.
- Algunos de los servicios nuevos (destination, notification, promotion, support) no tienen comunicación con otros servicios, son bastante autónomos — se priorizó tener 10 servicios reales con su propia regla de negocio, antes que forzar comunicación entre todos ellos.
- Solo `travelgo-review-service` tiene tests de Controller; los otros 6 microservicios nuevos solo tienen tests de Service.
- Las entidades no están separadas de DTOs — los controllers reciben y devuelven las entidades JPA directamente.

