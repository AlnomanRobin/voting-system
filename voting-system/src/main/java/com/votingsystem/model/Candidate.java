package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Candidate entity
 */
@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 100)
    private String partyName;
    
    @Column(length = 100)
    private String partySymbol;
    
    @Column(columnDefinition = "TEXT")
    private String biography;
    
    @Column(columnDefinition = "TEXT")
    private String manifesto;
    
    private String photoPath;
    
    private String symbolImagePath;
    
    @Column(length = 100)
    private String education;
    
    @Column(columnDefinition = "TEXT")
    private String experience;
    
    private Integer age;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private Integer displayOrder = 0;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Vote count - calculated field, not stored
    @Transient
    private Long voteCount = 0L;
}
