package com.Vendor.service;


import com.Vendor.dto.PendingOnboardingResponse;
import com.Vendor.model.OfferRequestDTO;
import com.Vendor.model.Onboarding;
import com.Vendor.repository.OnboardingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final EmailService emailService;

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
    public void sendVerification(
            String id,
            String hrEmail
    ) {

        Onboarding onboarding =
                onboardingRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Onboarding not found"
                                )
                        );

        emailService.sendBackgroundVerificationMail(
                hrEmail,
                onboarding
        );

        onboarding.setPreviousCompanyHrEmail(hrEmail);
        onboarding.setBackgroundVerificationStatus("MAIL_SENT");
        onboarding.setVerificationMailSentAt(LocalDateTime.now());

        onboardingRepository.save(onboarding);
    }
    public List<Onboarding> getAllOnboardingCandidates() {

        return onboardingRepository.findAll();
    }
    public long getOnboardingCount() {
        return onboardingRepository.count();
    }
// ── PENDING ONBOARDING SUMMARY ───────────────────────────────────────

    public long getCurrentMonthPendingOnboardingCount() {
        LocalDate now = LocalDate.now();

        Date startDate = toDate(now.withDayOfMonth(1));
        Date endDate = toEndOfDay(now.withDayOfMonth(now.lengthOfMonth()));

        return onboardingRepository.countByOnboardingCompletedFalseAndCreatedAtBetween(startDate, endDate);
    }

    public long getLastMonthPendingOnboardingCount() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        Date startDate = toDate(lastMonth.withDayOfMonth(1));
        Date endDate = toEndOfDay(lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));

        return onboardingRepository.countByOnboardingCompletedFalseAndCreatedAtBetween(startDate, endDate);
    }

    public long getPendingOnboardingChange() {
        return getCurrentMonthPendingOnboardingCount() - getLastMonthPendingOnboardingCount();
    }

    public PendingOnboardingResponse getPendingOnboardingSummary() {
        long currentMonthPendingCount = getCurrentMonthPendingOnboardingCount();
        long lastMonthPendingCount = getLastMonthPendingOnboardingCount();
        long change = currentMonthPendingCount - lastMonthPendingCount;

        return new PendingOnboardingResponse(
                currentMonthPendingCount,
                lastMonthPendingCount,
                change
        );
    }

// ── HELPER METHODS ───────────────────────────────────────────────────

    private Date toDate(LocalDate localDate) {
        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    private Date toEndOfDay(LocalDate localDate) {
        return Date.from(
                localDate.atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

}