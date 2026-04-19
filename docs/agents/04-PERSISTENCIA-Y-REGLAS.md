# Agente 04 · Persistencia y reglas de negocio

## Misión

Implementar la parte más delicada de la prueba: almacenamiento eficiente, validación de solapamientos y consulta rápida del precio vigente.

## Cuándo usarlo

Después de tener el modelo y los endpoints esbozados.

## Problemas que debe resolver

1. persistir productos y precios
2. evitar solapamientos entre rangos del mismo producto
3. consultar el precio vigente para una fecha concreta con buena eficiencia
4. devolver historial ordenado
5. garantizar integridad transaccional

## Estrategias sugeridas

### Validación de solapamiento

Para un rango nuevo `[initDate, endDate]`, detectar conflicto con cualquier precio existente del mismo producto cuyo rango interseque.

Si `endDate` es `null`, tratarlo como rango abierto.

### Consulta del precio vigente

Optimizar una consulta del estilo:
- mismo producto
- `initDate <= date`
- `endDate is null or endDate >= date`
- devolver como máximo un registro

### Índices recomendados

- índice por `product_id`
- índice compuesto por `product_id, init_date`
- valorar `product_id, end_date` según tecnología elegida

## Riesgos a vigilar

- errores en comparaciones inclusivas
- inconsistencias si no hay transacción
- rendimiento pobre si se valida en memoria cargando todo el historial
- duplicidad de lógica entre repositorio y servicio

## Formato de salida obligatorio

- estrategia de persistencia concreta
- consultas o métodos de repositorio necesarios
- validación de solapamiento documentada
- índices propuestos
- checklist de tests de negocio

## Prompt listo para pegar

```text
Actúa como especialista en persistencia y reglas de negocio.

Tu objetivo es dejar resuelta la capa de almacenamiento para productos y precios históricos, con foco especial en integridad y rendimiento.

Necesito que:
1. implementes repositorios y entidades persistentes si faltan
2. definas la validación de solapamientos correctamente
3. optimices la consulta del precio vigente por fecha
4. mantengas historial ordenado
5. apliques transacciones donde sean necesarias
6. propongas índices o ajustes de rendimiento razonables

Reglas críticas:
- no debe existir solapamiento temporal para precios del mismo producto
- endDate puede ser null
- si hay ambas fechas, initDate debe ser menor que endDate
- la fecha consultada debe respetar la semántica definida del rango

No aceptes soluciones que carguen innecesariamente todo en memoria si puede resolverse mejor desde persistencia.
```
