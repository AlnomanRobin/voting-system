package com.votingsystem.service;

import com.votingsystem.model.AuditLog;
import com.votingsystem.model.User;
import com.votingsystem.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for audit logging
 */
@Service
public class AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Transactional
    public void log(User user, AuditLog.ActionType actionType, String action, 
                    String details, String ipAddress, String userAgent, 
                    AuditLog.Severity severity, boolean success, String errorMessage) {
        
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setActionType(actionType);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setSeverity(severity);
        log.setSuccess(success);
        log.setErrorMessage(errorMessage);
        
        auditLogRepository.save(log);
    }
    
    public void logSuccess(User user, AuditLog.ActionType actionType, String action, 
                          String details, String ipAddress) {
        log(user, actionType, action, details, ipAddress, null, AuditLog.Severity.INFO, true, null);
    }
    
    public void logError(User user, AuditLog.ActionType actionType, String action, 
                        String errorMessage, String ipAddress) {
        log(user, actionType, action, null, ipAddress, null, AuditLog.Severity.ERROR, false, errorMessage);
    }
    
    public void logCritical(User user, AuditLog.ActionType actionType, String action, 
                           String details, String ipAddress) {
        log(user, actionType, action, details, ipAddress, null, AuditLog.Severity.CRITICAL, true, null);
    }
    
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
    
    public Page<AuditLog> getRecentLogs(int page, int size) {
        return auditLogRepository.findByOrderByTimestampDesc(PageRequest.of(page, size));
    }
    
    public List<AuditLog> getLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }
    
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }
    
    public List<AuditLog> getCriticalLogs() {
        return auditLogRepository.findCriticalLogs();
    }
    
    public Long getSuspiciousActivityCount() {
        return auditLogRepository.countSuspiciousActivities();
    }
}
