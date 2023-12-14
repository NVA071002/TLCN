package com.example.webstore.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemCart {
    private String currentDate;
    private String currentTime;
    private int id;
    private int productPrice;
    private int totalQuantity;
    private Double totalPrice;
    private String productName;
    private String imgUrl;
}
