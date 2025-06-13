package com.foodie.dto;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private int status;

    // Constructor for success response
    public ApiResponse(T data, String message, int status) {
        this.success = true;
        this.data = data;
        this.message = message;
        this.status = status;
    }

    // Constructor for error response
    public ApiResponse(String message, int status) {
        this.success = false;
        this.message = message;
        this.status = status;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}