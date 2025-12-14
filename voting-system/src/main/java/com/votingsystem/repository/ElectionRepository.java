package com.votingsystem.repository;

import com.votingsystem.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    
    List<Election> findByStatus(Election.ElectionStatus status);
    
    @Query("SELECT e FROM Election e WHERE e.status = 'ACTIVE' AND e.startTime <= :now AND e.endTime > :now")
    List<Election> findActiveElections(LocalDateTime now);
    
    @Query("SELECT e FROM Election e WHERE e.endTime < :now AND e.status = 'ACTIVE'")
    List<Election> findExpiredActiveElections(LocalDateTime now);
    
    List<Election> findByType(Election.ElectionType type);
    
    @Query("SELECT COUNT(e) FROM Election e WHERE e.status = 'ACTIVE'")
    Long countActiveElections();
    
    @Query("SELECT e FROM Election e WHERE e.resultsPublished = true ORDER BY e.endTime DESC")
    List<Election> findElectionsWithPublishedResults();
}
