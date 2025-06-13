package com.foodie.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Event name is required")
    @Size(max = 100, message = "Event name must be less than 100 characters")
    private String eventName;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must be less than 200 characters")
    private String location;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL must be less than 500 characters")
    private String imageUrl;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}