package com.lagou.dao;

import com.lagou.pojo.User;

import java.util.List;

public interface IUserDao {

    public List<User> findAll();

    public User findByCondition(User user);

    public int addUser(User user);

    public int updateUser(User user);

    public int deleteUser(User user);
}
