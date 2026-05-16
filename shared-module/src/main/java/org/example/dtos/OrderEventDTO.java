package org.example.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public class OrderEventDTO implements Serializable{
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long orderId;
    private String customerName;
    private Double totalPrice;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String status;

    // Default konstruktor je obavezan za Jackson
    public OrderEventDTO() {}

    public OrderEventDTO(Long orderId, String customerName, Double totalPrice, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderEventDTO{" +
                "orderId=" + orderId +
                ", customerName='" + customerName + '\'' +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                '}';
    }
}
