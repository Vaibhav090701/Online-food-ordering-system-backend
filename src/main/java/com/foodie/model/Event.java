package com.foodie.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String imageUrl;
    private boolean deleted;

    @ManyToOne
    private Restaurant restaurant;
}