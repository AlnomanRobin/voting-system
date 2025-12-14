package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Audit Log entity for tracking all system actions
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;
    
    @Column(nullable = false, length = 200)
    private String action;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(length = 50)
    private String ipAddress;
    
    @Column(length = 200)
    private String userAgent;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;
    
    @Column(nullable = false)
    private Boolean success = true;
    
    private String errorMessage;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    public enum ActionType {
        LOGIN,
        LOGOUT,
        VOTE_CAST,
        ELECTION_CREATED,
        ELECTION_UPDATED,
        ELECTION_DELETED,
        CANDIDATE_ADDED,
        CANDIDATE_UPDATED,
        CANDIDATE_DELETED,
        VOTER_REGISTERED,
        VOTER_VERIFIED,
        VOTER_SUSPENDED,
        RESULT_PUBLISHED,
        PASSWORD_CHANGED,
        ACCOUNT_LOCKED,
        SUSPICIOUS_ACTIVITY,
        DATA_EXPORT,
        CONFIGURATION_CHANGED
    }
    
    public enum Severity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }
}
