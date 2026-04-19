package org.demo.seniorjavatechchallenge.repository.specification;

import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.adapter.jpa.PriceJpaEntity;
import org.springframework.data.jpa.domain.Specification;


public class PriceSpecifications {

    private PriceSpecifications() {
        
    }

    
    public static Specification<PriceJpaEntity> currentPriceAt(Long productId, LocalDate date) {
        return (root, query, cb) -> {
            
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

    
    public static Specification<PriceJpaEntity> overlappingWith(Long productId, LocalDate initDate, LocalDate endDate) {
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

    
    public static Specification<PriceJpaEntity> byProduct(Long productId) {
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), productId);
    }
}



