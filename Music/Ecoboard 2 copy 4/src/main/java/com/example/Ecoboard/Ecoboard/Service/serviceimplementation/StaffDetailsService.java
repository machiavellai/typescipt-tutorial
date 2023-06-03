package com.example.Ecoboard.Ecoboard.Service.serviceimplementation;

import com.example.Ecoboard.Ecoboard.Model.user.Staff;
import com.example.Ecoboard.Ecoboard.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StaffDetailsService implements UserDetailsService {
    private final StaffRepository staffRepository;

    @Autowired
    public StaffDetailsService(StaffRepository personRepository) {
        this.staffRepository = personRepository;
    }
    @Override
    public StaffDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Staff> staff = staffRepository.findByUserName(userName);
        staff.orElseThrow(()-> new UsernameNotFoundException("Not Found: " + userName));
        return staff.map(StaffDetails::new).get();

    }
    }
