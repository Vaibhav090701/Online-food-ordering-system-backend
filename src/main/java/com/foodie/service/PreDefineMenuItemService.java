package com.foodie.service;

import com.foodie.dto.PreDefineMenuItemDTO;
import com.foodie.request.PreDefineMenuItemRequest;

import java.util.List;

public interface PreDefineMenuItemService {
    PreDefineMenuItemDTO createPreDefineMenuItem(PreDefineMenuItemRequest req, String email);
    void deletePreDefineMenuItem(String email, long id);
    PreDefineMenuItemDTO updatePreDefineMenuItem(PreDefineMenuItemRequest req, String email, long id);
    List<PreDefineMenuItemDTO> getAllPreDefineMenuItems(String email);
    List<PreDefineMenuItemDTO> getAllCategoryWisePreDefineMenuItems(long categoryId, String email);
}