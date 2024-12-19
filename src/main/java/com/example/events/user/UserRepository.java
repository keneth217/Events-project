package com.example.events.user;

import com.example.events.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    User findByRole(Role role);

    @Query("SELECT u FROM User u JOIN FETCH u.registrations WHERE u.id = :userId")
    Optional<User> findUserWithRegistrations(UUID userId);
};
