package com.votingsystem.controller;

import com.votingsystem.model.Candidate;
import com.votingsystem.model.Election;
import com.votingsystem.service.VotingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Voter Controller
 */
@RestController
@RequestMapping("/api/voter")
@CrossOrigin(origins = "*")
public class VoterController {
    
    @Autowired
    private VotingService votingService;
    
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<Map<String, Object>> getDashboard(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> dashboard = votingService.getVoterDashboard(username);
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/elections/active")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<List<Election>> getActiveElections() {
        List<Election> elections = votingService.getActiveElections();
        return ResponseEntity.ok(elections);
    }
    
    @GetMapping("/elections/{electionId}/candidates")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<List<Candidate>> getCandidates(@PathVariable Long electionId) {
        List<Candidate> candidates = votingService.getCandidatesByElection(electionId);
        return ResponseEntity.ok(candidates);
    }
    
    @PostMapping("/vote")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<Map<String, Object>> castVote(@RequestBody Map<String, Long> voteData,
                                                         Authentication authentication,
                                                         HttpServletRequest request) {
        String username = authentication.getName();
        Long electionId = voteData.get("electionId");
        Long candidateId = voteData.get("candidateId");
        
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession().getId();
        
        Map<String, Object> response = votingService.castVote(
                username, electionId, candidateId, ipAddress, userAgent, sessionId);
        
        if ((Boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/vote/status/{electionId}")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<Map<String, Object>> getVoteStatus(@PathVariable Long electionId,
                                                              Authentication authentication) {
        String username = authentication.getName();
        boolean hasVoted = votingService.hasVoted(username, electionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("hasVoted", hasVoted);
        response.put("electionId", electionId);
        
        return ResponseEntity.ok(response);
    }
}
