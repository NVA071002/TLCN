package com.example.webstore.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String userEmail;
    private String address;
    private String email;
    private String phoneNumber;
    private String userName;
    private int isUser;
}
