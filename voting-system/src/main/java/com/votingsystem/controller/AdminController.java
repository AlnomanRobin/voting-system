package com.votingsystem.controller;

import com.votingsystem.model.*;
import com.votingsystem.repository.UserRepository;
import com.votingsystem.service.AdminService;
import com.votingsystem.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Admin Controller
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    // ==================== Election Management ====================
    
    @GetMapping("/elections")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Election>> getAllElections() {
        List<Election> elections = adminService.getAllElections();
        return ResponseEntity.ok(elections);
    }
    
    @PostMapping("/elections")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Election> createElection(@RequestBody Election election,
                                                    Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        Election created = adminService.createElection(election, admin);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/elections/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Election> updateElection(@PathVariable Long id,
                                                    @RequestBody Election election,
                                                    Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        Election updated = adminService.updateElection(id, election, admin);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/elections/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteElection(@PathVariable Long id,
                                                               Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        adminService.deleteElection(id, admin);
        return ResponseEntity.ok(Map.of("message", "Election deleted successfully"));
    }
    
    @PostMapping("/elections/{id}/publish-results")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> publishResults(@PathVariable Long id,
                                                               Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        adminService.publishResults(id, admin);
        return ResponseEntity.ok(Map.of("message", "Results published successfully"));
    }
    
    // ==================== Candidate Management ====================
    
    @PostMapping("/candidates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Candidate> addCandidate(@RequestBody Candidate candidate,
                                                   Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        Candidate created = adminService.addCandidate(candidate, admin);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/candidates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id,
                                                      @RequestBody Candidate candidate,
                                                      Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        Candidate updated = adminService.updateCandidate(id, candidate, admin);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/candidates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCandidate(@PathVariable Long id,
                                                                Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        adminService.deleteCandidate(id, admin);
        return ResponseEntity.ok(Map.of("message", "Candidate deleted successfully"));
    }
    
    // ==================== Voter Management ====================
    
    @GetMapping("/voters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Voter>> getAllVoters() {
        List<Voter> voters = adminService.getAllVoters();
        return ResponseEntity.ok(voters);
    }
    
    @GetMapping("/voters/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Voter>> getPendingVoters() {
        List<Voter> voters = adminService.getPendingVoters();
        return ResponseEntity.ok(voters);
    }
    
    @PostMapping("/voters/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> verifyVoter(@PathVariable Long id,
                                                            Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        adminService.verifyVoter(id, admin);
        return ResponseEntity.ok(Map.of("message", "Voter verified successfully"));
    }
    
    @PostMapping("/voters/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> suspendVoter(@PathVariable Long id,
                                                             Authentication authentication) {
        User admin = userRepository.findByUsername(authentication.getName()).orElse(null);
        adminService.suspendVoter(id, admin);
        return ResponseEntity.ok(Map.of("message", "Voter suspended successfully"));
    }
    
    // ==================== Results ====================
    
    @GetMapping("/results/{electionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getResults(@PathVariable Long electionId) {
        Map<String, Object> results = adminService.getElectionResults(electionId);
        return ResponseEntity.ok(results);
    }
    
    // ==================== Audit Logs ====================
    
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLog>> getLogs(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        Page<AuditLog> logs = auditLogService.getRecentLogs(page, size);
        return ResponseEntity.ok(logs);
    }
    
    @GetMapping("/logs/critical")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getCriticalLogs() {
        List<AuditLog> logs = auditLogService.getCriticalLogs();
        return ResponseEntity.ok(logs);
    }
}
