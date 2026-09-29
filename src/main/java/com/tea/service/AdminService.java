package com.tea.service;

import com.tea.dao.AdminDao;
import com.tea.entity.Admin;

import java.sql.SQLException;

public class AdminService {
    private final AdminDao adminDao =new AdminDao();
    public Admin login(String username,String password){
        if(isblank(username)||isblank(password)){
            return null;
        }
        try {
            return adminDao.findByUsernameAndPassword(username.trim(),password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean register(String username,String password){
        if(isblank(username)||isblank(password)){
            throw  new IllegalArgumentException("用户名或密码不能为空");
        }
        if(password.length()<6){
            throw new IllegalArgumentException("密码长度必须>=6位");
        }
        if(adminDao.existsByUsername(username)){
            throw new IllegalArgumentException("用户名已被使用");
        }
        Admin a = new Admin(username.trim(),password);
        return adminDao.insert(a)==1;
    }
    private boolean isblank(String s){
        return s==null||s.trim().isEmpty();
    }
}
