package com.foodie.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SalesReportDTO {

    private double totalSales;
    private int totalOrders;
    private Map<String, Double> categoryWiseSales; // Pizza: 40%, Burgers: 30%, etc.
    private List<DailySalesDTO> dailySales;

}
