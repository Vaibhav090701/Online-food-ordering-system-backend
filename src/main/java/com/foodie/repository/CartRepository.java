package com.foodie.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.Cart;
import com.foodie.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);

}
