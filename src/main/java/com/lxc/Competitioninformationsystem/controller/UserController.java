package com.lxc.Competitioninformationsystem.controller;

import com.lxc.Competitioninformationsystem.entity.Notification;
import com.lxc.Competitioninformationsystem.entity.User;
import com.lxc.Competitioninformationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
// 设置允许跨域
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/add")
    private User addUser(User user) {
        return userService.saveUser(user);
    }

    @GetMapping("/delById")
    private String delUserById(Integer id) {
        userService.deleteUserById(id);
        return "SUCCESS";
    }

    @GetMapping("/delByName")
    private String delUserByName(String name) {
        userService.deleteUserByName(name);
        return "SUCCESS";
    }

//    @GetMapping("/update")
//    private User updateUser(String name, String password, String email, String iconUrl) {
//        User user = userService.findUserByName(name);
//        user.setPassword(password);
//        user.setEmail(email);
//        user.setIconUrl(iconUrl);
//        return userService.updateUser(user);
//    }

    @GetMapping("/findById")
    private User findUserById(Integer id) {
        return userService.findUserById(id);
    }

    @GetMapping("/findByName")
    private String findUserByName(@RequestParam("userName") String name) {
        User user = userService.findUserByName(name);
        if(user != null) {
            return "User name already exists";
        } else {
            return "OK";
        }
    }

    @PostMapping("/login")
    private Object login(@RequestParam("userName") String name, @RequestParam("password") String password) {
        User user = userService.login(name, password);
        if(user != null) {
            return user;
        } else {
            return "Login failed";
        }
    }

    @PostMapping("/register")
    private String register(@RequestParam("userName") String name, @RequestParam("password") String pwd,
                            @RequestParam("email") String email, @RequestParam("registerTime") Timestamp time) {
        int result = userService.register(name, pwd, email, time);
        if(result != -1) {
            return "REGISTER SUCCESS";
        } else {
            return "REGISTER FAILED";
        }
    }

    @GetMapping("/pullNotification")
    private List<Notification> pullNotification() {
        // 返回最新的10条数据
        return redisTemplate.opsForList().range("notification", 0, 10).stream()
                .map(value -> (Notification) value).collect(Collectors.toList());
    }

    @GetMapping("/getLastNotifyTime")
    private String getLastNotifyTime() {
        return stringRedisTemplate.opsForValue().get("lastNotify");
    }
}
