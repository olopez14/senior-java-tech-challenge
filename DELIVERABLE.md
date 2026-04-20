# Entrega (resumen sencillo)

Proyecto: Senior Java Tech Challenge — Productos y Precios Históricos

Resumen rápido
---------------
API backend para gestionar productos y su historial de precios. Permite crear productos, añadir precios históricos, consultar el precio vigente para una fecha y listar el historial de precios de un producto.

Stack
-----
- Java 21
- Spring Boot 4.x
- Maven

Endpoints principales
---------------------
- POST /products — Crea un producto. Retorna 201 con el producto creado (incluye `id`).
- POST /products/{id}/prices — Añade un precio. Valida solapamientos; `endDate` puede ser `null`.
- GET /products/{id}/prices?date=YYYY-MM-DD — Devuelve `{ "value": ... }` con el precio vigente para la fecha.
- GET /products/{id}/prices — Devuelve historial de precios ordenado por `initDate` ascendente.

Reglas de negocio clave
----------------------
- Un producto puede tener múltiples precios históricos.
- No se permite solapamiento temporal entre precios del mismo producto.
- `endDate` puede ser `null` (rango abierto).
- Si existen ambas fechas, `initDate` debe ser anterior a `endDate`.
- Fechas tratadas como inclusivas.

Caché (por qué y cómo)
---------------------
- Se usa Caffeine para cachear la consulta `getCurrentPrice` (key: `productId-date`).
- TTL por defecto: 10 minutos (configurable en `CacheConfig`).
- Lectura optimizada: el repositorio devuelve `Optional<BigDecimal>` para evitar hidratar entidades completas.
- Invalidación: al crear un precio se eliminan las claves del producto afectado para mantener consistencia.

Comandos útiles (PowerShell)
---------------------------
Compilar y tests:

```
.\mvnw.cmd clean package
.\mvnw.cmd test
```

Ejecutar local:

```
.\mvnw.cmd spring-boot:run
```

Docker / Podman (rápido)
-----------------------
Construir imagen:

```
docker build -t senior-java-tech-challenge:latest .
# o con podman
podman build -t senior-java-tech-challenge:latest .
```

Ejecutar contenedor:

```
docker run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev senior-java-tech-challenge:latest
# o con podman
podman run --rm -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev senior-java-tech-challenge:latest
```

Verificar cache manualmente
--------------------------
1. Lanza la aplicación.
2. Haz `GET /products/{id}/prices?date=...` — la primera vez verás acceso a BD (cache miss).
3. Repite la misma petición — ahora debería ser cache hit.
4. Crea un precio que afecte esa fecha y repite la petición — verás acceso a BD otra vez (invalidación).

Notas para entrega
------------------
- Incluye README con instrucciones y ejemplos curl.
- Crea una rama o tag para la entrega (ej.: `release/v1.0` o `deliverable/tu-apellido-YYYYMMDD`).
- Reemplaza prints por logging antes de subir (opcional pero recomendado).

Contacto
--------
Si quieres que añada este archivo al repo, lo puedo también commitear y mostrar los comandos `git` listos para ejecutar.

