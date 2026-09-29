package com.tea;

import com.tea.util.Dbutil;

import java.sql.Connection;
import java.sql.SQLException;

public class Main{
    public static void main(String[] args) {
        try {
            Connection conn= Dbutil.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } {
            System.out.println("连接成功");
        }
    }
}