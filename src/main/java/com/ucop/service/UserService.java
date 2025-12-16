package com.ucop.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ucop.dao.UserDAO;
import com.ucop.entity.AccountProfile;
import com.ucop.entity.Role;
import com.ucop.entity.User;
import com.ucop.util.HibernateUtil;

public class UserService {

    private final UserDAO dao = new UserDAO();

    public List<User> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from User", User.class).list();
        }
    }

    /** Save new user */
    public void save(User user) {
        Transaction tx = null;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            // 🌟 ensure profile link
            if (user.getProfile() == null) {
                AccountProfile profile = new AccountProfile();
                profile.setUser(user);
                user.setProfile(profile);
            }

            // 🌟 ensure default CUSTOMER role
            if (user.getRoles().isEmpty()) {
                Role customer = s.createQuery("from Role where name = 'CUSTOMER'", Role.class)
                        .uniqueResult();
                if (customer != null) user.addRole(customer);
            }

            s.persist(user);
            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    /** Update existing user */
    public void update(User user) {
        Transaction tx = null;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            s.merge(user);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    /** Delete user by ID */
    public void delete(Integer id) {
        Transaction tx = null;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            User u = s.get(User.class, id);
            if (u != null) {
                s.remove(u);
            }

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }


    /** Login helper */
    public User findByUsernameOrEmail(String input) {
        return dao.findByUsernameOrEmail(input);
    }
}
