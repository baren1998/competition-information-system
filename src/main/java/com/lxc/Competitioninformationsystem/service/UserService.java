package com.lxc.Competitioninformationsystem.service;

import com.lxc.Competitioninformationsystem.entity.User;

import java.sql.Timestamp;

public interface UserService {
    User saveUser(User user);
    void deleteUserById(Integer id);
    User updateUser(User user);
    User findUserById(Integer id);
    void deleteUserByName(String userName);
    User findUserByName(String userName);
    User login(String userName, String password);
    int register(String userName, String password, String email, Timestamp registerTime);
}
