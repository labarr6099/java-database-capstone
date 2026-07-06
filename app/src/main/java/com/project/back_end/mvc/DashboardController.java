package com.project.back_end.mvc;


    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    
    import java.util.Map;
    
    @Controller
    public class DashboardController {
    
        // Note: If your token validation service has a different class name 
        // (e.g., AuthService or ValidationService), make sure to update the type here!
        @Autowired
        private TokenValidationService tokenValidationService; 
    
        @GetMapping("/adminDashboard/{token}")
        public String adminDashboard(@PathVariable String token) {
            // Call the service to validate the token for an admin
            Map<String, String> errors = tokenValidationService.validateToken(token, "admin");
            
            // If the map is empty, there are no errors -> token is valid
            if (errors.isEmpty()) {
                return "admin/adminDashboard";
            } else {
                // Invalid token, redirect to the root login page
                return "redirect:/"; 
            }
        }
    
        @GetMapping("/doctorDashboard/{token}")
        public String doctorDashboard(@PathVariable String token) {
            // Call the service to validate the token for a doctor
            Map<String, String> errors = tokenValidationService.validateToken(token, "doctor");
            
            // If the map is empty, there are no errors -> token is valid
            if (errors.isEmpty()) {
                return "doctor/doctorDashboard";
            } else {
                // Invalid token, redirect to the root login page
                return "redirect:/";
            }
        }
    }   

// 1. Set Up the MVC Controller Class:
//    - Annotate the class with `@Controller` to indicate that it serves as an MVC controller returning view names (not JSON).
//    - This class handles routing to admin and doctor dashboard pages based on token validation.


// 2. Autowire the Shared Service:
//    - Inject the common `Service` class, which provides the token validation logic used to authorize access to dashboards.


// 3. Define the `adminDashboard` Method:
//    - Handles HTTP GET requests to `/adminDashboard/{token}`.
//    - Accepts an admin's token as a path variable.
//    - Validates the token using the shared service for the `"admin"` role.
//    - If the token is valid (i.e., no errors returned), forwards the user to the `"admin/adminDashboard"` view.
//    - If invalid, redirects to the root URL, likely the login or home page.


// 4. Define the `doctorDashboard` Method:
//    - Handles HTTP GET requests to `/doctorDashboard/{token}`.
//    - Accepts a doctor's token as a path variable.
//    - Validates the token using the shared service for the `"doctor"` role.
//    - If the token is valid, forwards the user to the `"doctor/doctorDashboard"` view.
//    - If the token is invalid, redirects to the root URL.


}
