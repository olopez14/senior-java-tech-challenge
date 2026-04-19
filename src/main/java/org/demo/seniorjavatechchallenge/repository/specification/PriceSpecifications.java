package org.demo.seniorjavatechchallenge.repository.specification;

import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.springframework.data.jpa.domain.Specification;

/**
 * Especificaciones reutilizables para consultas de precios.
 * Proporciona una API type-safe para filtrar precios sin necesidad de @Query.
 */
public class PriceSpecifications {

    private PriceSpecifications() {
        // Utility class
    }

    /**
     * Especificación para obtener el precio vigente en una fecha específica.
     *
     * Criterios:
     * - El producto coincide
     * - initDate es menor o igual a la fecha consultada
     * - endDate es null O mayor o igual a la fecha consultada
     * - Se ordena por initDate DESC y se toma el primero (más reciente)
     *
     * @param productId ID del producto
     * @param date Fecha para la consulta
     * @return Specification que encuentra el precio vigente
     */
    public static Specification<Price> currentPriceAt(Long productId, LocalDate date) {
        return (root, query, cb) -> {
            // Ordenar por initDate descendente para obtener el más reciente
            query.orderBy(cb.desc(root.get("initDate")));

            return cb.and(
                cb.equal(root.get("product").get("id"), productId),
                cb.lessThanOrEqualTo(root.get("initDate"), date),
                cb.or(
                    cb.isNull(root.get("endDate")),
                    cb.greaterThanOrEqualTo(root.get("endDate"), date)
                )
            );
        };
    }

    /**
     * Especificación para verificar si existe un precio que se superpone con el rango dado.
     *
     * Criterios:
     * - El producto coincide
     * - El precio existente tiene initDate menor o igual al endDate del rango (o initDate si endDate es null)
     * - El precio existente tiene endDate null O endDate mayor o igual a initDate del rango
     *
     * @param productId ID del producto
     * @param initDate Fecha de inicio del rango a validar
     * @param endDate Fecha de fin del rango a validar (puede ser null)
     * @return Specification que encuentra precios que se solapan
     */
    public static Specification<Price> overlappingWith(Long productId, LocalDate initDate, LocalDate endDate) {
        return (root, query, cb) -> {
            LocalDate effectiveEndDate = endDate != null ? endDate : initDate;

            return cb.and(
                cb.equal(root.get("product").get("id"), productId),
                cb.lessThanOrEqualTo(root.get("initDate"), effectiveEndDate),
                cb.or(
                    cb.isNull(root.get("endDate")),
                    cb.greaterThanOrEqualTo(root.get("endDate"), initDate)
                )
            );
        };
    }

    /**
     * Especificación para filtrar precios de un producto.
     *
     * @param productId ID del producto
     * @return Specification que filtra por producto
     */
    public static Specification<Price> byProduct(Long productId) {
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), productId);
    }
}

