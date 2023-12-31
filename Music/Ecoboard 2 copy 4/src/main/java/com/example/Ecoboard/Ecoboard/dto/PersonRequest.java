package com.example.Ecoboard.Ecoboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
@Data

public class PersonRequest {


    @NotEmpty(message = "Username Name cannot be empty")
    @Size(min = 2, message = "username must not be less than 1")
    private String userName;

    @NotEmpty(message = "first Name cannot be empty")
    @Size(min = 2, message = "first Name must not be less than 2")
    private String firstName;

    @NotEmpty(message = "Last Name cannot be empty")
    @Size(min = 2, message = "last Name must not be less than 2")
    private String lastName;

    @Email
    private String email;

    @NotNull(message = "password cannot be null")
    @Size(min = 8, message = "password must not be less than 8")
    private String password;

    @Size(min = 10, max = 14, message = "invalid Phone Number")
    private String phoneNumber;

    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dateOfBirth;


}
