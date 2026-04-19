# AGENTS.md

Guía maestra de agentes para resolver la prueba técnica descrita en `README.md` con un flujo fuerte, verificable y reutilizable en Copilot, ChatGPT o Claude.

## Objetivo

Construir una API backend para gestionar productos y precios históricos, cumpliendo estos mínimos:

- `POST /products`
- `POST /products/{id}/prices`
- `GET /products/{id}/prices?date=YYYY-MM-DD`
- `GET /products/{id}/prices`

Reglas clave del dominio:

- un producto puede tener múltiples precios históricos
- no puede existir solapamiento temporal entre precios del mismo producto
- `endDate` puede ser `null`
- si existen ambas fechas, `initDate` debe ser menor que `endDate`
- el rendimiento es un criterio principal de evaluación

## Stack de partida detectado

- Java 21
- Maven Wrapper (`mvnw.cmd`)
- Spring Boot 4.x
- Spring MVC

## Flujo recomendado de agentes

1. `docs/agents/00-ORQUESTADOR.md`
2. `docs/agents/01-ANALISTA-REQUISITOS.md`
3. `docs/agents/02-ARQUITECTO-BACKEND.md`
4. `docs/agents/03-DOMINIO-Y-API.md`
5. `docs/agents/04-PERSISTENCIA-Y-REGLAS.md`
6. `docs/agents/05-QA-Y-TESTS.md`
7. `docs/agents/06-PERFORMANCE-Y-README.md`

## Modo estricto de trabajo

Todos los agentes deben comportarse como si estuvieran en modo ejecución real, no en modo brainstorming.

1. Leer primero `README.md`, `pom.xml` y la estructura real del proyecto.
2. No asumir archivos, dependencias, endpoints ni configuraciones que no se hayan comprobado.
3. Mantener una solución simple, rápida y fácil de ejecutar.
4. Favorecer cambios pequeños, atómicos y verificables.
5. No romper los endpoints obligatorios del enunciado.
6. Tratar los rangos de fechas como inclusivos salvo que se documente otra decisión explícita.
7. Documentar cualquier supuesto funcional en el `README.md` final.
8. Añadir pruebas automáticas para reglas de negocio y contratos HTTP.
9. Priorizar consultas eficientes e índices adecuados si se usa base de datos relacional.
10. No cerrar una fase sin indicar exactamente qué se verificó y qué queda pendiente.

## Prohibiciones explícitas

Ningún agente debe:

- inventar clases, archivos o dependencias “probables” sin revisar el proyecto
- saltarse la validación final por ir más rápido
- dejar ambigua la semántica de fechas
- proponer cambios grandes sin justificar coste/beneficio
- mezclar DTOs, entidades y contratos HTTP sin una decisión consciente
- afirmar que algo funciona si no se ha probado o verificado

## Invariantes funcionales

Estas reglas deben repetirse y respetarse en todas las fases:

- los endpoints obligatorios deben seguir disponibles
- un producto puede tener múltiples precios históricos
- no puede existir solapamiento temporal entre precios del mismo producto
- `endDate` puede ser `null`
- si existen ambas fechas, `initDate` debe ser menor que `endDate`
- el historial debe devolverse ordenado por `initDate`
- los errores HTTP deben ser consistentes
- el criterio de rendimiento influye en diseño y persistencia

## Supuestos técnicos recomendados

Estos supuestos ayudan a que los agentes converjan rápido:

- arquitectura por capas: controller, service, repository, domain/entity, dto, mapper, exception
- validación de entrada con anotaciones y validación de negocio en servicio
- historial de precios ordenado por `initDate` ascendente
- consulta de precio vigente optimizada para una fecha concreta
- errores HTTP consistentes: `400` validación, `404` recurso inexistente, `409` conflicto de solapamiento
- base de datos ligera para la prueba local: H2 o PostgreSQL según estrategia elegida, siempre documentada

## Contrato de salida obligatorio por agente

Cada agente debe devolver siempre estas secciones, en este orden:

1. `Resumen breve`
2. `Decisiones tomadas`
3. `Archivos a crear o modificar`
4. `Implementación o cambios propuestos`
5. `Checklist de validación`
6. `Riesgos abiertos o traspaso al siguiente agente`

## Definición de fase completada

Una fase solo se considera terminada si:

- el agente indica qué archivos leyó
- las decisiones importantes quedan cerradas o marcadas como riesgo
- existe una checklist de verificación concreta
- queda claro el input que necesita el siguiente agente

## Uso con Copilot, ChatGPT y Claude

Este kit está pensado para los tres asistentes, pero no se comportan igual.

- Copilot: responde mejor con instrucciones directas, accionables y muy explícitas sobre edición y validación.
- ChatGPT: responde mejor cuando se le fuerza una estructura fija de salida y se repiten restricciones críticas.
- Claude: suele rendir mejor con contexto abundante, secciones claras y criterios de “no terminar sin verificar”.

Las diferencias de uso y los prompts reforzados están en `docs/agents/INSTRUCCIONES.md`.

## Cómo usar este kit

1. Abre `docs/agents/INSTRUCCIONES.md`.
2. Elige si vas a trabajar con Copilot, ChatGPT o Claude.
3. Usa el prompt base común.
4. Añade el archivo del agente correspondiente.
5. Ejecuta los agentes en orden.


