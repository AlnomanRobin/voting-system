package com.votingsystem.repository;

import com.votingsystem.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    List<Candidate> findByElectionId(Long electionId);
    
    List<Candidate> findByElectionIdAndActive(Long electionId, Boolean active);
    
    @Query("SELECT c FROM Candidate c WHERE c.election.id = :electionId ORDER BY c.displayOrder ASC")
    List<Candidate> findByElectionIdOrderByDisplayOrder(@Param("electionId") Long electionId);
    
    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.election.id = :electionId AND c.active = true")
    Long countActiveCandidatesByElection(@Param("electionId") Long electionId);
}
