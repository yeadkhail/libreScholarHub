package com.ynm.usermanagementservice.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    String fullName;
    String email;
    String password;
}
