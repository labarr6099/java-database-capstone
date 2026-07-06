/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/
import { openModal } from '../components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

// Load Doctor Cards on Page Load
async function loadDoctorCards() {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = ""; 
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}

// Utility function to render doctor cards
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";
    
    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found</p>";
        return;
    }
    
    doctors.forEach(doctor => {
        contentDiv.innerHTML += createDoctorCard(doctor);
    });
}

// Implement Search and Filter Logic
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar").value;
    const time = document.getElementById("filterTime").value;
    const specialty = document.getElementById("filterSpecialty").value;

    const doctors = await filterDoctors(name, time, specialty);
    renderDoctorCards(doctors);
}

// Handle Add Doctor Modal Form Submission
window.adminAddDoctor = async function() {
    const token = localStorage.getItem('token');
    if (!token) {
        alert("Authentication error: No admin token found.");
        return;
    }

    // Collect values from the modal form inputs
    const name = document.getElementById("docName").value;
    const specialty = document.getElementById("docSpecialty").value;
    const email = document.getElementById("docEmail").value;
    const password = document.getElementById("docPassword").value;
    const mobile = document.getElementById("docMobile").value;
    const time = document.getElementById("docTime").value;

    const doctor = { name, specialty, email, password, mobile, time };

    const response = await saveDoctor(doctor, token);
    
    if (response.success) {
        alert(response.message);
        loadDoctorCards(); // Refresh the list
        // Note: You would also close the modal here if a closeModal() function exists
    } else {
        alert(response.message);
    }
};

// Event Binding setup on DOMContentLoaded
document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();

    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) {
        addDocBtn.addEventListener('click', () => openModal('addDoctor'));
    }

    const searchBar = document.getElementById("searchBar");
    if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);

    const filterTime = document.getElementById("filterTime");
    if (filterTime) filterTime.addEventListener("change", filterDoctorsOnChange);

    const filterSpecialty = document.getElementById("filterSpecialty");
    if (filterSpecialty) filterSpecialty.addEventListener("change", filterDoctorsOnChange);
});
