package com.votingsystem.service;

import com.votingsystem.model.*;
import com.votingsystem.repository.CandidateRepository;
import com.votingsystem.repository.ElectionRepository;
import com.votingsystem.repository.VoteRepository;
import com.votingsystem.repository.VoterRepository;
import com.votingsystem.security.VoteEncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Voting Service - handles all voting operations
 */
@Service
public class VotingService {
    
    @Autowired
    private VoteRepository voteRepository;
    
    @Autowired
    private VoterRepository voterRepository;
    
    @Autowired
    private ElectionRepository electionRepository;
    
    @Autowired
    private CandidateRepository candidateRepository;
    
    @Autowired
    private VoteEncryptionUtil voteEncryptionUtil;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Transactional
    public Map<String, Object> castVote(String username, Long electionId, Long candidateId, 
                                        String ipAddress, String userAgent, String sessionId) {
        Map<String, Object> response = new HashMap<>();
        
        // Get voter
        Voter voter = voterRepository.findByUsername(username)
                .orElse(null);
        
        if (voter == null) {
            response.put("success", false);
            response.put("message", "Voter not found");
            return response;
        }
        
        // Check if voter is verified and eligible
        if (!voter.getVerified() || !voter.getEligible()) {
            auditLogService.logError(voter, AuditLog.ActionType.VOTE_CAST, 
                    "Attempted to vote - not eligible", 
                    "Election: " + electionId, ipAddress);
            response.put("success", false);
            response.put("message", "You are not eligible to vote");
            return response;
        }
        
        // Get election
        Election election = electionRepository.findById(electionId)
                .orElse(null);
        
        if (election == null) {
            response.put("success", false);
            response.put("message", "Election not found");
            return response;
        }
        
        // Check if election is active
        if (!election.isActive()) {
            auditLogService.logError(voter, AuditLog.ActionType.VOTE_CAST, 
                    "Attempted to vote in inactive election", 
                    "Election: " + election.getName(), ipAddress);
            response.put("success", false);
            response.put("message", "Election is not currently active");
            return response;
        }
        
        // Check if voter has already voted
        if (voteRepository.existsByVoterIdAndElectionId(voter.getId(), electionId)) {
            auditLogService.logError(voter, AuditLog.ActionType.VOTE_CAST, 
                    "Attempted to vote twice", 
                    "Election: " + election.getName(), ipAddress);
            response.put("success", false);
            response.put("message", "You have already voted in this election");
            return response;
        }
        
        // Get candidate
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElse(null);
        
        if (candidate == null || !candidate.getActive()) {
            response.put("success", false);
            response.put("message", "Invalid candidate");
            return response;
        }
        
        // Verify candidate belongs to election
        if (!candidate.getElection().getId().equals(electionId)) {
            response.put("success", false);
            response.put("message", "Candidate not in this election");
            return response;
        }
        
        // Create vote
        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setElection(election);
        vote.setCandidate(candidate);
        vote.setIpAddress(ipAddress);
        vote.setUserAgent(userAgent);
        vote.setSessionId(sessionId);
        
        // Generate vote hash
        String timestamp = LocalDateTime.now().toString();
        String voteHash = voteEncryptionUtil.generateVoteHash(
                voter.getId(), electionId, candidateId, timestamp);
        vote.setVoteHash(voteHash);
        
        voteRepository.save(vote);
        
        auditLogService.logSuccess(voter, AuditLog.ActionType.VOTE_CAST, 
                "Vote cast successfully", 
                "Election: " + election.getName() + ", Candidate: " + candidate.getName(), 
                ipAddress);
        
        response.put("success", true);
        response.put("message", "Your vote has been recorded securely");
        response.put("votedAt", vote.getVotedAt());
        
        return response;
    }
    
    public List<Election> getActiveElections() {
        return electionRepository.findActiveElections(LocalDateTime.now());
    }
    
    public List<Candidate> getCandidatesByElection(Long electionId) {
        return candidateRepository.findByElectionIdOrderByDisplayOrder(electionId);
    }
    
    public boolean hasVoted(String username, Long electionId) {
        Voter voter = voterRepository.findByUsername(username).orElse(null);
        if (voter == null) return false;
        
        return voteRepository.existsByVoterIdAndElectionId(voter.getId(), electionId);
    }
    
    public Map<String, Object> getVoterDashboard(String username) {
        Map<String, Object> dashboard = new HashMap<>();
        
        Voter voter = voterRepository.findByUsername(username).orElse(null);
        if (voter == null) {
            dashboard.put("error", "Voter not found");
            return dashboard;
        }
        
        dashboard.put("voterName", voter.getFullName());
        dashboard.put("voterId", voter.getVoterId());
        dashboard.put("verified", voter.getVerified());
        dashboard.put("status", voter.getStatus().name());
        
        List<Election> activeElections = getActiveElections();
        dashboard.put("activeElections", activeElections);
        
        List<Map<String, Object>> electionStatus = new ArrayList<>();
        for (Election election : activeElections) {
            Map<String, Object> status = new HashMap<>();
            status.put("electionId", election.getId());
            status.put("electionName", election.getName());
            status.put("hasVoted", hasVoted(username, election.getId()));
            electionStatus.add(status);
        }
        dashboard.put("votingStatus", electionStatus);
        
        return dashboard;
    }
}
