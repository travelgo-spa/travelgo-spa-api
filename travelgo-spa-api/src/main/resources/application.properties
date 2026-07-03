spring.application.name=travelgo-spa-api
server.port=8081

# =============================================
# BASE DE DATOS - SQLite
# =============================================
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.url=jdbc:sqlite:data/travelgo.db
spring.datasource.username=
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
# CORRECTO: validate en lugar de update (Flyway gestiona el esquema)
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# =============================================
# FLYWAY - Migraciones versionadas
# =============================================
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# =============================================
# SEGURIDAD / JWT
# =============================================
jwt.secret=Expedition33gano9premiosyesGOTYdel2026segura
jwt.expirationMinutes=120

# =============================================
# MICROSERVICIO DE USUARIOS
# =============================================
userservice.base-url=http://localhost:8082

# =============================================
# SWAGGER / OpenAPI
# =============================================
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/doc/swagger-ui.html
spring.web.resources.add-mappings=true

# =============================================
# LOGS Y ERRORES
# =============================================
logging.level.com.travelgo=DEBUG
logging.level.org.springframework.security=INFO
server.error.include-message=always
server.error.include-binding-errors=always
