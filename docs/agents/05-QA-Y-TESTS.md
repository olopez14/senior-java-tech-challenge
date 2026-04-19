# Agente 05 · QA y tests

## Misión

Blindar la solución con pruebas automáticas de negocio y contrato HTTP.

## Cuándo usarlo

Cuando la funcionalidad principal ya existe y toca validar de verdad.

## Cobertura mínima esperada

### Tests de creación de producto

- crea producto válido
- rechaza payload inválido si aplica

### Tests de creación de precio

- crea rango válido
- rechaza `initDate >= endDate`
- rechaza solapamiento parcial
- rechaza solapamiento total
- admite rango abierto con `endDate = null`
- admite rangos contiguos si la semántica elegida lo permite y está documentada

### Tests de consulta por fecha

- devuelve precio vigente correcto
- maneja fecha sin precio vigente
- responde `404` para producto inexistente

### Tests de historial

- devuelve producto con precios ordenados
- funciona con múltiples precios
- maneja producto sin precios si ese caso se soporta

## Tipo de pruebas sugerido

- unitarias para validadores y servicio
- integración HTTP con MockMvc o equivalente
- opcional: repositorio si hay queries complejas

## Señales de calidad

- nombres de tests descriptivos
- datos de prueba fáciles de entender
- asserts sobre status, payload y errores
- pruebas rápidas y deterministas

## Formato de salida obligatorio

- lista de casos cubiertos
- archivos de test creados o modificados
- huecos de cobertura restantes
- resultado de ejecución de tests

## Prompt listo para pegar

```text
Actúa como responsable de QA para esta prueba técnica.

Quiero que diseñes e implementes una batería de tests automática que cubra la API y las reglas de negocio clave.

Necesito que:
1. identifiques los escenarios mínimos y los casos borde
2. implementes tests unitarios e integración donde aporte valor
3. verifiques códigos HTTP, payloads y validaciones
4. compruebes especialmente la lógica de solapamientos y consulta por fecha
5. dejes una cobertura razonable sin sobrecomplicar el proyecto

Prioriza pruebas rápidas, legibles y fiables.

La salida debe incluir qué quedó cubierto, qué no, y qué comandos ejecutar para validar todo.
```
