# Cómo se evita el solapamiento de fechas: del Controller a la Persistencia

Este documento explica de forma práctica y con ejemplos el flujo completo para evitar que dos precios del mismo producto se solapen en tiempo. Incluye snippets desde el `Controller`, `Service`, bloqueo de producto y la consulta en `Repository` que detecta solapamientos.

Resumen breve
- Validaciones locales: comprobación de que `initDate < endDate` cuando `endDate != null`.
- Comprobación de solapamiento en la base de datos: `existsOverlappingPrice(productId, initDate, endDate)`.
- Operación atómica: todo dentro de una transacción y con bloqueo por producto (`SELECT ... FOR UPDATE` / `PESSIMISTIC_WRITE`) para evitar condiciones de carrera.

Checklist rápido
- [ ] Validar rango (service)
- [ ] Bloquear fila del producto (product service / repository)
- [ ] Comprobar `existsOverlappingPrice(...)` (repository)
- [ ] Insertar precio (dentro de la misma transacción)

1) Contrato HTTP — Controller (ejemplo)

```java
@PostMapping("/products/{productId}/prices")
@ResponseStatus(HttpStatus.CREATED)
public CreatedPriceResponse createPrice(
        @PathVariable Long productId,
        @Valid @RequestBody CreatePriceRequest request) {
    return priceService.createPrice(productId, request);
}
```

2) Capa Service — flujo principal

```java
@Transactional
public CreatedPriceResponse createPrice(Long productId, CreatePriceRequest request) {
    // 1) Validación básica del request
    validateDateRange(request);

    // 2) Recuperar el producto con bloqueo para serializar por producto
    Product product = productService.findProductForUpdateOrThrow(productId);

    // 3) Comprobar solapamiento en BD (consulta rápida y eficiente)
    if (priceRepository.existsOverlappingPrice(productId, request.initDate(), request.endDate())) {
        throw new PriceOverlapException(productId);
    }

    // 4) Mapear y persistir
    Price price = PriceMapper.toPrice(product, request);
    Price saved = priceRepository.save(price);

    return PriceMapper.toCreatedResponse(saved);
}
```

Notas:
- `@Transactional` asegura atomicidad: la comprobación y la inserción ocurren dentro de la misma transacción.
- `findProductForUpdateOrThrow` debe devolver el `Product` aplicando un lock (ver sección siguiente).

3) Bloqueo por producto (evitar condiciones de carrera)

Opciones para implementar `findProductForUpdateOrThrow`:

- Usando `EntityManager`:

```java
@Transactional
public Product findProductForUpdateOrThrow(Long productId) {
    Product p = entityManager.find(Product.class, productId, LockModeType.PESSIMISTIC_WRITE);
    if (p == null) throw new ProductNotFoundException(productId);
    return p;
}
```

- Usando `Spring Data JPA` repository con `@Lock`:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
```

Y en `ProductService`:

```java
@Transactional
public Product findProductForUpdateOrThrow(Long id) {
    return productRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
}
```

Por qué bloquear el producto:
- Si dos peticiones intentan insertar precios para el mismo producto simultáneamente, ambas podrían ver "no hay solapamiento" y terminar insertando precios solapados. El bloqueo serializa las operaciones por producto y evita esta condición de carrera.

4) Consulta para detectar solapamiento (Repository)

Regla matemática (fechas inclusivas): dos intervalos [a,b] y [c,d] se solapan si

```
a <= d  &&  c <= b
```

En SQL/JPQL debemos considerar que `endDate` puede ser `NULL` (rango abierto). Una forma portable es usar un parámetro `farFuture` (ej. 9999-12-31) y `COALESCE`.

- Ejemplo JPQL (portable, pasando `farFuture` como parámetro):

```java
@Query("""
  SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
  FROM Price p
  WHERE p.product.id = :productId
    AND (COALESCE(p.endDate, :farFuture) >= :initDate)
    AND (COALESCE(:endDate, :farFuture) >= p.initDate)
""")
boolean existsOverlappingPrice(@Param("productId") Long productId,
                               @Param("initDate") LocalDate initDate,
                               @Param("endDate") LocalDate endDate,
                               @Param("farFuture") LocalDate farFuture);
```

Uso desde el service:

```java
LocalDate farFuture = LocalDate.of(9999,12,31);
boolean overlaps = priceRepository.existsOverlappingPrice(productId, request.initDate(), request.endDate(), farFuture);
```

- Ejemplo nativo (si prefieres COALESCE con literal):

```java
@Query(value = """
  SELECT CASE WHEN COUNT(1) > 0 THEN true ELSE false END
  FROM price p
  WHERE p.product_id = :productId
    AND COALESCE(p.end_date, :farFuture) >= :initDate
    AND COALESCE(:endDate, :farFuture) >= p.init_date
""", nativeQuery = true)
boolean existsOverlappingPriceNative(@Param("productId") Long productId,
                                     @Param("initDate") LocalDate initDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("farFuture") LocalDate farFuture);
```

5) Casos límite y semántica (decisiones)

- Igualdad de bordes (semántica actual: inclusiva):
  - Si existe [2024-01-01, 2024-06-30] y se intenta insertar [2024-06-30, 2024-12-31] → se considera solapamiento.
- Si quieres permitir intervalos contiguos (un precio empieza el día siguiente a otro):
  - Cambia la regla a `a < d && c < b` o trata `endDate` como exclusivo (half-open intervals [init, end)).

6) Concurrencia: alternativas y recomendaciones

- Pessimistic lock (recomendado para este caso):
  - Bloqueo por fila de producto (`PESSIMISTIC_WRITE`). Serializa por producto y minimiza ventana de inconsistencia.
- Serializable isolation:
  - Garantiza ausencia de anomalías pero tiene mayor coste y puede provocar rollbacks por conflictos.
- Constraint en BD:
  - Difícil de aplicar para intervalos arbitrarios; normalmente no es práctico sin estructuras específicas.
- Atomic check-and-insert en BD:
  - Implementar lógica en un procedimiento almacenado para que la BD verifique y haga insert en una única operación.

7) Tests (qué cubrir y ejemplos)

- Unitarios (service):
  - Caso no solapado → verifica que se llama `priceRepository.save(...)`.
  - Caso solapado → `existsOverlappingPrice(...)` devuelve true y se lanza `PriceOverlapException`.

- Integración concurrente (recomendado):
  - Disparar dos requests simultáneos para insertar precios solapados y verificar que solo uno persiste (el otro recibe 409 o excepción).
  - Ejemplo esbozado (Java):

```java
CountDownLatch ready = new CountDownLatch(2);
CountDownLatch start = new CountDownLatch(1);
ExecutorService ex = Executors.newFixedThreadPool(2);

Callable<Integer> task = () -> {
  ready.countDown();
  start.await();
  // llamar endpoint POST /products/{id}/prices y devolver status code
};

Future<Integer> f1 = ex.submit(task);
Future<Integer> f2 = ex.submit(task);
ready.await();
start.countDown();

int s1 = f1.get();
int s2 = f2.get();
// comprobar que exactamente uno es 201 y el otro es 409
```

8) Ejemplo completo rápido (resumen del flujo)

1. Cliente POST /products/1/prices { initDate, endDate }
2. Controller llama a `priceService.createPrice(1, request)`
3. `PriceService` valida `initDate < endDate`
4. `PriceService` invoca `productService.findProductForUpdateOrThrow(1)` que aplica `PESSIMISTIC_WRITE`
5. `PriceService` invoca `priceRepository.existsOverlappingPrice(1, init, end, farFuture)`
6. Si devuelve `true` → lanzar `PriceOverlapException` (HTTP 409)
7. Si `false` → persistir nuevo `Price` y commit de la transacción

9) Recomendaciones finales

- Mantén la semántica de fechas documentada en README (inclusiva o exclusiva).
- Implementa locking por producto si esperas concurrencia en updates de precios.
- Añade tests de concurrencia en integración para demostrar robustez.
- Si el dominio requiere muy alta concurrencia, considera estrategias avanzadas (eventual consistency, colas, versionado de precios).

Si quieres, puedo:
- Implementar exactamente las consultas `existsOverlappingPrice(...)` en tu `PriceRepository` con JPQL o native query.
- Añadir el método `findProductForUpdateOrThrow(...)` en `ProductService` usando `EntityManager` o `@Lock`.
- Añadir un test de integración concurrente que valide la solución.

