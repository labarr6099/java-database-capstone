package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // ---------------------------------------------------------
    // Find doctor by email
    // ---------------------------------------------------------
    Optional<Doctor> findByEmail(String email);

    // ---------------------------------------------------------
    // Partial name match (case-insensitive)
    // ---------------------------------------------------------
    @Query("""
           SELECT d 
           FROM Doctor d 
           WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?1, '%'))
           """)
    List<Doctor> findByNameLike(String name);

    // ---------------------------------------------------------
    // Partial name + exact specialty (case-insensitive)
    // ---------------------------------------------------------
    @Query("""
           SELECT d 
           FROM Doctor d 
           WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', ?1, '%'))
             AND LOWER(d.specialty) = LOWER(?2)
           """)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            String name,
            String specialty
    );

    // ---------------------------------------------------------
    // Find doctors by specialty (case-insensitive)
    // ---------------------------------------------------------
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
