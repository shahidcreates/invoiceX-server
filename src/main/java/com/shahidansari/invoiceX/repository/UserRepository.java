package com.shahidansari.invoiceX.repository;

import com.shahidansari.invoiceX.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {


    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String verificationToken);

    boolean existsByEmail(String email);
}
