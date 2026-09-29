package com.tea.service;

import com.tea.dao.MemberDao;
import com.tea.dao.OrderItemDao;
import com.tea.dao.TeaOrderDao;
import com.tea.entity.Drink;
import com.tea.entity.OrderItem;
import com.tea.entity.TeaOrder;
import com.tea.util.Dbutil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final TeaOrderDao teaOrderDao=new TeaOrderDao();
    private final OrderItemDao orderItemDao=new OrderItemDao();
    private final DrinkService drinkService =new DrinkService();
    private final MemberService memberService=new MemberService();
    public record PlaceOrderResult(Long OrderId, BigDecimal totalAmount,Integer pointsAfter) {}
        public PlaceOrderResult placeOrder(Long memberId, List<OrderItem> items) {
            if (items == null || items.isEmpty()) {
                throw new IllegalArgumentException("订单至少要 1 杯");
            }
            for (OrderItem it : items) {
                Drink d = drinkService.findById(it.getDrinkId());
                if (d == null || d.getIsActive() == 0) {
                    throw new IllegalArgumentException("奶茶不存在或已下架：id=" + it.getDrinkId());
                }
                if (d.getStock() < it.getQuantity()) {
                    throw new IllegalArgumentException(
                            String.format("库存不足：%s 仅剩 %d 杯", d.getName(), d.getStock()));
                }
            }
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItem it : items) {
                total = total.add(it.getSubtotal());
            }
            Connection conn = null;
            try {
                conn = Dbutil.getConnection();
                conn.setAutoCommit(false);
                TeaOrder order = new TeaOrder();
                order.setMemberId(memberId);
                order.setTotalAmount(total);
                order.setStatus("PAID");
                Long orderId = teaOrderDao.insertWithConn(conn, order);

                for (OrderItem it : items) {
                    it.setOrderId(orderId);
                    orderItemDao.insertWithConn(conn, it);
                    decrementStock(conn, it.getDrinkId(), it.getQuantity());
                }
                Integer pointsAfter = null;
                if (memberId != null) {
                    int delta = total.intValue();
                    pointsAfter = addPointsInTx(conn, memberId, delta);
                }
                conn.commit();
                return new PlaceOrderResult(orderId, total, pointsAfter);

            } catch (Exception e) {

                if (conn != null) {
                    try { conn.rollback(); } catch (SQLException ignore) {}
                }
                throw new RuntimeException("下单失败，已回滚：" + e.getMessage(), e);
            } finally {
                if (conn != null) {
                    try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
                    try { conn.close(); } catch (SQLException ignore) {}
                }
            }
        }
    private void decrementStock(Connection conn, Long drinkId, int qty) throws SQLException {
        String sql = "UPDATE drink SET stock = stock - ? "
                + "WHERE id = ? AND stock >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, qty);
            ps.setLong(2, drinkId);
            ps.setInt(3, qty);
            int n = ps.executeUpdate();
            if (n != 1) {

                throw new SQLException("扣库存失败（可能并发超卖）：drinkId=" + drinkId);
            }
        }
    }


    private int addPointsInTx(Connection conn, Long memberId, int delta) throws SQLException {
        String sql = "UPDATE member SET points = points + ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setLong(2, memberId);
            ps.executeUpdate();
        }

        String q = "SELECT points FROM member WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setLong(1, memberId);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }



    public TeaOrder findOrderById(Long id) {
        return teaOrderDao.findById(id);
    }

    public List<OrderItem> findItemsByOrderId(Long orderId) {
        return orderItemDao.findByOrderIdWithDrinkName(orderId);
    }
}


