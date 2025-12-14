package com.votingsystem.repository;

import com.votingsystem.model.Voter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoterRepository extends JpaRepository<Voter, Long> {
    
    Optional<Voter> findByVoterId(String voterId);
    
    Optional<Voter> findByNationalId(String nationalId);
    
    Optional<Voter> findByUsername(String username);
    
    boolean existsByVoterId(String voterId);
    
    boolean existsByNationalId(String nationalId);
    
    List<Voter> findByStatus(Voter.VoterStatus status);
    
    List<Voter> findByVerified(Boolean verified);
    
    @Query("SELECT COUNT(v) FROM Voter v WHERE v.verified = true")
    Long countVerifiedVoters();
    
    @Query("SELECT COUNT(v) FROM Voter v WHERE v.status = 'PENDING'")
    Long countPendingVoters();
}
