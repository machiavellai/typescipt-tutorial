package com.example.Ecoboard.Ecoboard.Service.serviceimplementation;

import com.example.Ecoboard.Ecoboard.Email.EmailService;
import com.example.Ecoboard.Ecoboard.Model.user.Staff;
import com.example.Ecoboard.Ecoboard.Security.JwtUtils;
import com.example.Ecoboard.Ecoboard.Service.StaffService;
import com.example.Ecoboard.Ecoboard.dto.*;
import com.example.Ecoboard.Ecoboard.exception.PersonNotFoundException;
import com.example.Ecoboard.Ecoboard.repository.StaffRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StaffServiceImpl implements StaffService {

   private final PasswordEncoder bcryptPasswordEncoder;

   private final StaffDetailsService userDetailsService;

   private final AuthenticationManager authenticationManager;

   private final ModelMapper modelMapper;

    private final StaffRepository staffRepository;

    private final EmailValidator emailValidator;

    private final JwtUtils jwtUtils;

    private final EmailService emailSender;



    private String website;
    @Value("${server.port}")
    private Integer port;

    @Autowired
    public StaffServiceImpl(PasswordEncoder bcryptPasswordEncoder, StaffDetailsService userDetailsService,
                            AuthenticationManager authenticationManager,
                            ModelMapper modelMapper,
                            StaffRepository staffRepository,
                            EmailValidator emailValidator,
                            JwtUtils jwtUtils,
                            EmailService emailSender) {
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.staffRepository = staffRepository;
        this.emailValidator = emailValidator;
        this.jwtUtils = jwtUtils;
        this.emailSender = emailSender;
    }


    @Override
    public ResponseEntity<AuthResponse> loginUser(AuthRequest req) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(),
                    req.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final StaffDetails staff = userDetailsService.loadUserByUsername(req.getUsername());
            List<String> roles = staff.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
            log.info("{} {}", roles.size(),roles);

            final String jwt = JwtUtils.generateToken(staff);
            final AuthResponse res = new AuthResponse();

            String role =null;
            for (String r : roles) {
                if (r!=null) role = r;
            }
            final PersonInfoResponse userInfo = getUserInfo(req.getUsername());
            res.setToken(jwt);
            res.setRole(role);
            res.setUserInfo(userInfo);
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            throw new Exception("Incorrect username or password!", e);
        }
    }

    @Override
    public PersonInfoResponse getInfo(Authentication authentication) throws Exception {
        Staff staff = staffRepository.findByUserName(authentication.getName())
                .orElseThrow(()-> new PersonNotFoundException("Person Not Found"));
//        Address address = addressRepository.findFirstByPerson(person)
//                .orElseThrow(()-> new AddressNotFoundException("Address Not Found"));
        PersonInfoResponse personInfoResponse = new PersonInfoResponse();
//        AddressRequest addressRequest = new AddressRequest();
//        modelMapper.map(address, addressRequest);
        modelMapper.map(staff, personInfoResponse);
        personInfoResponse.setDobText(personInfoResponse.setDate(personInfoResponse.getDateOfBirth()));
//        return personInfoResponse;
        return getUserInfo(authentication.getName());
    }

    private PersonInfoResponse getUserInfo(String username) {
        Staff staff = staffRepository.findByUserName(username)
                .orElseThrow(()-> new PersonNotFoundException("Person Not Found"));
//        Address address = addressRepository.findFirstByPerson(person)
//                .orElseThrow(()-> new AddressNotFoundException("Address Not Found"));
        PersonInfoResponse personInfoResponse = new PersonInfoResponse();
//        AddressRequest addressRequest = new AddressRequest();
//        modelMapper.map(address, addressRequest);
        modelMapper.map(staff, personInfoResponse);
//        personInfoResponse.setAddress(addressRequest);
        personInfoResponse.setDobText(personInfoResponse.setDate(personInfoResponse.getDateOfBirth()));
        return personInfoResponse;
    }

    @Override
    public UpdatePersonResponse updateUserDetails(UpdatePersonRequest updatePersonRequest) {
        Staff existingStaff = staffRepository.findPersonByUserName(updatePersonRequest.getUserName())
                .orElseThrow(
                        () -> new PersonNotFoundException("Person Not Found")
                );
        modelMapper.map(updatePersonRequest, existingStaff);
        staffRepository.save(existingStaff);
        UpdatePersonResponse response = new UpdatePersonResponse();
        modelMapper.map(existingStaff,response);
        return response;
    }

    @Override
    public PersonResponse register(PersonRequest personRequest) throws IOException {
        boolean isValidEmail = emailValidator.test(personRequest.getEmail());
        if(!isValidEmail){
            return PersonResponse.builder().message("Not a valid email").build();
        }

        boolean isValidNumber = emailValidator.validatePhoneNumber(personRequest.getPhoneNumber());

        if(!isValidNumber){
            return PersonResponse.builder().message("Not a valid phone Number").build();
        }

        boolean userExists = staffRepository.findByEmail(personRequest.getEmail()).isPresent();
        if(userExists){
            return PersonResponse.builder().message("email taken").build();
        }

        Staff staff = new Staff();
        modelMapper.map(personRequest, staff);

        final String encodedPassword = bcryptPasswordEncoder.encode(personRequest.getPassword());
        staff.setPassword(encodedPassword);
        String token = RandomString.make(64);
        staff.setResetPasswordToken(token);

        staffRepository.save(staff);
        sendingEmail(personRequest.getEmail());
        return PersonResponse.builder().firstName(staff.getFirstName()).lastName(staff.getLastName())
                .email(staff.getEmail()).message("Successful") .build();    }

    @Override
    public ChangePasswordResponse updateCurrentPassword(ChangePasswordRequest changePasswordRequest) {
        Staff currentStaff = staffRepository.findByUserName(changePasswordRequest.getUserName())
                .orElseThrow(()-> new PersonNotFoundException("Person Not Found"));
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmPassword();
        if(bcryptPasswordEncoder.matches(changePasswordRequest.getCurrentPassword(), currentStaff.getPassword())){
            if (newPassword.equals(confirmPassword)) {
                currentStaff.setPassword(bcryptPasswordEncoder.encode(newPassword));
                staffRepository.save(currentStaff);
                return new ChangePasswordResponse("Password successfully changed");
            }
            else { return new ChangePasswordResponse("Confirm password does not match proposed password");}
        }
        else {
            return new ChangePasswordResponse("Incorrect current password");
        }
    }

    @Override
    public void resetPasswordMailSender(String email, String token) {
        String resetPasswordLink = "http://"+ website + ":" + port + "/update_password?token=" + token;
        String subject = "Here's the link to reset your password";
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + resetPasswordLink + "\">Change my password</a></p>"
                + "<br>"
                + "<p> Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
        emailSender.sendMessage(subject, email, content);
    }

    @Override
    public Page<Staff> getAllUsers(int pageNumber) {
        final List<Staff> personList = staffRepository.findAll();
        int pageSize = 10;
        int skipCount = (pageNumber - 1) * pageSize;
        List<Staff> usersPaginated = personList
                .stream()
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());
        Pageable usersPage = PageRequest.of(pageNumber, pageSize, Sort.by("firstName").ascending());
        return new PageImpl<>(usersPaginated, usersPage, personList.size());
    }


    @Override
    public PersonResponse resetPasswordToken(String email) {
        Staff person = staffRepository.findByEmail(email)
                .orElseThrow(()-> new PersonNotFoundException("Email not Registered"));
        String token = RandomString.make(64);
        person.setResetPasswordToken(token);
        staffRepository.save(person);
        resetPasswordMailSender(person.getEmail(), token);
        return PersonResponse.builder().message("email sent").build();
    }

    @Override
    public PersonResponse updateResetPassword(ResetPasswordRequest passwordRequest, String token) {
        Staff staff = staffRepository.findByResetPasswordToken(token)
                .orElseThrow(()-> new PersonNotFoundException("Person not found"));
        if(passwordRequest.getNewPassword().equals(passwordRequest.getConfirmPassword())){
            staff.setPassword(bcryptPasswordEncoder.encode(passwordRequest.getNewPassword()));
            staffRepository.save(staff);
            return PersonResponse.builder().message("updated").build();
        }
        return PersonResponse.builder().message("mismatch of new and confirm password").build();
    }

    @Override
    public PersonResponse sendingEmail(String email) {

    }
}
