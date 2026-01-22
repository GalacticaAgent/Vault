package com.vault.dto.request;

import lombok.Data;

@Data
public class CreateCardRequest {
    private String title;
    private String query;
    private Integer refreshInterval;
    private Integer cardOrder;
}

