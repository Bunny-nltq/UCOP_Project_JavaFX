package com.ucop.dao;

import com.ucop.entity.Role;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;

public class RoleDAO extends GenericDAO<Role> {

    public RoleDAO() {
        super(Role.class);
    }

    public Role findByName(String name) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "FROM Role r WHERE r.name = :name", Role.class
            )
            .setParameter("name", name)
            .uniqueResult();
        }
    }
}
