package com.ynm.usermanagementservice.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    String accessToken;
    String refreshToken;
}
