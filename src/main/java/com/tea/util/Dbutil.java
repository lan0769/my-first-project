package com.tea.util;

import com.sun.jdi.PathSearchingVirtualMachine;

import java.security.PublicKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dbutil {
    private static final String URL =
            "jdbc:mysql://localhost:3306/milktea_saga"
                    + "?useUnicode=true&characterEncoding=UTF-8"
                    + "&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Mysql驱动加载失败，请检查pom.xml", e);

        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    private Dbutil(){}
}