package com.tea.service;

import com.tea.dao.MemberDao;
import com.tea.entity.Member;

import java.util.List;

public class MemberService {
    private final MemberDao memberDao=new MemberDao();
    public Member findByPhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        return memberDao.findByPhone(phone.trim());
    }
    public Member findById(Long id) {
        return memberDao.findById(id);
    }

    public List<Member> listAll() {
        return memberDao.listAll();
    }
    public boolean addMember(String name, String phone) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("姓名不能为空");
        if (phone == null || !phone.matches("^\\d{11}$"))
            throw new IllegalArgumentException("手机号必须是 11 位数字");
        if (memberDao.findByPhone(phone) != null)
            throw new IllegalArgumentException("该手机号已注册");
        Member m = new Member(name.trim(), phone);
        return memberDao.insert(m) == 1;
    }
}
