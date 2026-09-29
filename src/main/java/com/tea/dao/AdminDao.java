package com.tea.dao;

import com.tea.entity.Admin;
import com.tea.util.Dbutil;

import java.sql.*;

public class AdminDao {
    public Admin findByUsernameAndPassword(String username,String password) throws SQLException {
    String sql="SELECT id,username,password,created_at"+"FROM admin WHERE username =?AND password=?";
        try(Connection conn= Dbutil.getConnection();
        PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,username);
            ps.setString(2,password);
        try(ResultSet rs =ps.executeQuery()){
            if(rs.next()){
                Admin a=new Admin();
                a.setId(rs.getLong("id"));
                a.setUsername(rs.getString("username"));
                a.setPassword(rs.getString("password"));
                Timestamp ts=rs.getTimestamp("created_at");
                if(ts!=null){
                    a.setCreatedAt(ts.toLocalDateTime());
                }
                return a;
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException("查询店员失败",e);
        }
    }
    }
    public boolean existsByUsername(String username){
    String sql ="SELECT 1 FROM admin WHERE username=? LIMIT 1";
        try (Connection conn=Dbutil.getConnection();
        PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,username);
           try(ResultSet rs=ps.executeQuery()){
               return rs.next();
           }

        }catch (SQLException e){
            throw new RuntimeException("判断用户名是否存在失败",e);
        }
    }
    public int insert(Admin admin){
        String sql="INSERT INTO admin (username,password) VALUES (?,?)";
        try (Connection conn=Dbutil.getConnection();
             PreparedStatement ps=conn.prepareStatement(sql)){
            ps.setString(1,admin.getUsername());
            ps.setString(2,admin.getPassword());
            return ps.executeUpdate();}
        catch (SQLException e){
            throw new RuntimeException("新增店员失败",e);

        }



    }
}
