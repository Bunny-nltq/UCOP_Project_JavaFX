package com.ucop.repository.impl;

import com.ucop.entity.Appointment;
import com.ucop.repository.AppointmentRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class AppointmentRepositoryImpl implements AppointmentRepository {
    
    private final SessionFactory sessionFactory;
    
    public AppointmentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    @Override
    public Appointment save(Appointment appointment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(appointment);
            transaction.commit();
            return appointment;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public Optional<Appointment> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Appointment appointment = session.get(Appointment.class, id);
            return Optional.ofNullable(appointment);
        }
    }
    
    @Override
    public Optional<Appointment> findByAppointmentNumber(String appointmentNumber) {
        try (Session session = sessionFactory.openSession()) {
            Query<Appointment> query = session.createQuery(
                "FROM Appointment WHERE appointmentNumber = :appointmentNumber", Appointment.class);
            query.setParameter("appointmentNumber", appointmentNumber);
            return query.uniqueResultOptional();
        }
    }
    
    @Override
    public List<Appointment> findByOrderId(Long orderId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Appointment> query = session.createQuery(
                "FROM Appointment WHERE order.id = :orderId", Appointment.class);
            query.setParameter("orderId", orderId);
            return query.list();
        }
    }
    
    @Override
    public List<Appointment> findByStatus(String status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Appointment> query = session.createQuery(
                "FROM Appointment WHERE status = :status", Appointment.class);
            query.setParameter("status", status);
            return query.list();
        }
    }
    
    @Override
    public List<Appointment> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Appointment", Appointment.class).list();
        }
    }
    
    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Appointment appointment = session.get(Appointment.class, id);
            if (appointment != null) {
                session.delete(appointment);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    @Override
    public void update(Appointment appointment) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(appointment);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}
