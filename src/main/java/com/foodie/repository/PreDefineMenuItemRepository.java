package com.foodie.repository;

import com.foodie.model.MenuCategory;
import com.foodie.model.PreDefineMenuItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreDefineMenuItemRepository extends JpaRepository<PreDefineMenuItems, Long> {
    List<PreDefineMenuItems> findByMenuCategoryAndDeletedFalse(MenuCategory menuCategory);
    List<PreDefineMenuItems> findByDeletedFalse();
    Optional<PreDefineMenuItems> findByIdAndDeletedFalse(Long id);
}