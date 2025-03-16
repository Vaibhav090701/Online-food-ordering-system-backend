package com.foodie.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class DailySalesDTO {

    private LocalDate date;
    private double salesAmount;

}
