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
- Todas estas llamadas pasan por el Gateway, igual que si vinieran de afuera.

## Cómo levantar todo

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

