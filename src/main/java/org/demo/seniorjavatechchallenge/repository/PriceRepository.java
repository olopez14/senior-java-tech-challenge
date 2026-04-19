package org.demo.seniorjavatechchallenge.repository;

import java.time.LocalDate;
import java.util.List;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findByProductIdOrderByInitDateAsc(Long productId);

    @Query("""
            select p
            from Price p
            where p.product.id = :productId
              and p.initDate <= :date
              and (p.endDate is null or p.endDate >= :date)
            order by p.initDate desc
            """)
    List<Price> findCurrentPrices(@Param("productId") Long productId, @Param("date") LocalDate date, Pageable pageable);

    @Query("""
            select count(p) > 0
            from Price p
            where p.product.id = :productId
              and (
                    (:endDate is null and (p.endDate is null or p.endDate >= :initDate))
                    or
                    (:endDate is not null and p.initDate <= :endDate and (p.endDate is null or p.endDate >= :initDate))
                  )
            """)
    boolean existsOverlappingPrice(
            @Param("productId") Long productId,
            @Param("initDate") LocalDate initDate,
            @Param("endDate") LocalDate endDate);
}

