# Plantillas reutilizables para prompts

## 1. Plantilla de arranque universal

```text
Vas a trabajar sobre una prueba técnica backend descrita en README.md.

Reglas obligatorias:
- lee primero los archivos reales del proyecto
- no asumas nada no verificado
- trabaja con cambios pequeños y verificables
- no rompas los endpoints obligatorios
- documenta supuestos funcionales
- no cierres sin validar

Formato de trabajo:
1. indica qué archivos revisarás
2. resume lo entendido
3. propone un plan breve
4. implementa o concreta cambios
5. valida con tests o comprobaciones reales
6. cierra con riesgos y siguiente paso
```

## 2. Plantilla de salida obligatoria

```text
1. Resumen breve
2. Decisiones tomadas
3. Archivos leídos
4. Archivos creados o modificados
5. Implementación o cambios propuestos
6. Checklist de validación
7. Riesgos abiertos o traspaso al siguiente agente
```

## 3. Plantilla de verificación

```text
Checklist de validación:
- [ ] Leí los archivos relevantes del proyecto
- [ ] No asumí dependencias ni clases inexistentes
- [ ] Verifiqué el impacto en endpoints obligatorios
- [ ] Ejecuté tests o comprobaciones relevantes
- [ ] Dejé claros los riesgos abiertos
```

## 4. Refuerzo para Copilot

```text
Quiero ejecución real sobre el proyecto, no solo recomendaciones.
Antes de editar, inspecciona los archivos necesarios.
Después de editar, valida.
Evita respuestas genéricas y céntrate en el workspace actual.
```

## 5. Refuerzo para ChatGPT

```text
No omitas ninguna sección del formato de salida.
Repite las restricciones críticas antes de implementar.
No inventes dependencias, archivos ni comportamiento.
No cierres sin decir exactamente qué verificaste.
```

## 6. Refuerzo para Claude

```text
Trabaja con máxima disciplina de verificación.
Quiero una salida muy estructurada y auditable.
No des la fase por cerrada sin un handoff claro al siguiente agente.
Resuelve ambigüedades con supuestos explícitos y trazables.
```

## 7. Plantilla de handoff

```text
Input para el siguiente agente:
- decisiones ya cerradas:
- archivos ya modificados:
- validaciones ya ejecutadas:
- riesgos abiertos:
- foco principal de la siguiente fase:
```

