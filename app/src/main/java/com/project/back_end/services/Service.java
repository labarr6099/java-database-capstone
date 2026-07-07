package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.*; // <-- Restored to fix the Map and List errors

@org.springframework.stereotype.Service
public class Service {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {

        Map<String, String> response = new HashMap<>();

        boolean valid = tokenService.validateToken(token, user);

        if (!valid) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        Optional<Admin> adminOpt = adminRepository.findByUsername(receivedAdmin.getUsername());
        if (adminOpt.isEmpty()) {
            response.put("message", "Invalid username");
            return ResponseEntity.badRequest().body(response);
        }

        Admin admin = adminOpt.get();

        if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid password");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(
                admin.getId(),
                admin.getUsername(),
                "admin"
        );

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {

        if (name != null && specialty != null && time != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }

        if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        }

        if (name != null && time != null) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }

        if (specialty != null && time != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        }

        if (name != null) {
            return doctorService.findDoctorByName(name);
        }

        if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        }

        if (time != null) {
            return doctorService.filterDoctorsByTime(time);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> doctorOpt = doctorRepository.findById(
                appointment.getDoctor().getId()
        );
        if (doctorOpt.isEmpty()) return -1;

        Doctor doctor = doctorOpt.get();

        List<String> availableSlots = doctorService.getDoctorAvailability(
                doctor.getId(),
                appointment.getAppointmentTime().toLocalDate()
        );

        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();

        if (!availableSlots.contains(requestedTime)) {
            return 0;
        }

        return 1;
    }

    public boolean validatePatient(Patient patient) {

        Optional<Patient> existing =
                patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());

        return existing.isEmpty();
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        Optional<Patient> patientOpt = patientRepository.findByEmail(login.getIdentifier());
        if (patientOpt.isEmpty()) {
            response.put("message", "Invalid email");
            return ResponseEntity.badRequest().body(response);
        }

        Patient patient = patientOpt.get();

        if (!patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid password");
            return ResponseEntity.badRequest().body(response);
        }

        String token = tokenService.generateToken(
                patient.getId(),
                patient.getEmail(),
                "patient"
        );

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token
    ) {

        Long patientId = tokenService.extractId(token);

        if (condition != null && name != null) {
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        }

        if (condition != null) {
            return patientService.filterByCondition(condition, patientId);
        }

        if (name != null) {
            return patientService.filterByDoctor(name, patientId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "No filter applied");
        return ResponseEntity.ok(response);
    }
}