package com.project.back_end.controller;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token
    ) {
        var validation = service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        LocalDate parsedDate = LocalDate.parse(date);

        Map<String, Object> result =
                appointmentService.getAppointment(patientName, parsedDate, token);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {
        var validation = service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        int valid = service.validateAppointment(appointment);

        if (valid == -1) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid doctor ID"));
        }

        if (valid == 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "Time slot unavailable"));
        }

        int booked = appointmentService.bookAppointment(appointment);

        if (booked == 1) {
            return ResponseEntity.status(201).body(Map.of("message", "Appointment booked"));
        }

        return ResponseEntity.status(500).body(Map.of("message", "Error booking appointment"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable String token,
            @RequestBody Appointment appointment
    ) {
        var validation = service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable long id,
            @PathVariable String token
    ) {
        var validation = service.validateToken(token, "patient");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
        }

        return appointmentService.cancelAppointment(id, token);
    }
}
