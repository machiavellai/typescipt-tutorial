package com.example.Ecoboard.Ecoboard.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordRequest {

    private String userName;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
