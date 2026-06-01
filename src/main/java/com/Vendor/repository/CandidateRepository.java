package com.Vendor.repository;


import com.Vendor.model.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    Optional<Candidate> findByEmailAndRole(String email, String role);

    List<Candidate> findByStatusIgnoreCase(String shortlisted);
    List<Candidate> findByAssignedPanelId(
            String panelId
    );

    long countByAssignedPanelId(
            String panelId
    );
    boolean existsByEmail(String email);

    List<Candidate> findByName(String name);
}