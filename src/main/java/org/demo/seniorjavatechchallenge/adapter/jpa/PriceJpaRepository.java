package org.demo.seniorjavatechchallenge.adapter.jpa;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

/**
 * Repositorio JPA para PriceJpaEntity.
 */
@Repository
public interface PriceJpaRepository extends JpaRepository<PriceJpaEntity, Long>, JpaSpecificationExecutor<PriceJpaEntity> {

    java.util.List<PriceJpaEntity> findByProductIdOrderByInitDateAsc(Long productId);

    @Query("""
            select p
            from PriceJpaEntity p
            where p.product.id = :productId
              and p.initDate = (
                    select max(p2.initDate)
                    from PriceJpaEntity p2
                    where p2.product.id = :productId
                      and p2.initDate <= :date
                      and (p2.endDate is null or p2.endDate >= :date)
              )
              and (p.endDate is null or p.endDate >= :date)
            """)
    Optional<PriceJpaEntity> findCurrentPrice(@Param("productId") Long productId, @Param("date") LocalDate date);

    @Query("""
            select count(p) > 0
            from PriceJpaEntity p
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PriceJpaEntity p WHERE p.id = ?1")
    Optional<PriceJpaEntity> findByIdForUpdate(Long id);
}

