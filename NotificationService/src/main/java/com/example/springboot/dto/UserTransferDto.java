package com.example.springboot.dto;

import lombok.Data;

@Data
public class UserTransferDto {
    private Long id;
    private String name;
    private String email;
}