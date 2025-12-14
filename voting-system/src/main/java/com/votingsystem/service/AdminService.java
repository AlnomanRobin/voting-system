package com.votingsystem.service;

import com.votingsystem.model.*;
import com.votingsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Admin Service - handles all administrative operations
 */
@Service
public class AdminService {
    
    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private VoterRepository voterRepository;
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ==================== Dashboard ====================
    
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalVoters", voterRepository.count());
        stats.put("verifiedVoters", voterRepository.countVerifiedVoters());
        stats.put("pendingVoters", voterRepository.countPendingVoters());
        stats.put("totalElections", electionRepository.count());
        stats.put("activeElections", electionRepository.countActiveElections());
        stats.put("totalVotes", voteRepository.count());
        stats.put("suspiciousActivities", auditLogService.getSuspiciousActivityCount());
        
        return stats;
    }
    
    // ==================== Election Management ====================
    
    @Transactional
    public Election createElection(Election election, User admin) {
        election.setCreatedBy(admin);
        election.setStatus(Election.ElectionStatus.SCHEDULED);
        Election saved = electionRepository.save(election);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.ELECTION_CREATED, 
                "Election created", "Election: " + election.getName(), null);
        
        return saved;
    }
    
    @Transactional
    public Election updateElection(Long id, Election updatedElection, User admin) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        
        election.setName(updatedElection.getName());
        election.setDescription(updatedElection.getDescription());
        election.setType(updatedElection.getType());
        election.setStartTime(updatedElection.getStartTime());
        election.setEndTime(updatedElection.getEndTime());
        
        Election saved = electionRepository.save(election);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.ELECTION_UPDATED, 
                "Election updated", "Election: " + election.getName(), null);
        
        return saved;
    }
    
    @Transactional
    public void deleteElection(Long id, User admin) {
        Election election = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        
        electionRepository.delete(election);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.ELECTION_DELETED, 
                "Election deleted", "Election: " + election.getName(), null);
    }
    
    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }
    
    @Transactional
    public void publishResults(Long electionId, User admin) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        
        if (!election.isCompleted()) {
            throw new RuntimeException("Cannot publish results for ongoing election");
        }
        
        election.setResultsPublished(true);
        election.setResultsPublishedAt(LocalDateTime.now());
        electionRepository.save(election);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.RESULT_PUBLISHED, 
                "Results published", "Election: " + election.getName(), null);
    }
    
    // ==================== Candidate Management ====================
    
    @Transactional
    public Candidate addCandidate(Candidate candidate, User admin) {
        Candidate saved = candidateRepository.save(candidate);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.CANDIDATE_ADDED, 
                "Candidate added", "Candidate: " + candidate.getName(), null);
        
        return saved;
    }
    
    @Transactional
    public Candidate updateCandidate(Long id, Candidate updatedCandidate, User admin) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        
        candidate.setName(updatedCandidate.getName());
        candidate.setPartyName(updatedCandidate.getPartyName());
        candidate.setPartySymbol(updatedCandidate.getPartySymbol());
        candidate.setBiography(updatedCandidate.getBiography());
        candidate.setManifesto(updatedCandidate.getManifesto());
        candidate.setActive(updatedCandidate.getActive());
        
        Candidate saved = candidateRepository.save(candidate);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.CANDIDATE_UPDATED, 
                "Candidate updated", "Candidate: " + candidate.getName(), null);
        
        return saved;
    }
    
    @Transactional
    public void deleteCandidate(Long id, User admin) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        
        candidateRepository.delete(candidate);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.CANDIDATE_DELETED, 
                "Candidate deleted", "Candidate: " + candidate.getName(), null);
    }
    
    // ==================== Voter Management ====================
    
    public List<Voter> getAllVoters() {
        return voterRepository.findAll();
    }
    
    public List<Voter> getPendingVoters() {
        return voterRepository.findByStatus(Voter.VoterStatus.PENDING);
    }
    
    @Transactional
    public void verifyVoter(Long voterId, User admin) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));
        
        voter.setVerified(true);
        voter.setStatus(Voter.VoterStatus.VERIFIED);
        voterRepository.save(voter);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.VOTER_VERIFIED, 
                "Voter verified", "Voter ID: " + voter.getVoterId(), null);
    }
    
    @Transactional
    public void suspendVoter(Long voterId, User admin) {
        Voter voter = voterRepository.findById(voterId)
                .orElseThrow(() -> new RuntimeException("Voter not found"));
        
        voter.setStatus(Voter.VoterStatus.SUSPENDED);
        voter.setEligible(false);
        voterRepository.save(voter);
        
        auditLogService.logSuccess(admin, AuditLog.ActionType.VOTER_SUSPENDED, 
                "Voter suspended", "Voter ID: " + voter.getVoterId(), null);
    }
    
    // ==================== Results ====================
    
    public Map<String, Object> getElectionResults(Long electionId) {
        Map<String, Object> results = new HashMap<>();
        
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        
        results.put("election", election);
        results.put("totalVotes", voteRepository.countVotesByElection(electionId));
        
        List<Candidate> candidates = candidateRepository.findByElectionId(electionId);
        List<Map<String, Object>> candidateResults = new ArrayList<>();
        
        for (Candidate candidate : candidates) {
            Map<String, Object> candidateData = new HashMap<>();
            candidateData.put("id", candidate.getId());
            candidateData.put("name", candidate.getName());
            candidateData.put("party", candidate.getPartyName());
            candidateData.put("votes", voteRepository.countVotesByCandidate(candidate.getId()));
            candidateResults.add(candidateData);
        }
        
        // Sort by votes descending
        candidateResults.sort((a, b) -> 
            Long.compare((Long)b.get("votes"), (Long)a.get("votes")));
        
        results.put("candidates", candidateResults);
        
        return results;
    }
}
