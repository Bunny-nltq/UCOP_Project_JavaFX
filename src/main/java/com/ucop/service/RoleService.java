package com.ucop.service;

import com.ucop.dao.RoleDAO;
import com.ucop.entity.Role;

import java.util.List;

public class RoleService {

    private final RoleDAO dao = new RoleDAO();

    public List<Role> findAll() {
        return dao.findAll();
    }

    public Role findByName(String name) {
        return dao.findByName(name);
    }
}
