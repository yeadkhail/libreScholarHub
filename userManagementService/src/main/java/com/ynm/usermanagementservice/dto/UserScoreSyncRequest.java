package com.ynm.usermanagementservice.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class UserScoreSyncRequest {
    private Integer userId;
    private Float lastUpdate;
    private Float newUpdate;
}
