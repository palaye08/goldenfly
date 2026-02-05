package com.goldenfly.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour le callback Orange Money
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrangeMoneyCallbackDto {
    private String reference;
    private String transactionId;
    private String status;
    private String message;
    private Double amount;
}