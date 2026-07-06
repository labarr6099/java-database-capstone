# Smart Clinic Management System – Schema Design

## MySQL Database Design

> Core, structured, relational data (patients, doctors, appointments, admin, payments, locations) lives in MySQL.  
> Rules like “no overlapping appointments” or “a patient must exist before an appointment” are enforced here.

---

### Table: patients

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **first_name:** VARCHAR(100), NOT NULL  
- **last_name:** VARCHAR(100), NOT NULL  
- **date_of_birth:** DATE, NOT NULL  
- **gender:** ENUM('M', 'F', 'Other'), NULL  
- **email:** VARCHAR(255), UNIQUE, NOT NULL  
- **phone:** VARCHAR(20), NOT NULL  
- **address_line1:** VARCHAR(255), NOT NULL  
- **address_line2:** VARCHAR(255), NULL  
- **city:** VARCHAR(100), NOT NULL  
- **state:** VARCHAR(100), NOT NULL  
- **zip_code:** VARCHAR(20), NOT NULL  
- **created_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP  
- **updated_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  
- **is_active:** TINYINT(1), NOT NULL, DEFAULT 1  

> Deleting a patient: typically we **soft-delete** (`is_active = 0`) to preserve appointment history and audit trails.

---

### Table: doctors

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **first_name:** VARCHAR(100), NOT NULL  
- **last_name:** VARCHAR(100), NOT NULL  
- **specialization:** VARCHAR(150), NOT NULL  
- **email:** VARCHAR(255), UNIQUE, NOT NULL  
- **phone:** VARCHAR(20), NOT NULL  
- **license_number:** VARCHAR(100), UNIQUE, NOT NULL  
- **clinic_location_id:** INT, NULL, FOREIGN KEY → `clinic_locations(id)`  
- **created_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP  
- **updated_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  
- **is_active:** TINYINT(1), NOT NULL, DEFAULT 1  

> Doctor availability and overlapping appointments are controlled via `doctor_availability` and `appointments` logic, not just schema.

---

### Table: clinic_locations

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **name:** VARCHAR(150), NOT NULL  
- **address_line1:** VARCHAR(255), NOT NULL  
- **address_line2:** VARCHAR(255), NULL  
- **city:** VARCHAR(100), NOT NULL  
- **state:** VARCHAR(100), NOT NULL  
- **zip_code:** VARCHAR(20), NOT NULL  
- **phone:** VARCHAR(20), NULL  
- **is_active:** TINYINT(1), NOT NULL, DEFAULT 1  

> Allows multi-location clinics and future expansion without changing patient/doctor core tables.

---

### Table: doctor_availability

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **doctor_id:** INT, NOT NULL, FOREIGN KEY → `doctors(id)`  
- **day_of_week:** ENUM('MON','TUE','WED','THU','FRI','SAT','SUN'), NOT NULL  
- **start_time:** TIME, NOT NULL  
- **end_time:** TIME, NOT NULL  
- **is_active:** TINYINT(1), NOT NULL, DEFAULT 1  

> Overlapping availability slots should be prevented at the application/service layer with validation logic.

---

### Table: appointments

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **doctor_id:** INT, NOT NULL, FOREIGN KEY → `doctors(id)`  
- **patient_id:** INT, NOT NULL, FOREIGN KEY → `patients(id)`  
- **clinic_location_id:** INT, NOT NULL, FOREIGN KEY → `clinic_locations(id)`  
- **appointment_time_start:** DATETIME, NOT NULL  
- **appointment_time_end:** DATETIME, NOT NULL  
- **status:** ENUM('SCHEDULED','COMPLETED','CANCELLED','NO_SHOW'), NOT NULL, DEFAULT 'SCHEDULED'  
- **reason:** VARCHAR(255), NULL  
- **created_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP  
- **updated_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  

> Past appointment history should be retained indefinitely for medical/legal reasons—appointments are never hard-deleted, only marked by `status`.

---

### Table: prescriptions_core

*(Core linkage; detailed notes/metadata live in MongoDB)*

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **appointment_id:** INT, NOT NULL, FOREIGN KEY → `appointments(id)`  
- **doctor_id:** INT, NOT NULL, FOREIGN KEY → `doctors(id)`  
- **patient_id:** INT, NOT NULL, FOREIGN KEY → `patients(id)`  
- **created_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP  

> A prescription is tied to a specific appointment for traceability, but flexible details (medications, notes, attachments) are stored in MongoDB.

---

### Table: payments

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **appointment_id:** INT, NOT NULL, FOREIGN KEY → `appointments(id)`  
- **patient_id:** INT, NOT NULL, FOREIGN KEY → `patients(id)`  
- **amount:** DECIMAL(10,2), NOT NULL  
- **currency:** VARCHAR(10), NOT NULL, DEFAULT 'USD'  
- **status:** ENUM('PENDING','PAID','REFUNDED','FAILED'), NOT NULL, DEFAULT 'PENDING'  
- **payment_method:** ENUM('CASH','CARD','INSURANCE','ONLINE'), NOT NULL  
- **transaction_reference:** VARCHAR(255), UNIQUE, NULL  
- **created_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP  

> Payment history is critical for reporting and compliance—never hard-delete, only adjust via `status` and new records.

---

### Table: admin_users

- **id:** INT, PRIMARY KEY, AUTO_INCREMENT  
- **username:** VARCHAR(100), UNIQUE, NOT NULL  
- **password_hash:** VARCHAR(255), NOT NULL  
- **role:** ENUM('ADMIN','STAFF','SUPER_ADMIN'), NOT NULL  
- **email:** VARCHAR(255), UNIQUE, NOT NULL  
- **is_active:** TINYINT(1), NOT NULL, DEFAULT 1  
- **created_at:** DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP  

> Admins manage clinic configuration, users, and reports. Authentication/authorization logic will use `role` and `is_active`.

---

## MongoDB Collection Design

> Flexible, semi-structured, and evolving data (rich notes, feedback, chat, logs, attachments) lives in MongoDB.  
> These documents can reference MySQL IDs but are free to grow with new fields and nested structures.

---

### Collection: prescriptions_details

> Complements `prescriptions_core` in MySQL. Holds rich prescription data, notes, tags, and attachments.

```json
{
  "_id": { "$oid": "64abc1234567890fedcba001" },
  "prescriptionCoreId": 42,              // FK-like reference to MySQL prescriptions_core.id
  "patientId": 15,                       // MySQL patients.id
  "doctorId": 7,                         // MySQL doctors.id
  "appointmentId": 120,                  // MySQL appointments.id
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "frequency": "Every 6 hours",
      "durationDays": 5,
      "instructions": "Take after meals",
      "tags": ["pain", "fever"]
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "Twice daily",
      "durationDays": 3,
      "instructions": "Avoid on empty stomach",
      "tags": ["inflammation"]
    }
  ],
  "doctorNotes": [
    {
      "noteId": "note-1",
      "createdAt": "2026-07-05T20:30:00Z",
      "text": "Patient reports mild headache and low-grade fever.",
      "visibility": "INTERNAL"
    },
    {
      "noteId": "note-2",
      "createdAt": "2026-07-05T20:40:00Z",
      "text": "Advise rest and hydration. Follow-up in 3 days if symptoms persist.",
      "visibility": "PATIENT"
    }
  ],
  "pharmacy": {
    "name": "Walgreens Downtown",
    "location": {
      "addressLine1": "123 Main St",
      "city": "Jacksonville",
      "state": "FL",
      "zipCode": "32202"
    },
    "preferred": true
  },
  "attachments": [
    {
      "fileId": "scan-001.pdf",
      "type": "LAB_REPORT",
      "uploadedAt": "2026-07-05T21:00:00Z",
      "metadata": {
        "sizeKb": 320,
        "mimeType": "application/pdf"
      }
    }
  ],
  "tags": ["acute", "follow-up-required"],
  "auditTrail": [
    {
      "event": "CREATED",
      "timestamp": "2026-07-05T20:30:00Z",
      "performedByAdminId": 3
    },
    {
      "event": "UPDATED",
      "timestamp": "2026-07-05T20:45:00Z",
      "performedByAdminId": 3,
      "changes": {
        "medicationsAdded": ["Ibuprofen"],
        "notesAdded": ["note-2"]
      }
    }
  ],
  "version": 2
}

{
  "_id": { "$oid": "64def9876543210abc000123" },
  "conversationId": "conv-2026-07-05-15",
  "appointmentId": 120,
  "patientId": 15,
  "doctorId": 7,
  "senderType": "PATIENT",
  "senderId": 15,
  "messageText": "I'm still feeling a bit feverish. Should I adjust the dosage?",
  "sentAt": "2026-07-06T09:15:00Z",
  "attachments": [],
  "metadata": {
    "readByDoctor": false,
    "priority": "NORMAL"
  }
}
