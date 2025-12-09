package com.ucop.dao;

import com.ucop.entity.User;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    /** LOGIN QUERY */
    public User findByUsernameOrEmail(String input) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "FROM User u " +
                    "LEFT JOIN FETCH u.roles " +
                    "LEFT JOIN FETCH u.profile " +
                    "WHERE lower(u.username) = lower(:x) OR lower(u.email) = lower(:x)",
                    User.class
            )
            .setParameter("x", input)
            .uniqueResult();
        }
    }
}
