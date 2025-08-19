package com.ynm.usermanagementservice.dao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    String email;
    String password;
}
