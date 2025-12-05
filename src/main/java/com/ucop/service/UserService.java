package com.ucop.service;

import com.ucop.dao.UserDAO;
import com.ucop.entity.User;

import java.util.List;

public class UserService {

    private final UserDAO dao = new UserDAO();

    public void save(User user){
        dao.save(user);
    }

    public void update(User user){
        dao.update(user);
    }

    public void delete(User user){
        dao.delete(user);
    }

    public List<User> findAll(){
        return dao.findAll();
    }

    public User findById(int id){
        return dao.findById(id);
    }

    // ⭐ THÊM HÀM LOGIN QUAN TRỌNG
    public User findByUsernameOrEmail(String input){
        return dao.findByUsernameOrEmail(input);
    }
}