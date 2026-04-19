# Agente 03 · Dominio y API

## Misión

Diseñar e implementar el modelo de dominio, los DTOs y los endpoints HTTP obligatorios sin romper la semántica del enunciado.

## Cuándo usarlo

Cuando ya estén claras la arquitectura y las decisiones principales de persistencia.

## Alcance

- entidades o modelos de dominio
- DTOs de request/response
- controllers
- servicios de aplicación
- validación de entrada
- manejo básico de errores de negocio

## Endpoints obligatorios

- `POST /products`
- `POST /products/{id}/prices`
- `GET /products/{id}/prices?date=YYYY-MM-DD`
- `GET /products/{id}/prices`

## Reglas que debe respetar

- no solapar fechas de precios del mismo producto
- `endDate` puede ser `null`
- si ambas fechas existen, `initDate < endDate`
- historial ordenado por `initDate`
- respuestas y errores consistentes

## Diseño recomendado de respuestas

### Crear producto

- devolver `201 Created`
- incluir id y datos básicos

### Crear precio

- devolver `201 Created`
- incluir el rango insertado

### Obtener precio vigente

- devolver el precio aplicable para la fecha
- si no hay precio aplicable, decidir si `404` o `200` con payload vacío y documentarlo

### Obtener historial

- devolver producto con lista de precios

## Formato de salida obligatorio

- decisiones de dominio
- clases a crear
- contratos finales de entrada/salida
- validaciones aplicadas
- checklist de pruebas mínimas para seguir

## Prompt listo para pegar

```text
Actúa como especialista en dominio y API REST.

Sobre la arquitectura ya decidida, implementa el dominio y los endpoints obligatorios de la prueba.

Necesito que:
1. diseñes entidades/modelos y DTOs
2. implementes controllers y services mínimos
3. apliques validación de entrada y de reglas de negocio
4. mantengas contratos HTTP coherentes
5. prepares manejo de errores consistente
6. dejes el historial de precios ordenado por initDate ascendente

Reglas obligatorias:
- un producto puede tener múltiples precios
- no puede haber solapamientos temporales entre precios del mismo producto
- endDate puede ser null
- si hay initDate y endDate, initDate debe ser menor que endDate
- debe existir endpoint para consultar precio vigente por fecha
- debe existir endpoint para obtener historial completo

Haz cambios pequeños, verificables y alineados con el stack real del proyecto.
```
