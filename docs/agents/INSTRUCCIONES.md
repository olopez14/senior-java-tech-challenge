# Instrucciones de uso del kit de agentes

## Objetivo

Este directorio contiene un flujo de agentes para completar la prueba técnica de `README.md` sin perder tiempo en re-trabajo.

## Orden de ejecución recomendado

1. `00-ORQUESTADOR.md`
2. `01-ANALISTA-REQUISITOS.md`
3. `02-ARQUITECTO-BACKEND.md`
4. `03-DOMINIO-Y-API.md`
5. `04-PERSISTENCIA-Y-REGLAS.md`
6. `05-QA-Y-TESTS.md`
7. `06-PERFORMANCE-Y-README.md`

## Cómo usar cada agente

### Opción A: un chat por agente

Usa un chat separado por fase. En cada chat:

1. pega el contenido del archivo del agente
2. añade el contexto actual del proyecto
3. pide cambios concretos en el workspace
4. valida el resultado antes de pasar al siguiente agente

### Opción B: un único chat con cambio de rol

En el mismo chat:

1. pega primero `00-ORQUESTADOR.md`
2. completa la fase
3. pega el siguiente agente
4. repite el ciclo hasta terminar

## Plantilla de arranque para cualquier agente

Copia esto antes del prompt del agente:

```text
Contexto del proyecto:
- La especificación funcional está en README.md.
- El proyecto base es Java 21 + Maven Wrapper + Spring Boot.
- Quiero una solución simple, rápida y bien testeada.
- No rompas los endpoints obligatorios del enunciado.

Forma de trabajar:
- Primero resume lo que entiendes.
- Después propón un plan breve.
- Luego implementa los cambios directamente.
- Finalmente valida con tests o comprobaciones reales.
- Si haces supuestos, documéntalos.
```

## Contrato de salida entre agentes

Al terminar cada fase, el agente debe dejar esta información:

### 1. Decisiones cerradas

- stack elegido
- estructura de paquetes
- semántica de fechas
- formato de errores
- estrategia de persistencia

### 2. Archivos tocados

Lista exacta de archivos creados o modificados.

### 3. Validaciones ejecutadas

- tests corridos
- endpoints verificados
- reglas de negocio cubiertas

### 4. Riesgos abiertos

- puntos no resueltos
- mejoras opcionales
- deuda técnica aceptada

## Definición de terminado mínima

La prueba no debería darse por cerrada hasta cumplir esto:

- `POST /products` funciona
- `POST /products/{id}/prices` valida solapamientos y fechas
- `GET /products/{id}/prices?date=...` devuelve el precio vigente o un caso controlado
- `GET /products/{id}/prices` devuelve producto + historial
- existen tests automáticos relevantes
- el `README.md` explica cómo ejecutar y qué decisiones se tomaron

## Recomendaciones prácticas

- Mantén DTOs separados de entidades.
- Usa validación declarativa para entrada y reglas de negocio en servicio.
- Si usas JPA, define índices para búsqueda por producto y fecha.
- Ordena el historial por fecha de inicio.
- Documenta si el rango es inclusivo en ambos extremos.
- Si el tiempo es limitado, prioriza calidad funcional, tests y claridad.

## Estrategia sugerida de implementación

### Camino conservador

- Spring MVC
- Spring Data JPA
- H2 para ejecución local y tests
- MockMvc para integración

### Camino más orientado a producción

- Spring MVC
- PostgreSQL
- Flyway
- Testcontainers

Si eliges el segundo camino, documenta bien cómo ejecutar todo.

