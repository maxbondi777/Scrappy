package com.scrappy.scrappy.controller.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private T data;
    private String error;

    public ApiResponse(T data, String error) {
        this.data = data;
        this.error = error;
    }
}