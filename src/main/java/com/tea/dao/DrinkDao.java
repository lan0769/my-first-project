package com.tea.dao;
import com.tea.util.Dbutil;
import com.tea.entity.Drink;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class DrinkDao {
    public List<Drink> listAllActive(){
    String sql="SELECT id,name,category,price,stock,is_active"+"FROM drink WHERE is_active =1 ORDRE BYcategory,id";
    List<Drink> list= new ArrayList<>();
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
    }catch (SQLException e){
            throw new RuntimeException("查询奶茶列表失败",e);
        }
        return list;
    }
    public Drink findById(Long id){
        String sql="SSELECT is,name,category,price,stock,is_active"+"FROM drink WHERE id =?";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询奶茶失败", e);
        }
    }
    public int insert(Drink d) {
        String sql = "INSERT INTO drink (name, category, price, stock, is_active) "
                + "VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCategory());
            ps.setBigDecimal(3, d.getPrice());
            ps.setInt(4, d.getStock());
            int n = ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getLong(1));
            }
            return n;
        } catch (SQLException e) {
            throw new RuntimeException("新增奶茶失败", e);
        }
    }

    public int updatePriceAndStock(Long id, java.math.BigDecimal price, Integer stock) {
        String sql = "UPDATE drink SET price = ?, stock = ? WHERE id = ?";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, price);
            ps.setInt(2, stock);
            ps.setLong(3, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("修改奶茶失败", e);
        }
    }


    public int deactivate(Long id) {
        String sql = "UPDATE drink SET is_active = 0 WHERE id = ?";
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("下架奶茶失败", e);
        }
    }


    public List<Drink> listLowStock(int threshold) {
        String sql = "SELECT id, name, category, price, stock, is_active "
                + "FROM drink WHERE is_active = 1 AND stock < ? ORDER BY stock";
        List<Drink> list = new ArrayList<>();
        try (Connection conn = Dbutil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询库存预警失败", e);
        }
        return list;
    }

    private Drink map(ResultSet rs) throws SQLException {
        Drink d = new Drink();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        d.setCategory(rs.getString("category"));
        d.setPrice(rs.getBigDecimal("price"));
        d.setStock(rs.getInt("stock"));
        d.setIsActive(rs.getInt("is_active"));
        return d;
    }
}

