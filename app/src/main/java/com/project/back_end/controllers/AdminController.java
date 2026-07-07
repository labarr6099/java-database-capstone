
package com.project.back_end.controller;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")
public class AdminController {

    @Autowired
    private Service service;

    // ---------------------------------------------------------
    // ADMIN LOGIN
    // ---------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
 