package com.Vendor.controller;

import com.Vendor.dto.OfferAcceptanceRateResponse;
import com.Vendor.dto.OfferRejectRequest;
import com.Vendor.dto.TimeToHireResponse;
import com.Vendor.model.OfferRequestDTO;
import com.Vendor.repository.OfferRepository;
import com.Vendor.service.EmailService;
import com.Vendor.service.OfferService;
import com.Vendor.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final OfferRepository offerRepository;
    private final EmailService emailService;
    private final OnboardingService onboardingService;

    @PostMapping("/send")
    public String sendOffer(@RequestBody OfferRequestDTO dto) {
        return offerService.sendOffer(dto);
    }
    @GetMapping("/{token}")
    public ResponseEntity<?> getOffer(
            @PathVariable String token
    ) {

        OfferRequestDTO offer =
                offerRepository.findByOfferToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid token"));

        // UPDATE VIEWED STATUS

        if (offer.getOfferStatus().equals("SENT")) {

            offer.setOfferStatus("VIEWED");

            offerRepository.save(offer);
        }

        return ResponseEntity.ok(offer);
    }
    @PostMapping("/respond")
    public ResponseEntity<?> respondOffer(
            @RequestParam String token,
            @RequestParam String action,
            @RequestParam(required = false) String rejectionReason
    ) {

        OfferRequestDTO offer =
                offerRepository.findByOfferToken(token)
                        .orElseThrow(() ->
                                new RuntimeException("Invalid token"));

        if (action.equalsIgnoreCase("accept")) {

            offer.setOfferStatus("ACCEPTED");

            onboardingService.startOnboarding(offer);

            emailService.sendHrOfferAcceptedMail(
                    offer.getHrEmail(),
                    offer.getName(),
                    offer.getRole()
            );

            emailService.sendCandidateOfferAcceptedMail(
                    offer.getEmail(),
                    offer.getName(),
                    offer.getCandidateId()
            );

        } else {

            offer.setOfferStatus("REJECTED");

            // Save rejection reason
            offer.setRejectionReason(rejectionReason);

            emailService.sendHrOfferRejectedMail(
                    offer.getHrEmail(),
                    offer.getName(),
                    offer.getRole(),
                    rejectionReason
            );

            emailService.sendCandidateOfferRejectedMail(
                    offer.getEmail(),
                    offer.getName()
            );
        }

        offer.setRespondedAt(LocalDateTime.now());

        offerRepository.save(offer);

        return ResponseEntity.ok().body(
                java.util.Map.of(
                        "message",
                        "Offer response updated"
                )
        );
    }
    @GetMapping("/acceptance-summary")
    public ResponseEntity<OfferAcceptanceRateResponse> getOfferAcceptanceSummary() {
        return ResponseEntity.ok(offerService.getOfferAcceptanceSummary());
    }
    @GetMapping("/time-to-hire-summary")
    public ResponseEntity<TimeToHireResponse> getTimeToHireSummary() {
        return ResponseEntity.ok(offerService.getTimeToHireSummary());
    }

}