package org.profit.telegrambot.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;
    private Integer categoryId;
    private String name;
    private Double price;
    private String image;
    private String description;

}
