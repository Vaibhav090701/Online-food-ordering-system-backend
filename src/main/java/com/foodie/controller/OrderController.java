package com.foodie.controller;

import com.foodie.dto.OrderDTO;
import com.foodie.model.OrderStatus;
import com.foodie.request.OrderRequest;
import com.foodie.service.OrderService;
import com.foodie.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    

    // Place a new order
    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody OrderRequest request) {
        OrderDTO orderDTO = null;
		try {
			orderDTO = orderService.placeOrder(token, request);
//			System.out.println(orderDTO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new ResponseEntity<>(orderDTO, HttpStatus.CREATED);
    }

    // Get order details
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetails(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.getOrderDetails(token, orderId);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    // Get order history
    @GetMapping("/history")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(@RequestHeader("Authorization") String token) {
        List<OrderDTO> orderHistory = orderService.getOrderHistory(token);
        return new ResponseEntity<>(orderHistory, HttpStatus.OK);
    }

    // Cancel an order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<OrderDTO> cancelOrder(
            @RequestHeader("Authorization") String token,
            @PathVariable Long orderId) {
        OrderDTO orderDTO = orderService.cancelOrder(token, orderId);
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

}
