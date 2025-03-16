package com.foodie.service;

import java.util.List;

import com.foodie.dto.SalesReportDTO;
import com.foodie.dto.UserProfileDTO;

public interface AdminService {

    List<UserProfileDTO> getAllUsers(String token);
    UserProfileDTO updateUserRole(String token, Long userId, String newRole);
//    List<AuditLogDto> getAuditLogs(String token, LocalDate start, LocalDate end);
//    SalesReportDTO generateSalesReport(String token, DateRangeRequest request);
//    void sendSystemNotification(String token, NotificationRequest request);

}

