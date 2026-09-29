package com.tea.dao;

import com.tea.entity.Member;
import com.tea.util.Dbutil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao {
    public Member findByPhone(String phone){
        String sql ="SELECT id,name,phone,points,cerated_at"+"FROM member WHERE phone = ?";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("按手机号查会员失败", e);
        }
    }
    public Member findById(Long id){
        String sql = "SELECT id, name, phone, points, created_at FROM member WHERE id = ?";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("按 ID 查会员失败", e);
        }
    }
    public List<Member> listAll() {
        String sql = "SELECT id, name, phone, points, created_at "
                + "FROM member ORDER BY id DESC";
        List<Member> list = new ArrayList<>();
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException("查询会员列表失败", e);
        }
        return list;
    }
    public int insert(Member m) {
        String sql = "INSERT INTO member (name, phone, points) VALUES (?, ?, ?)";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getName());
            ps.setString(2, m.getPhone());
            ps.setInt(3, m.getPoints() == null ? 0 : m.getPoints());
            int n = ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getLong(1));
            }
            return n;
        } catch (SQLException e) {
            throw new RuntimeException("新增会员失败", e);
        }
    }
    public int updatePoints(Long id, Integer points) {
        String sql = "UPDATE member SET points = ? WHERE id = ?";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setLong(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新积分失败", e);
        }
    }
    private Member map(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(rs.getLong("id"));
        m.setName(rs.getString("name"));
        m.setPhone(rs.getString("phone"));
        m.setPoints(rs.getInt("points"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) m.setCreatedAt(ts.toLocalDateTime());
        return m;
    }
}
