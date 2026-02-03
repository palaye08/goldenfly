package com.goldenfly.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaveCallbackDto {
    private String id; // Transaction ID Wave
    private String status; // success, failed, pending
    private Double amount;
    private String currency;
    private String reference; // Notre numeroPaiement
}