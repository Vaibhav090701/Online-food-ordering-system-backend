package com.foodie.controller;

import com.foodie.dto.ApiResponse;
import com.foodie.dto.OrderDTO;
import com.foodie.model.OrderStatus;
import com.foodie.request.OrderRequest;
import com.foodie.service.OrderService;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> placeOrder(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @Valid @RequestBody OrderRequest request) {
        try {
            OrderDTO orderDTO = orderService.placeOrder(email, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(orderDTO, "Order placed successfully", HttpStatus.CREATED.value()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Payment processing failed: " + e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get order details
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderDetails(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long orderId) {
        try {
            OrderDTO orderDTO = orderService.getOrderDetails(email, orderId);
            return ResponseEntity.ok(new ApiResponse<>(orderDTO, "Order details retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get order history
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrderHistory(
            @CurrentSecurityContext(expression = "authentication?.name") String email) {
        try {
            List<OrderDTO> orderHistory = orderService.getOrderHistory(email);
            return ResponseEntity.ok(new ApiResponse<>(orderHistory, "Order history retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Cancel an order
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long orderId) {
        try {
            OrderDTO orderDTO = orderService.cancelOrder(email, orderId);
            return ResponseEntity.ok(new ApiResponse<>(orderDTO, "Order cancelled successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Get restaurant orders (admin only)
    @GetMapping("/restaurant")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getRestaurantOrders(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<OrderDTO> orders = orderService.getRestaurantOrders(email, status, page, size);
            return ResponseEntity.ok(new ApiResponse<>(orders, "Restaurant orders retrieved successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // Update order status (admin only)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @CurrentSecurityContext(expression = "authentication?.name") String email,
            @PathVariable Long orderId,
            @RequestParam String status) {
        try {
            OrderDTO orderDTO = orderService.updateOrderStatus(email, orderId, status);
            return ResponseEntity.ok(new ApiResponse<>(orderDTO, "Order status updated successfully", HttpStatus.OK.value()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(new ApiResponse<>(e.getReason(), e.getStatusCode().value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}