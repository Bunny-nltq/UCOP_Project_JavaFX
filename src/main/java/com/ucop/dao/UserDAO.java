package com.ucop.dao;

import com.ucop.entity.User;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public User findByUsernameOrEmail(String input) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "FROM User u WHERE u.username = :x OR u.email = :x",
                    User.class
            )
            .setParameter("x", input)
            .uniqueResult();
        }
    }
}
