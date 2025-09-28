package com.ynm.usermanagementservice.dto;

import lombok.Data;

@Data
public class UserScoreSyncRequest {
    private String email;
    private Float lastUpdate;
    private Float newUpdate;
}
