package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorService doctorService;

    // ---------------------------------------------------------
    // BOOK APPOINTMENT
    // ---------------------------------------------------------
    public int bookAppointment(Appointment appointment) {

        // Validate doctor exists
        Doctor doctor = appointment.getDoctor();
        if (doctor == null || doctor.getId() == null) {
            return -1;
        }

        Optional<Doctor> doctorOpt = doctorRepository.findById(doctor.getId());
        if (doctorOpt.isEmpty()) {
            return -1;
        }

        // Validate patient exists
        Patient patient = appointment.getPatient();
        if (patient == null || patient.getId() == null) {
            return -1;
        }

        Optional<Patient> patientOpt = patientRepository.findById(patient.getId());
        if (patientOpt.isEmpty()) {
            return -1;
        }

        // Validate appointment time availability
        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        List<String> availableSlots = doctorService.getDoctorAvailability(doctor.getId(), date);

        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();
        if (!availableSlots.contains(requestedTime)) {
            return 0; // time unavailable
        }

        // Save appointment
        appointmentRepository.save(appointment);
        return 1; // success
    }

    // ---------------------------------------------------------
    // UPDATE APPOINTMENT
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment updated) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(updated.getId());
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();

        existing.setAppointmentTime(updated.getAppointmentTime());
        existing.setStatus(updated.getStatus());

        appointmentRepository.save(existing);

        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // CANCEL APPOINTMENT
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {

        Map<String, String> response = new HashMap<>();

        Optional<Appointment> existingOpt = appointmentRepository.findById(id);
        if (existingOpt.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        appointmentRepository.deleteById(id);

        response.put("message", "Appointment cancelled successfully");
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // GET APPOINTMENTS FOR DOCTOR ON DATE + PATIENT NAME
    // ---------------------------------------------------------
    public Map<String, Object> getAppointment(String patientName, LocalDate date, String token) {

        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository.findByPatientNameAndDate(patientName, date);

        response.put("appointments", appointments);
        return response;
    }
}
