package com.Vendor.service;


import com.Vendor.model.OfferRequestDTO;
import com.Vendor.model.Onboarding;
import com.Vendor.repository.OnboardingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;

    public void startOnboarding(
            OfferRequestDTO offer
    ) {

        Onboarding onboarding =
                new Onboarding();

        onboarding.setCandidateId(
                offer.getCandidateId());

        onboarding.setCandidateName(
                offer.getName());

        onboarding.setEmail(
                offer.getEmail());

        onboarding.setHrEmail(
                offer.getHrEmail());

        onboarding.setRole(
                offer.getRole());

        onboarding.setDepartment(
                offer.getDepartment());

        onboarding.setJoiningDate(
                offer.getJoiningDate());

        onboarding.setOnboardingStatus(
                "ONBOARDING_STARTED");

        onboarding.setDocumentsSubmitted(
                false);

        onboarding.setHrVerified(
                false);

        onboarding.setOnboardingCompleted(
                false);

        onboarding.setCreatedAt(
                LocalDateTime.now());

        onboardingRepository.save(onboarding);
    }
}