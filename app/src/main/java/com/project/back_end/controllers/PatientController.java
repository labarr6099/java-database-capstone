package com.project.back_end.controller;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    // ---------------------------------------------------------
    // GET PATIENT DETAILS
    // ---------------------------------------------------------
    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatientDetails(@PathVariable String token) {

        // Validate patient token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        return patientService.getPatientDetails(token);
    }

    // ---------------------------------------------------------
    // CREATE NEW PATIENT (SIGNUP)
    // ---------------------------------------------------------
    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {

        boolean valid = service.validatePatient(patient);

        if (!valid) {
            return ResponseEntity.status(409)
                    .body(Map.of("message", "Patient with email id or phone no already exist"));
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Signup successful"));
        }

        return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
    }

    // ---------------------------------------------------------
    // PATIENT LOGIN
    // ---------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // ---------------------------------------------------------
    // GET PATIENT APPOINTMENTS
    // ---------------------------------------------------------
    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointments(
            @PathVariable Long id,
            @PathVariable String token
    ) {

        // Validate patient token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        return patientService.getPatientAppointment(id, token);
    }

    // ---------------------------------------------------------
    // FILTER PATIENT APPOINTMENTS
    // ---------------------------------------------------------
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token
    ) {

        // Validate patient token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        return service.filterPatient(condition, name, token);
    }
}
