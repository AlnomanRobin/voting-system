package com.votingsystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Voter entity extending User
 */
@Entity
@Table(name = "voters")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Voter extends User {
    
    @Column(unique = true, nullable = false, length = 20)
    private String voterId;
    
    @Column(unique = true, nullable = false, length = 20)
    private String nationalId;
    
    @Column(nullable = false, length = 100)
    private String fullName;
    
    @Column(nullable = false)
    private LocalDate dateOfBirth;
    
    @Column(nullable = false, length = 10)
    private String gender;
    
    @Column(nullable = false, length = 200)
    private String address;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 50)
    private String state;
    
    @Column(length = 10)
    private String zipCode;
    
    @Column(length = 15)
    private String phoneNumber;
    
    @Column(length = 100)
    private String email;
    
    @Column(nullable = false)
    private Boolean verified = false;
    
    @Column(nullable = false)
    private Boolean eligible = true;
    
    private String photoPath;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoterStatus status = VoterStatus.PENDING;
    
    public enum VoterStatus {
        PENDING,
        VERIFIED,
        REJECTED,
        SUSPENDED
    }
}
