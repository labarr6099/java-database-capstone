package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        // Standard work hours 09:00 to 17:00
        List<String> availableSlots = new ArrayList<>(Arrays.asList(
                "09:00", "10:00", "11:00", "12:00", 
                "13:00", "14:00", "15:00", "16:00", "17:00"
        ));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> bookedAppointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

        for (Appointment appt : bookedAppointments) {
            if (appt.getAppointmentTime() != null) {
                // Formatting matches "HH:mm"
                String bookedTime = appt.getAppointmentTime().toLocalTime().toString(); 
                availableSlots.remove(bookedTime);
            }
        }

        return availableSlots;
    }

    public int saveDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctorOpt = doctorRepository.findByEmail(doctor.getEmail());
            if (existingDoctorOpt.isPresent()) {
                return -1; // Doctor already exists
            }
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            Optional<Doctor> existingDoctor = doctorRepository.findById(doctor.getId());
            if (existingDoctor.isEmpty()) {
                return -1; // Doctor not found
            }
            doctorRepository.save(doctor);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(long id) {
        try {
            Optional<Doctor> doctor = doctorRepository.findById(id);
            if (doctor.isEmpty()) {
                return -1; // Doctor not found
            }
            // Delete associated appointments first to avoid foreign key constraint errors
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1; // Success
        } catch (Exception e) {
            return 0; // Internal error
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(login.getIdentifier());

        if (doctorOpt.isPresent()) {
            Doctor doctor = doctorOpt.get();
            if (doctor.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(doctor.getId(), doctor.getEmail(), "doctor");
                response.put("token", token);
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            }
        }

        response.put("error", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specilty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specilty);
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specilty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    public Map<String, Object> filterDoctorBySpecility(String specilty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specilty);
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filteredDoctors = new ArrayList<>();
        if (amOrPm == null || amOrPm.trim().isEmpty()) {
            return doctors;
        }

        for (Doctor doctor : doctors) {
            // Correctly using getAvailableTimes() to loop through the List
            if (doctor.getAvailableTimes() != null) {
                for (String timeSlot : doctor.getAvailableTimes()) {
                    if (timeSlot != null && timeSlot.toUpperCase().contains(amOrPm.toUpperCase())) {
                        filteredDoctors.add(doctor);
                        break; // Stop checking other times once we find a match
                    }
                }
            }
        }
        return filteredDoctors;
    }
}