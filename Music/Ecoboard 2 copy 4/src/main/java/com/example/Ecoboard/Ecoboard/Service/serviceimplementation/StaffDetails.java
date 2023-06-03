package com.example.Ecoboard.Ecoboard.Service.serviceimplementation;

import com.example.Ecoboard.Ecoboard.Model.user.Staff;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class StaffDetails implements UserDetails {

    private String name;
    private String password;
    private boolean active;


    public StaffDetails(Staff staff) {
        this.name = staff.getUserName();
        this.password = staff.getPassword();
        this.active = staff.isVerifyEmail();
    }

    public StaffDetails() {
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}