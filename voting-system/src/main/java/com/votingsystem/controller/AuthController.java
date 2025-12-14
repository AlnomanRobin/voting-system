package com.votingsystem.controller;

import com.votingsystem.model.Voter;
import com.votingsystem.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials, 
                                                      HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String ipAddress = request.getRemoteAddr();
        
        Map<String, Object> response = authService.login(username, password, ipAddress);
        
        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token,
                                                       HttpServletRequest request) {
        // Extract username from token or request
        String ipAddress = request.getRemoteAddr();
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Logged out successfully");
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> registrationData) {
        // Create voter from registration data
        Voter voter = new Voter();
        voter.setVoterId((String) registrationData.get("voterId"));
        voter.setNationalId((String) registrationData.get("nationalId"));
        voter.setFullName((String) registrationData.get("fullName"));
        voter.setUsername((String) registrationData.get("username"));
        // Set other fields...
        
        String password = (String) registrationData.get("password");
        
        Map<String, Object> response = authService.registerVoter(voter, password);
        
        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("message", "Token is valid");
        return ResponseEntity.ok(response);
    }
}
