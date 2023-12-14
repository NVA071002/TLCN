package com.example.webstore.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String userEmail;
    private Integer isUser;
    private String phoneNumber;
    private String address;
}
