package com.project.back_end.repo;

import com.project.back_end.models.Appointment; // <-- Fixed to models!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availability WHERE d.id = ?1 AND a.appointmentTime BETWEEN ?2 AND ?3")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d WHERE d.id = ?1 AND LOWER(p.name) LIKE LOWER(CONCAT('%', ?2, '%')) AND a.appointmentTime BETWEEN ?3 AND ?4")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    // Fixed method name to match what PatientService is calling
    List<Appointment> findByPatientIdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status); 

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = ?2 AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = ?2 AND a.status = ?3 AND LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);

    // Added missing method expected by AppointmentService
    @Query("SELECT a FROM Appointment a WHERE LOWER(a.patient.name) LIKE LOWER(CONCAT('%', ?1, '%')) AND CAST(a.appointmentTime AS date) = ?2")
    List<Appointment> findByPatientNameAndDate(String patientName, LocalDate date);
}