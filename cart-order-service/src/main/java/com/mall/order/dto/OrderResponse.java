package com.mall.order.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderResponse {
    private Long id;
    private Long customerId;
    private BigDecimal totalAmount;
    private String status;
}
