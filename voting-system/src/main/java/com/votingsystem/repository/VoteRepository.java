package com.votingsystem.repository;

import com.votingsystem.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByVoterIdAndElectionId(Long voterId, Long electionId);
    
    boolean existsByVoterIdAndElectionId(Long voterId, Long electionId);
    
    List<Vote> findByElectionId(Long electionId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.election.id = :electionId")
    Long countVotesByElection(@Param("electionId") Long electionId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.candidate.id = :candidateId")
    Long countVotesByCandidate(@Param("candidateId") Long candidateId);
    
    @Query("SELECT v.candidate.id, COUNT(v) FROM Vote v WHERE v.election.id = :electionId GROUP BY v.candidate.id")
    List<Object[]> countVotesByCandidateForElection(@Param("electionId") Long electionId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.votedAt BETWEEN :start AND :end")
    Long countVotesBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    List<Vote> findByVoterId(Long voterId);
}
