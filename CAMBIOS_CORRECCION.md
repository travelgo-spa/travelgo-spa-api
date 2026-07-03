# Correcciones realizadas - TravelGo SPA API

Fecha: 2026/07/03

## Cambios principales

1. Se corrigió la inconsistencia de tipos en `travelgo-spa-api`:
   - `UserService` ahora usa `Long` en `findById` y `delete`, igual que `UserServiceImpl`, `UserController`, `UserRepository` y la entidad `User`.

2. Se mejoró el manejo de fallas en comunicación entre microservicios:
   - `ReservationServiceImpl` ahora captura errores al llamar a `UserServiceClient` y entrega una `BusinessException` controlada.

3. Se agregó configuración Maven Wrapper en ambos microservicios:
   - `travelgo-spa-api/.mvn/wrapper/maven-wrapper.properties`
   - `travelgo-user-service/.mvn/wrapper/maven-wrapper.properties`

4. Se eliminó el proyecto duplicado anidado:
   - `travelgo-user-service/travelgo-user-service/...`

5. Se eliminaron artefactos generados:
   - carpetas `target/`
   - bases SQLite generadas
   - archivo accidental `JwtService` sin extensión

6. Se agregaron tests unitarios con JUnit 5 y Mockito:
   - `TravelPackageServiceImplTest`
   - `ReservationServiceImplTest`
   - `UserServiceImplTest` en `travelgo-spa-api`
   - `UserServiceImplTest` en `travelgo-user-service`

7. Se agregó JaCoCo en ambos `pom.xml` para generar reporte de cobertura.

8. Se mejoró Swagger/OpenAPI agregando `@Operation`, `@ApiResponse` y `@ApiResponses` en controladores principales.

## Comandos sugeridos para probar localmente

Desde cada carpeta de microservicio:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Servicios:

- `travelgo-user-service`: http://localhost:8082
- `travelgo-spa-api`: http://localhost:8081

Swagger:

- http://localhost:8082/doc/swagger-ui.html
- http://localhost:8081/doc/swagger-ui.html

