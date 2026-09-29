package com.tea.entity;

import java.math.BigDecimal;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long drinkId;
    private Integer quantity;
    private BigDecimal unitPrice=BigDecimal.valueOf(0);
    private BigDecimal subtotal=BigDecimal.valueOf(0);
    private String drinkName;

    public OrderItem(Long drinkId, Integer quantity, BigDecimal unitPrice) {

        this.drinkId = drinkId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;

    }

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(Long drinkId) {
        this.drinkId = drinkId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    @Override
    public String toString() {
        return String.format("  %s × %d   ¥%s = ¥%s",
                drinkName != null ? drinkName : ("drink#" + drinkId),
                quantity, unitPrice.toPlainString(), subtotal.toPlainString());
    }
}
