package com.ynm.searchservice.dto;

import lombok.Data;

@Data
public class UserScoreSyncRequest {
    private Long userId;
    private Float lastUpdate;
    private Float newUpdate;
}
