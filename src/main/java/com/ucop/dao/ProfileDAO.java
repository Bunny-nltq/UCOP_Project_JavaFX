package com.ucop.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ucop.entity.AccountProfile;
import com.ucop.util.HibernateUtil;

public class ProfileDAO extends GenericDAO<AccountProfile> {

    public ProfileDAO() {
        super(AccountProfile.class);
    }

    public AccountProfile findByUserId(int userId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                "FROM AccountProfile p WHERE p.user.id = :uid",
                AccountProfile.class
            )
            .setParameter("uid", userId)
            .uniqueResult();
        }
    }

    public void saveOrUpdate(AccountProfile profile) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.merge(profile);  // Hibernate 6 dùng merge() thay cho saveOrUpdate
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
