# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]  
**Story Points:** [Estimated Effort in Points]

**Notes:**
- [Additional information or edge cases]

---

# Admin User Stories

## User Story 1 — Admin Logs In
**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can securely manage the platform._

**Acceptance Criteria:**  
1. Admin enters valid credentials.  
2. System verifies and grants access.  
3. Admin is redirected to dashboard.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Failed login attempts should be logged.

---

## User Story 2 — Admin Logs Out
**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**  
1. Admin clicks logout.  
2. Session ends.  
3. Admin cannot access protected pages.  

**Priority:** High  
**Story Points:** 1  

**Notes:**  
- Session timeout should also log out admin.

---

## User Story 3 — Admin Adds Doctors
**Title:**  
_As an admin, I want to add doctors to the portal, so that they can access the system._

**Acceptance Criteria:**  
1. Admin enters doctor details.  
2. System creates profile and credentials.  
3. Doctor receives welcome email.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Email must include password reset link.

---

## User Story 4 — Admin Deletes Doctor Profiles
**Title:**  
_As an admin, I want to delete a doctor’s profile, so that I can remove inactive doctors._

**Acceptance Criteria:**  
1. Admin selects doctor profile.  
2. System removes access.  
3. Historical records remain intact.  

**Priority:** Medium  
**Story Points:** 2  

**Notes:**  
- Deletion should not remove past appointment history.

---

## User Story 5 — Admin Runs Stored Procedure
**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI, so that I can view monthly appointment statistics._

**Acceptance Criteria:**  
1. Admin executes stored procedure.  
2. System returns appointment counts by month.  
3. Results are readable and accurate.  

**Priority:** Medium  
**Story Points:** 3  

**Notes:**  
- Procedure should handle months with zero appointments.

---

# Patient User Stories

## User Story 1 — Patient Views Doctors Without Logging In
**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore my options before registering._

**Acceptance Criteria:**  
1. Patient can see doctor names, specialties, and availability.  
2. No login is required.  
3. Sensitive doctor information is hidden.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Helps encourage new users to register.

---

## User Story 2 — Patient Signs Up
**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**  
1. Patient enters email, password, and profile details.  
2. System validates email and password strength.  
3. Patient receives confirmation.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Duplicate emails should be blocked.

---

## User Story 3 — Patient Logs In
**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**  
1. Patient enters valid credentials.  
2. System verifies and grants access.  
3. Patient is redirected to dashboard.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Failed login attempts should be logged.

---

## User Story 4 — Patient Logs Out
**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**  
1. Patient clicks logout.  
2. Session ends.  
3. Patient cannot access protected pages.  

**Priority:** Medium  
**Story Points:** 1  

**Notes:**  
- Session timeout should also log out patient.

---

## User Story 5 — Patient Books an Hour-Long Appointment
**Title:**  
_As a patient, I want to book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**  
1. Patient selects a doctor.  
2. Patient chooses an available one-hour slot.  
3. System prevents double-booking.  
4. Patient receives confirmation.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Appointment duration must be exactly one hour.

---

## User Story 6 — Patient Views Upcoming Appointments
**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**  
1. Patient sees all future appointments.  
2. Appointment details include doctor, date, time, and location.  
3. Cancelled appointments are clearly marked.  

**Priority:** Medium  
**Story Points:** 2  

**Notes:**  
- Past appointments should not appear here.

---

# Doctor User Stories

## User Story 1 — Doctor Logs In
**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**  
1. Doctor enters valid credentials.  
2. System verifies and grants access.  
3. Doctor is redirected to dashboard.  

**Priority:** High  
**Story Points:** 2  

**Notes:**  
- Failed login attempts should be logged.

---

## User Story 2 — Doctor Logs Out
**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**  
1. Doctor clicks logout.  
2. Session ends.  
3. Doctor cannot access protected pages.  

**Priority:** High  
**Story Points:** 1  

**Notes:**  
- Session timeout should also log out doctor.

---

## User Story 3 — Doctor Views Appointment Calendar
**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**  
1. Doctor sees all upcoming appointments.  
2. Each appointment shows patient name, date, time, and reason.  
3. Calendar updates in real time.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Calendar should support daily, weekly, and monthly views.

---

## User Story 4 — Doctor Marks Unavailability
**Title:**  
_As a doctor, I want to mark my unavailability, so that patients only see available appointment slots._

**Acceptance Criteria:**  
1. Doctor selects dates/times to mark unavailable.  
2. System removes those slots from patient booking options.  
3. Doctor receives confirmation.  

**Priority:** Medium  
**Story Points:** 3  

**Notes:**  
- Unavailability should not affect existing appointments.

---

## User Story 5 — Doctor Updates Profile
**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**  
1. Doctor edits specialization and contact details.  
2. System validates required fields.  
3. Updated profile is visible to patients.  

**Priority:** Medium  
**Story Points:** 2  

**Notes:**  
- Profile changes should be logged.

---

## User Story 6 — Doctor Views Patient Details
**Title:**  
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**  
1. Doctor sees patient demographics and medical history.  
2. Appointment reason and past notes are visible.  
3. Access is restricted to assigned patients.  

**Priority:** High  
**Story Points:** 3  

**Notes:**  
- Sensitive data must follow privacy rules.

