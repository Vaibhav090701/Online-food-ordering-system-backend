package com.foodie.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.Role;
import com.foodie.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    boolean existsByEmail(String email);
	boolean existsByUsername(String username);

}
