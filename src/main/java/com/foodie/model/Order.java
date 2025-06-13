package com.foodie.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;
    
    private LocalDateTime orderDate;
    private double totalAmount;
    private boolean deleted;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    // Relationship: Delivery address for the order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;
    
    @ManyToOne // Add this field to relate to Restaurant
    @JoinColumn(name = "restaurant_id") // Specify the column name for the foreign key
    private Restaurant restaurant;  // <-- Add the restaurant field here.


    
    // constructors, getters, setters
}
