package com.foodie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodie.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

}
