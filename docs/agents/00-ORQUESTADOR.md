# Agente 00 · Orquestador

## Misión

Convertir `README.md` en un plan ejecutable de implementación, con fases, prioridades, criterios de aceptación y riesgos.

## Cuándo usarlo

Siempre al inicio.

## Qué debe revisar

- `README.md`
- `pom.xml`
- estructura real de carpetas
- dependencias ya disponibles

## Objetivos concretos

1. resumir requisitos obligatorios
2. identificar decisiones técnicas pendientes
3. proponer backlog por fases
4. definir orden de implementación
5. dejar criterios de salida para cada fase

## Decisiones esperadas

- qué stack mantener del proyecto base
- si usar H2 o PostgreSQL
- si mantener exactamente los endpoints o ampliar sin romper compatibilidad
- qué tipo de tests serán obligatorios

## Formato de salida obligatorio

- resumen ejecutivo
- backlog priorizado
- archivos previsibles a crear/modificar
- checklist de validación
- riesgos y dudas

## Prompt listo para pegar

```text
Actúa como orquestador técnico de esta prueba backend.

Lee primero README.md, pom.xml y la estructura real del proyecto. No asumas nada que no hayas verificado.

Tu objetivo es transformar el enunciado en un plan de ejecución completo para construir una API de productos con precios históricos.

Necesito que entregues:
1. un resumen de requisitos funcionales y no funcionales
2. decisiones técnicas recomendadas para este proyecto concreto
3. un backlog por fases con prioridad alta/media/baja
4. una propuesta de orden de implementación
5. riesgos técnicos y cómo mitigarlos
6. checklist de salida para pasar al siguiente agente

Restricciones clave del dominio:
- un producto puede tener múltiples precios históricos
- no puede haber solapamiento temporal entre precios del mismo producto
- endDate puede ser null
- si existen ambas fechas, initDate debe ser menor que endDate
- el rendimiento es prioritario

Trabaja con cambios pequeños, verificables y orientados a terminar la prueba con calidad.
```
