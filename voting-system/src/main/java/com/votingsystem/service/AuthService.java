package com.votingsystem.service;

import com.votingsystem.model.AuditLog;
import com.votingsystem.model.User;
import com.votingsystem.model.Voter;
import com.votingsystem.repository.UserRepository;
import com.votingsystem.repository.VoterRepository;
import com.votingsystem.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Service
 */
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VoterRepository voterRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private AuditLogService auditLogService;
    
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    @Transactional
    public Map<String, Object> login(String username, String password, String ipAddress) {
        Map<String, Object> response = new HashMap<>();
        
        User user = userRepository.findByUsername(username)
                .orElse(null);
        
        if (user == null) {
            auditLogService.log(null, AuditLog.ActionType.LOGIN, 
                    "Login attempt failed - user not found", 
                    "Username: " + username, ipAddress, null, 
                    AuditLog.Severity.WARNING, false, "Invalid credentials");
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return response;
        }
        
        if (user.getAccountLocked()) {
            auditLogService.logCritical(user, AuditLog.ActionType.LOGIN, 
                    "Login attempt on locked account", ipAddress);
            response.put("success", false);
            response.put("message", "Account is locked. Contact administrator.");
            return response;
        }
        
        if (!user.getActive()) {
            auditLogService.logError(user, AuditLog.ActionType.LOGIN, 
                    "Login attempt on inactive account", 
                    "Account disabled", ipAddress);
            response.put("success", false);
            response.put("message", "Account is disabled");
            return response;
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            handleFailedLogin(user, ipAddress);
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return response;
        }
        
        // Check voter specific conditions
        if (user.getRole() == User.UserRole.VOTER) {
            Voter voter = voterRepository.findByUsername(username).orElse(null);
            if (voter != null && !voter.getVerified()) {
                auditLogService.logError(user, AuditLog.ActionType.LOGIN, 
                        "Login attempt - voter not verified", 
                        "Voter needs verification", ipAddress);
                response.put("success", false);
                response.put("message", "Your account is pending verification");
                return response;
            }
        }
        
        // Successful login
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(ipAddress);
        userRepository.save(user);
        
        String token = jwtTokenUtil.generateToken(user.getUsername(), user.getRole().name());
        
        auditLogService.logSuccess(user, AuditLog.ActionType.LOGIN, 
                "Successful login", "IP: " + ipAddress, ipAddress);
        
        response.put("success", true);
        response.put("token", token);
        response.put("role", user.getRole().name());
        response.put("username", user.getUsername());
        response.put("message", "Login successful");
        
        return response;
    }
    
    private void handleFailedLogin(User user, String ipAddress) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            auditLogService.logCritical(user, AuditLog.ActionType.ACCOUNT_LOCKED, 
                    "Account locked due to multiple failed login attempts", ipAddress);
        } else {
            auditLogService.logError(user, AuditLog.ActionType.LOGIN, 
                    "Failed login attempt", 
                    "Attempt " + user.getFailedLoginAttempts() + " of " + MAX_FAILED_ATTEMPTS, 
                    ipAddress);
        }
        
        userRepository.save(user);
    }
    
    @Transactional
    public void logout(String username, String ipAddress) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            auditLogService.logSuccess(user, AuditLog.ActionType.LOGOUT, 
                    "User logged out", null, ipAddress);
        }
    }
    
    @Transactional
    public Map<String, Object> registerVoter(Voter voter, String password) {
        Map<String, Object> response = new HashMap<>();
        
        if (voterRepository.existsByVoterId(voter.getVoterId())) {
            response.put("success", false);
            response.put("message", "Voter ID already exists");
            return response;
        }
        
        if (voterRepository.existsByNationalId(voter.getNationalId())) {
            response.put("success", false);
            response.put("message", "National ID already registered");
            return response;
        }
        
        voter.setPassword(passwordEncoder.encode(password));
        voter.setRole(User.UserRole.VOTER);
        voter.setActive(true);
        voter.setVerified(false);
        voter.setStatus(Voter.VoterStatus.PENDING);
        
        voterRepository.save(voter);
        
        auditLogService.logSuccess(voter, AuditLog.ActionType.VOTER_REGISTERED, 
                "New voter registered", "Voter ID: " + voter.getVoterId(), null);
        
        response.put("success", true);
        response.put("message", "Registration successful. Awaiting verification.");
        return response;
    }
}
