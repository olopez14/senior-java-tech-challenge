# 🧪 Prueba Técnica – Sistema de Productos con Precios Históricos

## 🧩 Contexto

Tu objetivo es diseñar e implementar una API que permita gestionar productos y sus precios históricos. Cada producto puede tener múltiples precios a lo largo del tiempo, pero solo un precio puede estar vigente para una misma fecha.

---

## 🎯 Objetivo

Queremos que demuestres tus conocimientos técnicos, tu criterio para tomar decisiones de diseño, y tu capacidad para resolver un problema realista de backend.

Esta implementación entregada usa Spring Boot 4.x, Java 21, Maven y sigue una arquitectura hexagonal (ports & adapters). Se priorizó rendimiento y simplicidad.

⚠️ Uno de los requisitos principales es que la solución sea eficiente en respuesta y uso de recursos; por eso se añadió caching con Caffeine en el camino crítico de consulta de precio.

---

## 📘 Qué incluye esta entrega (resumen ejecutable)

- Endpoints obligatorios implementados: `POST /products`, `POST /products/{id}/prices`, `GET /products/{id}/prices?date=YYYY-MM-DD`, `GET /products/{id}/prices`.
- Arquitectura hexagonal: dominio, servicios, adaptadores JPA (en `adapter.jpa`) y repositorios (`repository`), DTOs y mappers separados.
- Caching con Caffeine para la consulta de precio vigente (optimizado como proyección al valor). TTL: 10 minutos (configurable en `CacheConfig`).
- Invalidación programática de cache cuando se crea un precio (se eliminan claves del producto afectado).
- Tests: suite de unit tests (PriceService y otros) y un test de integración mínimo (`ProductPriceIntegrationTest`) que cubre el flujo crear producto → añadir precio → consultar vigente → historial.

---

## 📘 Requisitos funcionales (implementación)

1. Crear un producto
    - `POST /products` — retorna 201 con el producto creado (incluye `id`).

2. Agregar un precio a un producto
    - `POST /products/{id}/prices` — valida solapamientos, `endDate` puede ser `null`, `initDate` < `endDate` si ambas existen.
    - En caso de solapamiento devuelve 409 Conflict.

3. Obtener el precio vigente de un producto en una fecha
    - `GET /products/{id}/prices?date=YYYY-MM-DD` — retorna `200` con `{ "value": ... }` si existe.

4. Obtener el historial completo de precios de un producto
    - `GET /products/{id}/prices` — retorna el producto con array `prices` ordenado por `initDate` ascendente.

---

## ✅ Enfoque de diseño y criterios de evaluación (cómo se cumplió)

- Modelado y hexagonalidad: dominio independiente, repositorios como interfaces y adaptadores JPA en `adapter.jpa` y `repository`.
- Validación: combinación de anotaciones y validación de negocio en `PriceService` (reglas de fecha y solapamiento).
- Rendimiento: caching con Caffeine en el hot-path `getCurrentPrice` — se proyecta directamente al valor (BigDecimal) evitando hidratar entidades innecesarias.
- Tests: unitarios para reglas de negocio y un test de integración mínimo que verifica el contrato HTTP.
- Documentación: este README y un documento adicional con explicaciones de diseño y respuestas a posibles preguntas técnicas (archivo separado `docs/INTERVIEW.md`).

---

## 🧠 Caché (detalles técnicos y verificación)

- Implementación: `CacheConfig` (bean Caffeine) define un cache `currentPrice` con TTL 10 minutos y maximumSize 10_000.
- Key: patrón "{productId}-{date}" (concatenación simple) usado en `@Cacheable(value = "currentPrice", key = "#productId + '-' + #date")`.
- Lectura optimizada: `PriceRepository.findCurrentPriceValue(productId, date)` devuelve Optional<BigDecimal> (proyección) para minimizar overhead.
- Invalidación: al crear un precio, `PriceService.createPrice` realiza una invalidación programática de entradas del cache que pertenecen al `productId` (se eliminan claves que empiezan por "{productId}-"). Esto evita invalidar todo el cache y mantiene eficiencia.

Cómo verificar manualmente:
1. Arrancar la aplicación.
2. Hacer GET /products/{id}/prices?date=... — en logs deberías ver: `[BD] Acceso a la base de datos para producto={id}, fecha={date}` la primera vez.
3. Repetir la misma petición: el mensaje no debería volver a aparecer (cache hit).
4. POST /products/{id}/prices que afecte a esa fecha, y luego volver a GET: verás el log de BD otra vez (invalidación correcta).

---

## 🔧 Cómo compilar y ejecutar (Windows PowerShell)

1. Compilar:
   .\mvnw.cmd clean package

2. Ejecutar:
   .\mvnw.cmd spring-boot:run

3. Ejecutar tests:
   - Toda la suite: .\mvnw.cmd test
   - Solo el test de integración añadido: .\mvnw.cmd -Dtest=ProductPriceIntegrationTest test

---

## 🐳 Docker / Podman (consejos y comandos)

Este proyecto incluye un `Dockerfile` y un `docker-compose.yml` mínimo.

Ejemplos (PowerShell):

- Construir la imagen (imagen local llamada `senior-java-tech-challenge`):

```
docker build -t senior-java-tech-challenge:latest .
# o con podman
podman build -t senior-java-tech-challenge:latest .
```

- Ejecutar un contenedor (puerto 8080 expuesto):

```
docker run --rm -p 8080:8080 --name sjtc -e SPRING_PROFILES_ACTIVE=dev senior-java-tech-challenge:latest
# o con podman
podman run --rm -p 8080:8080 --name sjtc -e SPRING_PROFILES_ACTIVE=dev senior-java-tech-challenge:latest
```

- Ejecutar en modo producción (ejemplo: desactivar OpenAPI en prod mediante variable):

```
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod -e SPRINGDOC_API_DOCS_ENABLED=false senior-java-tech-challenge:latest
```

- Usar `docker-compose` (si quieres levantar servicios adicionales, ver `docker-compose.yml`):

```
docker-compose up --build
# o con podman-compose si lo tienes instalado
podman-compose up --build
```

Recomendaciones y notas:

- Podman y Docker son CLI-compatibles en la mayoría de comandos; si usas Podman en modo rootless puede que necesites ajustes en el mapeo de puertos o en SELinux en sistemas Linux.
- Antes de construir la imagen, asegúrate de haber ejecutado `mvnw package` para generar el JAR en `target/` si tu `Dockerfile` copia el artefacto desde `target/`. El `Dockerfile` del proyecto también está preparado para usar el JAR generado por la fase `package`.
- Para producción, usar perfiles (`SPRING_PROFILES_ACTIVE=prod`) y desactivar Swagger/OpenAPI (propiedad `springdoc.api-docs.enabled=false` y `springdoc.swagger-ui.enabled=false`) es una buena práctica.
- Si vas a publicar la imagen en un registry privado o público, etiqueta la imagen con el repo adecuado (`registry.example.com/your-repo/senior-java-tech-challenge:tag`) y realiza `docker push` ó `podman push`.


## 🔎 Notas sobre dependencias y POM

- Durante el desarrollo local añadimos una versión explícita para `spring-boot-starter-test` (4.0.5) en el `pom.xml` para evitar un problema de resolución en este entorno. Si tu proyecto tiene una gestión central (BOM) puedes revertir esa línea y confiar en el BOM.

---

## 📌 Supuestos y decisiones importantes

- Tratamiento de fechas: las fechas son inclusivas para la definición de vigencia de un precio (documentado y testeado). Si se quiere otra convención, hay que indicarlo.
- Base de datos por defecto en tests: H2 en memoria (configuración en `application.properties`). Para producción recomendamos PostgreSQL.
- Caché: TTL 10 minutos, key por product+date, invalidación por product.

---

## 📄 Qué falta / mejoras recomendadas (opcional)

- Tests de integración adicionales: casos de error 400/404/409, y test que verifique la invalidación de cache (capturando logs o midiendo accesos a repo).
- Documentación OpenAPI/Swagger.
- Script de performance (k6/Gatling) en `docker-compose` para el bonus de rendimiento.

---

## 📦 Entrega

Incluye este README, el código fuente y el documento de explicación técnica `docs/INTERVIEW.md` con respuestas y guión para defender el diseño durante la prueba técnica.

---

¡Buena suerte! Queremos ver cómo piensas, no solo cómo codificas.
