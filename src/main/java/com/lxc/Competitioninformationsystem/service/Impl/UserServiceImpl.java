package com.lxc.Competitioninformationsystem.service.Impl;

import com.lxc.Competitioninformationsystem.entity.User;
import com.lxc.Competitioninformationsystem.repository.UserRepository;
import com.lxc.Competitioninformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserById(Integer id) {
        return userRepository.findById(id).get();
    }

    @Override
    public void deleteUserByName(String userName) {
        userRepository.deleteUserByName(userName);
    }

    @Override
    public User findUserByName(String userName) {
        return userRepository.findUserByName(userName);
    }

    @Override
    public User login(String userName, String password) {
        return userRepository.login(userName, password);
    }

    @Override
    public int register(String userName, String password, String email, Timestamp registerTime) {
        return userRepository.register(userName, password, email, registerTime);
    }
}
