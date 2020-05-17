package com.lxc.Competitioninformationsystem.repository;

import com.lxc.Competitioninformationsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "select * from user where name = :name", nativeQuery = true)
    User findUserByName(@Param("name") String userName);

    @Query(value = "select * from user where name = :name and password = :pwd", nativeQuery = true)
    User login(@Param("name") String userName, @Param("pwd") String password);

    @Modifying
    @Query(value = "delete from user where name = :name", nativeQuery = true)
    void deleteUserByName(@Param("name") String userName);

    @Modifying
    @Query(value = "insert into user (name, password, email, register_time) values (:name, :pwd, :email, :time)", nativeQuery = true)
    int register(@Param("name") String userName,@Param("pwd") String password,@Param("email") String email, @Param("time") Timestamp registerTime);

//    @Modifying
//    @Query(value = "update user set password = :password,email = :email,preference = :perference where name = :name", nativeQuery = true)
//    int updateUserByName(@Param("name") String userName, @Param("password") String password,
//                         @Param("email") String email, @Param("preference") String preference);
}
