package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Vote entity - stores encrypted vote information
 */
@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"voter_id", "election_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private Voter voter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime votedAt;
    
    @Column(nullable = false, length = 50)
    private String ipAddress;
    
    @Column(nullable = false, length = 200)
    private String userAgent;
    
    @Column(nullable = false, length = 64)
    private String voteHash;
    
    @Column(nullable = false)
    private Boolean verified = true;
    
    @Column(nullable = false)
    private String sessionId;
}
