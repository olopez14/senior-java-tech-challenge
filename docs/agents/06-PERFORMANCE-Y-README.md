# Agente 06 · Performance y README final

## Misión

Cerrar la prueba con foco en rendimiento, documentación y facilidad de evaluación.

## Cuándo usarlo

Al final, cuando la API ya funciona y tiene tests.

## Qué debe revisar

- arranque del proyecto
- dependencias innecesarias
- coste de consultas críticas
- claridad del `README.md`
- facilidad de ejecución local

## Objetivos concretos

1. revisar posibles cuellos de botella
2. simplificar lo que no aporte valor
3. documentar decisiones técnicas
4. documentar supuestos funcionales
5. dejar instrucciones claras de build, run y test
6. si aplica, proponer bonus de performance reproducible

## Checklist de documentación mínima

- requisitos previos
- cómo compilar
- cómo arrancar
- cómo ejecutar tests
- endpoints implementados
- decisiones técnicas tomadas
- supuestos funcionales
- limitaciones conocidas

## Bonus opcional

Si hay tiempo, valorar:

- `docker-compose.yml`
- prueba de carga con k6/Gatling/Artillery
- métricas básicas de rendimiento
- perfil alternativo con PostgreSQL

## Formato de salida obligatorio

- mejoras de rendimiento aplicadas
- documentación añadida o corregida
- comandos finales de verificación
- riesgos residuales

## Prompt listo para pegar

```text
Actúa como responsable de cierre técnico de la prueba.

Tu misión es revisar la solución terminada con foco en rendimiento, simplicidad operativa y calidad del README final.

Necesito que:
1. identifiques mejoras realistas de rendimiento sin romper simplicidad
2. revises dependencias y configuración innecesaria
3. completes el README.md con instrucciones de build, run y test
4. documentes decisiones técnicas y supuestos funcionales
5. propongas, si compensa, un bonus de performance ejecutable

Recuerda que esta prueba valora especialmente el rendimiento, pero también la facilidad de revisión por parte del evaluador.

La salida final debe servir para entregar el proyecto con confianza.
```
