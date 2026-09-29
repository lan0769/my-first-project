package com.tea.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.PriorityQueue;

public class Drink {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private Integer isActive;
    private LocalDateTime createdAt;

    public Drink() {
    }

    public Drink(String name, String category, BigDecimal price, Integer stock) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stock = stock;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-12s ¥%-6s 库存 %d",
                id, name, price.toPlainString(), stock);
    }
}
