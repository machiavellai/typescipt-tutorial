package com.example.Ecoboard.Ecoboard.Service;

import com.example.Ecoboard.Ecoboard.Model.user.Staff;
import com.example.Ecoboard.Ecoboard.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public interface StaffService {

    ResponseEntity<AuthResponse> loginUser(AuthRequest req) throws Exception;

    PersonInfoResponse getInfo(Authentication authentication) throws Exception;

    UpdatePersonResponse updateUserDetails(UpdatePersonRequest updatePersonRequest);

    PersonResponse register(PersonRequest personRequest) throws IOException;

    ChangePasswordResponse updateCurrentPassword(ChangePasswordRequest changePasswordRequest);

    void resetPasswordMailSender(String email, String token) ;

    Page<Staff> getAllUsers(int pageNumber);

    PersonResponse resetPasswordToken(String email) ;

    PersonResponse updateResetPassword(ResetPasswordRequest passwordRequest, String token);

    PersonResponse sendingEmail(String email) ;

}
