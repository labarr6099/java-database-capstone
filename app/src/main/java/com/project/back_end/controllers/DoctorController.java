package com.project.back_end.controller;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private Service service;

    // ---------------------------------------------------------
    // GET DOCTOR AVAILABILITY
    // ---------------------------------------------------------
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token
    ) {

        // Validate token for the given user type
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, user);

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        LocalDate parsedDate = LocalDate.parse(date);

        List<String> availability =
                doctorService.getDoctorAvailability(doctorId, parsedDate);

        return ResponseEntity.ok(Map.of("availability", availability));
    }

    // ---------------------------------------------------------
    // GET ALL DOCTORS
    // ---------------------------------------------------------
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctors() {
        return ResponseEntity.ok(Map.of("doctors", doctorService.getDoctors()));
    }

    // ---------------------------------------------------------
    // ADD NEW DOCTOR (ADMIN ONLY)
    // ---------------------------------------------------------
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor
    ) {

        // Validate admin token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Doctor added to db"));
        }

        if (result == -1) {
            return ResponseEntity.status(409).body(Map.of("message", "Doctor already exists"));
        }

        return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
    }

    // ---------------------------------------------------------
    // DOCTOR LOGIN
    // ---------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // ---------------------------------------------------------
    // UPDATE DOCTOR DETAILS (ADMIN ONLY)
    // ---------------------------------------------------------
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @PathVariable String token,
            @RequestBody Doctor doctor
    ) {

        // Validate admin token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor updated"));
        }

        if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found"));
        }

        return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
    }

    // ---------------------------------------------------------
    // DELETE DOCTOR (ADMIN ONLY)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable long id,
            @PathVariable String token
    ) {

        // Validate admin token
        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "admin");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            return ResponseEntity.ok(Map.of("message", "Doctor deleted successfully"));
        }

        if (result == -1) {
            return ResponseEntity.status(404).body(Map.of("message", "Doctor not found with id"));
        }

        return ResponseEntity.status(500).body(Map.of("message", "Some internal error occurred"));
    }

    // ---------------------------------------------------------
    // FILTER DOCTORS (NAME + TIME + SPECIALTY)
    // ---------------------------------------------------------
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality
    ) {

        Map<String, Object> result =
                service.filterDoctor(name, speciality, time);

        return ResponseEntity.ok(result);
    }
}
