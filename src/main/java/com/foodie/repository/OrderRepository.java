package com.foodie.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.foodie.model.Order;
import com.foodie.model.OrderStatus;
import com.foodie.model.Restaurant;
import com.foodie.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
    List<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status); // This should work now
    
//    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
//    List<Order> findOrdersBetweenDates(@Param("start") LocalDateTime start, 
//                                      @Param("end") LocalDateTime end);
	List<Order> findByRestaurant(Restaurant restaurant);
	
//	List<Order> findByRestaurantAndOrderDate(Restaurant restaurant, LocalDateTime atStartOfDay, LocalDateTime atTime);

    Page<Order> findByRestaurantAndStatus(Restaurant restaurant, OrderStatus status, Pageable pageable); // Pagination with filter by status

    Page<Order> findByRestaurant(Restaurant restaurant, Pageable pageable); // Pagination without filter

}
