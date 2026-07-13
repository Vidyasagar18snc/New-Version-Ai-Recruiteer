package com.Vendor.repository;

import com.Vendor.model.Onboarding;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.Optional;

public interface OnboardingRepository extends MongoRepository<Onboarding, String> {
    Optional<Onboarding> findByCandidateId(
            String candidateId
    );
    Optional<Onboarding>findById(String id);
    long countByOnboardingCompletedFalseAndCreatedAtBetween(Date startDate, Date endDate);

    Optional<Onboarding> findByEmail(String email);
}