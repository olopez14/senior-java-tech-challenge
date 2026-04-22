# 🧪 Prueba Técnica – Sistema de Productos con Precios Históricos

## 🧩 Contexto

Tu objetivo es diseñar e implementar una API que permita gestionar productos y sus precios históricos. Cada producto puede tener múltiples precios a lo largo del tiempo, pero solo un precio puede estar vigente para una misma fecha.

---

## 🎯 Objetivo

Queremos que demuestres tus conocimientos técnicos, tu criterio para tomar decisiones de diseño, y tu capacidad para resolver un problema realista de backend.

Esta implementación usa Spring Boot 4.x, Java 21, Maven y sigue una arquitectura hexagonal (domain, application, infrastructure). Se priorizó rendimiento, simplicidad y mantenibilidad.

**Puntos clave:**
- Acceso a datos con JDBC puro (`JdbcTemplate`). No se usa JPA ni ningún ORM.
- Dominio rico: entidades y Value Objects con reglas de negocio y validaciones.
- Sin Lombok en el dominio (Java puro, sin dependencias externas).
- Caching eficiente con Caffeine para consultas críticas.
- Tests unitarios y de integración.

---

## 📘 Qué incluye esta entrega (resumen ejecutivo)

- Endpoints obligatorios implementados: `POST /products`, `POST /products/{id}/prices`, `GET /products/{id}/prices?date=YYYY-MM-DD`, `GET /products/{id}/prices`.
- Arquitectura hexagonal real: separación clara entre dominio, aplicación y adaptadores de infraestructura (repositorios JDBC, mappers, controladores).
- Acceso a datos con JDBC (`JdbcTemplate`), queries SQL explícitas, sin JPA.
- Caching con Caffeine para la consulta de precio vigente (TTL configurable, invalidación eficiente por producto).
- Dominio sin Lombok, con validaciones y reglas encapsuladas.
- Servicios granulares (SRP), cada uno con una única responsabilidad.
- Tests: unitarios para reglas de negocio y tests de integración para los flujos principales.

---

## 📦 Arquitectura y diseño

- **Hexagonal (Ports & Adapters):**
  - `domain`: entidades, Value Objects y lógica de negocio.
  - `application`: servicios de caso de uso, DTOs, mappers.
  - `infrastructure`: adaptadores de persistencia (JDBC), caché, controladores REST.
- **Dominio rico:**
  - Entidades y Value Objects con validaciones e invariantes.
  - Sin setters públicos ni estado mutable expuesto.
  - Sin Lombok ni dependencias externas en el dominio.
- **Persistencia JDBC:**
  - Repositorios implementados con `JdbcTemplate`.
  - SQL explícito para todas las operaciones (insert, update, select).
  - Mapeo manual con `RowMapper`.
- **Caché:**
  - Caffeine configurado para la consulta de precio vigente.
  - Invalidación programática eficiente al crear o modificar precios.

---

## 🧠 Caché (detalles técnicos)

- Implementación: `CacheConfig` define un cache `currentPrice` con TTL 10 minutos y tamaño máximo configurable.
- Key: `{productId}-{date}`.
- Lectura optimizada: proyección directa al valor (`BigDecimal`) para minimizar overhead.
- Invalidación: al crear un precio, se eliminan solo las claves afectadas por el producto.

---

## 🧪 Tests

- Tests unitarios para reglas de negocio y validaciones de dominio.
- Tests de integración para los flujos principales (crear producto, añadir precio, consultar vigente, historial).
---

## 🚀 Cómo compilar y ejecutar

1. Compilar:
   ```
   .\mvnw.cmd clean package
   ```
2. Ejecutar:
   ```
   .\mvnw.cmd spring-boot:run
   ```
3. Ejecutar tests:
   ```
   .\mvnw.cmd test
   .\mvnw.cmd -Dtest=ProductPriceIntegrationTest test
   ```

---

## 🐳 Docker / Podman

Incluye `Dockerfile` y `docker-compose.yml` para facilitar la ejecución y pruebas en contenedores.

---

## 📌 Decisiones y supuestos importantes

- **Sin JPA:** Todo el acceso a datos es JDBC puro (`JdbcTemplate`).
- **Dominio limpio:** Sin Lombok, sin dependencias externas, validaciones en el propio modelo.
- **Hexagonalidad:** Separación estricta de capas.
- **Caché:** TTL 10 minutos, invalidación eficiente.
- **Tests:** Cobertura básica.
- **Base de datos:** H2 en memoria para tests

---

## 📝 Notas finales

Este proyecto es defendible ante cualquier revisión técnica: cumple con buenas prácticas de DDD, arquitectura limpia, acceso eficiente a datos, dominio rico y testabilidad. Si necesitas migrar más lógica a JDBC, ampliar tests o adaptar la caché, la estructura lo permite fácilmente.
