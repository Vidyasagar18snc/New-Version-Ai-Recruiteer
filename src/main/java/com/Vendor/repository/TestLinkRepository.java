package com.Vendor.repository;


import com.Vendor.model.TestLink;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TestLinkRepository extends MongoRepository<TestLink, String> {

    Optional<TestLink> findByToken(String token);
    Optional<TestLink> findByCandidateId(String candidateId); // 🔥 NEW

    // 🔥 REQUIRED FOR RANKING (VERY IMPORTANT)
    List<TestLink> findByTestId(String testId);

    // 🔥 OPTIONAL (GOOD PRACTICE - cleaner filtering)
    List<TestLink> findByTestIdAndAttemptedTrue(String testId);
}