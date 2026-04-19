package org.demo.seniorjavatechchallenge.adapter.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PriceJpaRepository extends JpaRepository<PriceJpaEntity, Long>,
                                          JpaSpecificationExecutor<PriceJpaEntity> {

    
    List<PriceJpaEntity> findByProductIdOrderByInitDateAsc(Long productId);

    
    @Query("""
        SELECT p.value FROM PriceJpaEntity p 
        WHERE p.product.id = :productId 
        AND p.initDate <= :date 
        AND (p.endDate IS NULL OR p.endDate >= :date)
        ORDER BY p.initDate DESC
        """)
    Optional<BigDecimal> findCurrentPriceValue(@Param("productId") Long productId,
                                                @Param("date") LocalDate date,
                                                Limit limit);
}








