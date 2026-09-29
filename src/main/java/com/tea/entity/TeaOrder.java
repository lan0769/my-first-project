package com.tea.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TeaOrder {
    private Long id;
    private Long memberId;
    private BigDecimal totalAmount=BigDecimal.valueOf(0);
    private String status;
    private LocalDateTime createdAt;

    public TeaOrder(Long memberId, BigDecimal totalAmount, String status) {
        this.memberId = memberId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public TeaOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Order[%d] member=%s ¥%s %s",
                id, memberId, totalAmount.toPlainString(), status);
    }
}
