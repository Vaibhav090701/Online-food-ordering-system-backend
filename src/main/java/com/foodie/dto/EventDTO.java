package com.foodie.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private Long id;
    private String eventName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String imageUrl;
    private Long restaurantId;
    private boolean deleted;
}