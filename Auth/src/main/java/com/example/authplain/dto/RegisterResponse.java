package com.example.authplain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String status;
    private String message;
    private Integer user_id;
}
