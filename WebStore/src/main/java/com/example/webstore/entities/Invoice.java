package com.example.webstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Invoice {
    private  List<ItemCart> listProduct;
    private int totalPrice;
    private int id;
    private String userEmail;
    private String date;
    private String phoneNumber;
    private String status;
}
