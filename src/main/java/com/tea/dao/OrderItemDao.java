package com.tea.dao;

import com.tea.entity.OrderItem;
import com.tea.util.Dbutil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class OrderItemDao {

    public void insertWithConn(Connection conn, OrderItem item) throws SQLException {
        String sql = "INSERT INTO order_item "
                + "(order_id, drink_id, quantity, unit_price, subtotal) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getOrderId());
            ps.setLong(2, item.getDrinkId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, item.getUnitPrice());
            ps.setBigDecimal(5, item.getSubtotal());
            ps.executeUpdate();
        }
    }
    public List<OrderItem> findByOrderIdWithDrinkName(Long orderId) {
        String sql = "SELECT oi.id, oi.order_id, oi.drink_id, oi.quantity, "
                + "       oi.unit_price, oi.subtotal, d.name AS drink_name "
                + "FROM order_item oi "
                + "JOIN drink d ON d.id = oi.drink_id "
                + "WHERE oi.order_id = ? "
                + "ORDER BY oi.id";
        List<OrderItem> list = new ArrayList<>();
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem it = new OrderItem();
                    it.setId(rs.getLong("id"));
                    it.setOrderId(rs.getLong("order_id"));
                    it.setDrinkId(rs.getLong("drink_id"));
                    it.setQuantity(rs.getInt("quantity"));
                    it.setUnitPrice(rs.getBigDecimal("unit_price"));
                    it.setSubtotal(rs.getBigDecimal("subtotal"));
                    it.setDrinkName(rs.getString("drink_name"));
                    list.add(it);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询订单明细失败", e);
        }
        return list;
    }
}