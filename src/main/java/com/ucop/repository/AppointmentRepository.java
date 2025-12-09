package com.ucop.repository;

import com.ucop.entity.Appointment;
import java.util.Optional;
import java.util.List;

public interface AppointmentRepository {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(Long id);
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    List<Appointment> findByOrderId(Long orderId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findAll();
    void delete(Long id);
    void update(Appointment appointment);
}
