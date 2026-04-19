package org.demo.seniorjavatechchallenge.adapter.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long>,
                                            JpaSpecificationExecutor<ProductJpaEntity> {
    
}



