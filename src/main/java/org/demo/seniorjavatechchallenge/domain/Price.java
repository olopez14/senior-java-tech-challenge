package org.demo.seniorjavatechchallenge.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Price {

    private Long id;
    private Product product;
    private BigDecimal value;
    private LocalDate initDate;
    private LocalDate endDate;

    public Price(Product product, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.product = product;
        this.value = value;
        this.initDate = initDate;
        this.endDate = endDate;
    }

}





