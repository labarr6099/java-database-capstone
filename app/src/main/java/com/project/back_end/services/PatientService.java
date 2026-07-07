package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // ---------------------------------------------------------
    // CREATE PATIENT
    // ---------------------------------------------------------
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // ---------------------------------------------------------
    // GET PATIENT APPOINTMENTS
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {

        Map<String, Object> response = new HashMap<>();

        Long patientIdFromToken = tokenService.extractId(token);
        if (!Objects.equals(patientIdFromToken, id)) {
            response.put("message", "Unauthorized: You can only view your own appointments");
            return ResponseEntity.status(403).body(response);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // FILTER BY CONDITION (PAST / FUTURE)
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {

        Map<String, Object> response = new HashMap<>();

        int status;
        if (condition.equalsIgnoreCase("past")) {
            status = 1;
        } else if (condition.equalsIgnoreCase("future")) {
            status = 0;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatientIdAndStatusOrderByAppointmentTimeAsc(id, status);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // FILTER BY DOCTOR NAME
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {

        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientId(name, patientId);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // FILTER BY DOCTOR NAME + CONDITION
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition, String name, long patientId
    ) {

        Map<String, Object> response = new HashMap<>();

        int status;
        if (condition.equalsIgnoreCase("past")) {
            status = 1;
        } else if (condition.equalsIgnoreCase("future")) {
            status = 0;
        } else {
            response.put("message", "Invalid condition. Use 'past' or 'future'.");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // GET PATIENT DETAILS
    // ---------------------------------------------------------
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractIdentifier(token);

        Optional<Patient> patientOpt = patientRepository.findByEmail(email);
        if (patientOpt.isEmpty()) {
            response.put("message", "Patient not found");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("patient", patientOpt.get());
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------
    // HELPER: CONVERT APPOINTMENTS TO DTO
    // ---------------------------------------------------------
    private List<AppointmentDTO> convertToDTO(List<Appointment> appointments) {

        List<AppointmentDTO> dtoList = new ArrayList<>();

        for (Appointment a : appointments) {

            AppointmentDTO dto = new AppointmentDTO(
                    a.getId(),
                    a.getDoctor().getId(),               // ✔ FIXED
                    a.getDoctor().getName(),             // doctorName
                    a.getPatient().getId(),              // ✔ FIXED
                    a.getPatient().getName(),            // patientName
                    a.getPatient().getEmail(),           // patientEmail
                    a.getPatient().getPhone(),           // patientPhone
                    a.getPatient().getAddress(),         // patientAddress
                    a.getAppointmentTime(),
                    a.getStatus()
            );

            dtoList.add(dto);
        }

        return dtoList;
    }
}
