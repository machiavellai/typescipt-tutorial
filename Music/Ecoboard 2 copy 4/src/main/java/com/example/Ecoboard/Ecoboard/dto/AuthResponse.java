package com.example.Ecoboard.Ecoboard.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String role;
    private PersonInfoResponse userInfo;
}
