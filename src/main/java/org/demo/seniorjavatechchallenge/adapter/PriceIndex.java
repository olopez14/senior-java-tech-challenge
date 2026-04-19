package org.demo.seniorjavatechchallenge.adapter;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.repository.PriceRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Índice in-memory para búsquedas ultra-rápidas de precios.
 * Usa TreeMap para encontrar el precio vigente en O(log n) sin JDBC.
 * Precargado al arrancar la app.
 */
@Component
public class PriceIndex {

    private final PriceRepository priceRepository;
    private final Map<Long, java.util.TreeMap<LocalDate, Price>> index = new ConcurrentHashMap<>();

    public PriceIndex(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @PostConstruct
    public void initialize() {
        // Precarga todos los precios en el índice durante el startup
        priceRepository.findAll().forEach(price -> {
            index.computeIfAbsent(price.getProduct().getId(), k -> new java.util.TreeMap<>())
                    .put(price.getInitDate(), price);
        });
    }

    /**
     * Busca el precio vigente para una fecha específica usando el índice.
     * O(log n) en lugar de una query JDBC.
     *
     * @param productId ID del producto
     * @param date Fecha de búsqueda
     * @return Optional con el precio vigente
     */
    public Optional<Price> findCurrentPrice(Long productId, LocalDate date) {
        java.util.TreeMap<LocalDate, Price> tree = index.get(productId);
        if (tree == null || tree.isEmpty()) {
            return Optional.empty();
        }

        // Buscar la entrada con fecha <= date (floorEntry)
        Map.Entry<LocalDate, Price> entry = tree.floorEntry(date);
        if (entry == null) {
            return Optional.empty();
        }

        Price price = entry.getValue();
        // Verificar que la fecha esté dentro del rango válido
        if (price.getEndDate() != null && date.isAfter(price.getEndDate())) {
            return Optional.empty();
        }

        return Optional.of(price);
    }

    /**
     * Invalida el caché para un producto al agregar un nuevo precio.
     * Se recarga el próximo flushAndReload().
     */
    public void invalidate(Long productId) {
        index.remove(productId);
    }

    /**
     * Recarga el índice del producto desde BD.
     * Se llama después de agregar un nuevo precio.
     */
    public void reloadProduct(Long productId) {
        java.util.TreeMap<LocalDate, Price> tree = new java.util.TreeMap<>();
        priceRepository.findByProductIdOrderByInitDateAsc(productId)
                .forEach(price -> tree.put(price.getInitDate(), price));
        index.put(productId, tree);
    }
}

