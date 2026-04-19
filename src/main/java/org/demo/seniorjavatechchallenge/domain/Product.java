package org.demo.seniorjavatechchallenge.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Product {

    private Long id;
    private String name;
    private String description;
    private List<Price> prices = new ArrayList<>();

    public Product(String name, String description) {
        this.name = name;
        this.description = description;
    }

}



