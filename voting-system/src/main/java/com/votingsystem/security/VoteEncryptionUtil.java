package com.votingsystem.security;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class for vote encryption and hashing
 */
@Component
public class VoteEncryptionUtil {
    
    /**
     * Generate a secure hash for vote verification
     */
    public String generateVoteHash(Long voterId, Long electionId, Long candidateId, String timestamp) {
        try {
            String data = voterId + ":" + electionId + ":" + candidateId + ":" + timestamp;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating vote hash", e);
        }
    }
    
    /**
     * Verify vote hash integrity
     */
    public boolean verifyVoteHash(String hash, Long voterId, Long electionId, Long candidateId, String timestamp) {
        String generatedHash = generateVoteHash(voterId, electionId, candidateId, timestamp);
        return generatedHash.equals(hash);
    }
}
