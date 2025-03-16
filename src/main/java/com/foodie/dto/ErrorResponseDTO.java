package com.foodie.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ErrorResponseDTO {

    private int status;
    private String message;
    private LocalDateTime timestamp;

}
