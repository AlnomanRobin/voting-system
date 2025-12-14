package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Election entity
 */
@Entity
@Table(name = "elections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Election {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionType type;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    @Column(nullable = false)
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ElectionStatus status = ElectionStatus.SCHEDULED;
    
    @Column(nullable = false)
    private Boolean resultsPublished = false;
    
    private LocalDateTime resultsPublishedAt;
    
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Candidate> candidates = new HashSet<>();
    
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    private Set<Vote> votes = new HashSet<>();
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    public enum ElectionType {
        PRESIDENTIAL,
        PARLIAMENTARY,
        LOCAL,
        REFERENDUM,
        OTHER
    }
    
    public enum ElectionStatus {
        SCHEDULED,
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
    
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == ElectionStatus.ACTIVE && 
               now.isAfter(startTime) && 
               now.isBefore(endTime);
    }
    
    public boolean isCompleted() {
        return status == ElectionStatus.COMPLETED || 
               LocalDateTime.now().isAfter(endTime);
    }
}
