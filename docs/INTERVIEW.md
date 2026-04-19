# Guía para defender tu código en la prueba técnica

Este documento está pensado para que lo leas antes de la entrevista técnica. Contiene explicaciones claras de las decisiones, métodos clave y respuestas a preguntas técnicas probables sobre la implementación entregada.

## Resumen rápido

- Proyecto: API para productos y precios históricos.
- Stack: Java 21, Spring Boot 4.x, Maven, H2 (tests), Caffeine (cache).
- Arquitectura: hexagonal (domain, service, repository ports, adapters JPA).
- Puntos fuertes: historial ordenado, reglas de negocio validadas, hot-path optimizado con caching y proyección al valor.

---

## Puntos que deberías explicar y cómo

1) Arquitectura hexagonal
   - Explica que los repositorios son interfaces (`repository.PriceRepository`) usadas por el servicio. Las implementaciones JPA están en `adapter.jpa` y `repository.*Adapter` (por ejemplo `ProductRepositoryAdapter`) que convierten entre entidades JPA y dominio.
   - Beneficio: permite separar la lógica del dominio de detalles de persistencia y facilita pruebas unitarias y cambios de DB.

2) Servicios y reglas de negocio (`PriceService`)
   - Métodos principales:
     - `createPrice(Long productId, CreatePriceRequest request)`:
         - Valida rango de fechas con `validateDateRange` (si endDate != null entonces initDate < endDate).
         - Busca producto con `productService.findProductForUpdateOrThrow(productId)` (lock para actualizar).
         - Comprueba solapamiento con `priceRepository.existsOverlappingPrice(productId, initDate, endDate)`. Si hay solapamiento lanza `PriceOverlapException` (HTTP 409 en controller).
         - Persiste la entidad `Price` y realiza invalidación programática del cache para las claves del producto afectado.
     - `getCurrentPrice(Long productId, LocalDate date)`:
         - Anotado con `@Cacheable(value = "currentPrice", key = "#productId + '-' + #date")`.
         - En hot path llama a `priceRepository.findCurrentPriceValue(productId, date)` que devuelve `Optional<BigDecimal>` (proyección directa al valor).
         - Si no hay precio, se llama a `productService.findProductOrThrow(productId)` para diferenciar 404 producto vs 404 precio y se lanza `PriceNotFoundForDateException`.

3) Caching con Caffeine
   - `CacheConfig` crea un bean Caffeine con TTL 10 minutos y maximumSize 10_000. Cache name: `currentPrice`.
   - Razón: reducir latencia y carga DB para consultas de precio vigentes que son frecuentes.
   - Key: `productId-date` (string). Es simple, eficiente y evita colisiones razonables.
   - Invalidación: cuando se crea un precio se eliminan claves del producto (programática) para no invalidar todo el cache.
   - Por qué proyección: `findCurrentPriceValue` devuelve solo el BigDecimal para evitar hidratar entidad Price y reducir GC/CPU.

4) Tests
   - Unit tests (Mockito) para `PriceService` validan reglas de negocio: creación, validaciones de fechas, solapamientos, excepciones.
   - Integration test `ProductPriceIntegrationTest` usa `MockMvc` construido desde `WebApplicationContext` para validar el contrato HTTP: crear producto, añadir precio, consultar vigente y historial.
   - Cómo defender: explica que los unit tests ejercitan reglas y el test de integración valida la API end-to-end con H2 en memoria.

5) Gestión de dependencias / POM
   - En el POM se fijó temporalmente `spring-boot-starter-test` a la versión del starter (4.0.5) para evitar problemas de resolución en el entorno de evaluación. En un proyecto con BOM deberías usar el BOM y no fijar manualmente.

---

## Preguntas técnicas esperables y respuestas sugeridas

Q: ¿Por qué usar caching y por qué Caffeine?
A: El endpoint de consulta de precio es el hot-path. Caffeine es un cache in-memory con muy buena latencia y características para uso en JVM (expirations, size-based eviction). Reduce consultas repetidas a DB para la misma combinación producto-fecha, mejorando latencia y reduciendo I/O.

Q: ¿Cómo evitas inconsistencias entre cache y DB?
A: Invalidamos las claves relacionadas con el producto al crear un precio. La invalidación es programática y elimina sólo las claves que pertenecen al producto (prefijo `{productId}-`). Es una estrategia práctica para la prueba; en sistemas distribuidos consideraríamos eventos de invalidación o cache distribuido con invalidación por key/versión.

Q: ¿Por qué proyectar solo el valor en la consulta (findCurrentPriceValue)?
A: Proyectar evita hidratar la entidad completa (menos objetos en heap, menos CPU para mapping), reduciendo GC pressure y mejorando throughput en el hot path.

Q: ¿Cómo gestionas el solapamiento de precios?
A: `PriceService.createPrice` llama a `priceRepository.existsOverlappingPrice(productId, initDate, endDate)`. Esa query debe estar indexada por `product_id` y por rangos de fecha para ser eficiente. Devuelve true si hay cualquier precio existente que solape el rango nuevo.

Q: ¿Qué garantías de concurrencia ofreces?
A: Para creación se usa `productService.findProductForUpdateOrThrow(productId)` que en el adaptador JPA obtiene la entidad con `PESSIMISTIC_WRITE` (ver `ProductRepositoryAdapter.findByIdForUpdate`) para serializar cambios a nivel de producto. Para escala real también se podría optar por manejo optimista o colas de cambios dependiendo de requisitos.

Q: ¿Cómo probarías la invalidez del cache en CI?
A: Añadir un test de integración que:
   1. Llama GET (espera cache miss y log de BD).
   2. Llama POST para añadir/actualizar precio.
   3. Llama GET de nuevo y verifica nueva lectura de BD (o valor actualizado). Se puede capturar log o inyectar un contador/métrica en el repositorio para pruebas.

Q: ¿Qué limitaciones tiene esta solución?
A: In-memory cache no es compartido entre instancias de la app. Para despliegues multi-instancia se requerirá cache distribuido (Redis, Hazelcast) o un bus de eventos para invalidación. Además, la invalidación actual borra por prefijo; si se necesita gran precisión se podría invalidar por keys afectadas según fechas exactas.

---

## Notas para explicar código en entrevistas (puntos clave por archivo/método)

- `PriceService`:
  - `getCurrentPrice(...)`: mostrar anotación `@Cacheable` y explicar fallback al productService cuando no existe precio para distinguir 404.
  - `createPrice(...)`: validar rango, comprobar solapamiento, persistir y evict cache por productId.

- `CacheConfig`:
  - Explicar TTL, maximumSize y por qué se eligieron (ej: 10 minutos es un valor conservador para balances entre frescura y hit-rate).

- `ProductRepositoryAdapter`:
  - Convierte entre JPA entity y domain, usa `EntityManager.lock(..., PESSIMISTIC_WRITE)` en `findByIdForUpdate` para proteger concurrencia en creación de precios.

- Tests:
  - `PriceServiceTest` cubre casos positivos y negativos de reglas de negocio.
  - `ProductPriceIntegrationTest` valida contrato HTTP. Menciona por qué usaste `MockMvc` vía `WebApplicationContext` (robustez en entorno local y evita dependencias adicionales en el test runtime).

---

Si quieres, preparo el test de integración extra que verifica la invalidación del cache (capturando logs o usando un bean contador) y lo integro en la suite.

Buena suerte en la entrevista — si quieres, te preparo un guión de 5-7 minutos para explicar el diseño en voz alta.

