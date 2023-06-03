package com.example.Ecoboard.Ecoboard.repository;

import com.example.Ecoboard.Ecoboard.Model.user.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findPersonByPassword(String password);
    Optional<Staff> findPersonByUserName(String username);
    Optional<Staff> findPersonByUserNameAndPassword(String username,String password);

    Optional<Staff> findByUserName(String userName);
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByResetPasswordToken(String token);
}
