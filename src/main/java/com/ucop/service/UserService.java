package com.ucop.service;

import com.ucop.dao.UserDAO;
import com.ucop.entity.AccountProfile;
import com.ucop.entity.Role;
import com.ucop.entity.User;
import com.ucop.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class UserService {

    private final UserDAO dao = new UserDAO();

    public List<User> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("from User", User.class).list();
        }
    }

    // ✅ NEW: find by id (Long)
    public User findById(Long id) {
        if (id == null) return null;
        // User.id của bạn đang là Integer -> convert
        return findById(Integer.valueOf(id.toString()));
    }

    // ✅ NEW: find by id (Integer)
    public User findById(Integer id) {
        if (id == null) return null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(User.class, id);
        }
    }

    // ✅ Optional helper (nếu bạn muốn dùng)
    public User getOrThrow(Long id) {
        User u = findById(id);
        if (u == null) throw new IllegalArgumentException("User not found with id=" + id);
        return u;
    }

    /** Save new user */
    public void save(User user) {
        Transaction tx = null;

        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            if (user == null) {
                throw new IllegalArgumentException("User is null");
            }

            // 🌟 ensure profile link
            if (user.getProfile() == null) {
                AccountProfile profile = new AccountProfile();
                profile.setUser(user);
                user.setProfile(profile);
            }

            // 🌟 ensure default CUSTOMER role
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                Role customer = s.createQuery(
                                "from Role where upper(name) = 'CUSTOMER'",
                                Role.class
                        )
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

            if (user == null) {
                throw new IllegalArgumentException("User is null");
            }

            s.merge(user);

            tx.commit();

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    /** Delete user by ID (Integer) */
    public void delete(Integer id) {
        if (id == null) return;
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

    /** ✅ NEW: Delete by Long (cho đồng bộ accountId) */
    public void delete(Long id) {
        if (id == null) return;
        delete(Integer.valueOf(id.toString()));
    }

    /** Login helper */
    public User findByUsernameOrEmail(String input) {
        return dao.findByUsernameOrEmail(input);
    }
}
