package com.example.webstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product  implements Serializable {
    private int id;
    private String imageUrl;
    private String name;
    private String description;
    private Double price;
    private String type;
}
