package com.Vendor.service;

import com.Vendor.config.GoogleCalendarConfig;
import com.Vendor.dto.CandidateAccessToken;
import com.Vendor.dto.SlotSelectionRequest;
import com.Vendor.model.Candidate;
import com.Vendor.model.Interview;
import com.Vendor.repository.CandidateAccessTokenRepository;
import com.Vendor.repository.CandidateRepository;
import com.Vendor.repository.InterviewRepository;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final GoogleCalendarConfig config;

    private final InterviewRepository interviewRepo;

    private final EmailService emailService;

    private final CandidateAccessTokenRepository tokenRepository;

    private final CandidateRepository candidateRepository;
    private  final AvailabilityService availabilityService;

    private static final int INTERVIEW_DURATION = 60;
    public synchronized Interview scheduleInterview(String candidateName, String candidateId, String panelEmail, String selectedSlot){
        try {
            Calendar service =
                    config.getCalendarService();
            LocalDateTime localDateTime =
                    LocalDateTime.parse(selectedSlot);
            ZonedDateTime zonedDateTime =
                    localDateTime.atZone(
                            ZoneId.of("Asia/Kolkata")
                    );
            Date start =
                    Date.from(
                            zonedDateTime.toInstant()
                    );
            LocalDateTime endLocalDateTime =
                    localDateTime.plusMinutes(
                            INTERVIEW_DURATION
                    );

            ZonedDateTime endZone = endLocalDateTime.atZone(ZoneId.of("Asia/Kolkata"));

            Date end = Date.from(endZone.toInstant());
            boolean alreadyBooked =
                    interviewRepo
                            .existsByInterviewerEmailAndStartTime(panelEmail, start);

            if (alreadyBooked) {

                throw new RuntimeException("Selected slot already booked");
            }
            Event event = new Event()

                    .setSummary("Interview - " + candidateName)

                    .setDescription("Technical Interview")
                    .setStart(new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(start))
                                    .setTimeZone("Asia/Kolkata")
                    )
                    .setEnd(new EventDateTime()
                            .setDateTime(new com.google.api.client.util.DateTime(end))
                                    .setTimeZone("Asia/Kolkata")
                    );
            List<EventAttendee> attendees = new ArrayList<>();
            attendees.add(new EventAttendee().setEmail(panelEmail));
            event.setAttendees(attendees);
            ConferenceData conferenceData = new ConferenceData().setCreateRequest(
                                    new CreateConferenceRequest()
                                            .setRequestId(UUID.randomUUID().toString())
                                            .setConferenceSolutionKey(new ConferenceSolutionKey()
                                                    .setType("hangoutsMeet")
                                            )
                            );
            event.setConferenceData(conferenceData);
            Event createdEvent =
                    service.events()
                            .insert("primary", event)
                            .setConferenceDataVersion(1)
                            .execute();

            Interview interview =
                    new Interview();
            interview.setCandidateId(candidateId);
            interview.setCandidateName(candidateName);
            interview.setInterviewerEmail(panelEmail);
            interview.setStartTime(start);
            interview.setEndTime(end);
            interview.setMeetLink(
                    createdEvent.getHangoutLink()
            );

            Interview savedInterview = interviewRepo.save(interview);
            Candidate candidate = candidateRepository.findById(candidateId)
                            .orElseThrow(() ->
                                    new RuntimeException("Candidate not found")
                            );
            candidate.setStatus("Scheduled");
            candidateRepository.save(candidate);
            LocalDateTime interviewTime =
                    savedInterview.getStartTime()
                            .toInstant()
                            .atZone(ZoneId.of("Asia/Kolkata"))
                            .toLocalDateTime();
            emailService.sendInterviewEmail(
                    candidate.getEmail(),
                    candidateName,
                    savedInterview.getMeetLink(),
                    interviewTime
            );
            emailService.sendInterviewerNotification(
                    panelEmail,
                    candidateName,
                    candidate.getRole(),
                    savedInterview.getMeetLink(),
                    interviewTime
            );
            return savedInterview;
        } catch (ResponseStatusException e) {
        throw e;
    } catch (Exception e) {
        e.printStackTrace();
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Panel member is busy at this moment. Please choose another slot"
        );
    }
    }
    //New Logic 14/05/2026
    public Interview scheduleInterviewUsingToken(
            SlotSelectionRequest request
    ) {

        CandidateAccessToken token =
                tokenRepository.findByToken(
                        request.getToken()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Invalid token"
                        )
                );

        // Token already used
        if (token.isUsed()) {

            throw new RuntimeException(
                    "Token already used"
            );  
        }

        // Token expired
        if (token.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Token expired"
            );
        }

        // Get candidate
        Candidate candidate =
                candidateRepository.findById(
                        token.getCandidateId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Candidate not found"
                        )
                );

        // CALL YOUR EXISTING METHOD
        Interview interview =
                scheduleInterview(
                        candidate.getName(),
                        candidate.getId(),
                        candidate.getPanelEmail(),
                        request.getSelectedSlot()
                );

        // Mark token used
        token.setUsed(true);

        tokenRepository.save(token);

        return interview;
    }
    public List<String> getSlotsByToken(
            String tokenValue
    ) {

        CandidateAccessToken token =
                tokenRepository.findByToken(
                        tokenValue
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Invalid Token"
                        )
                );

        // TOKEN ALREADY USED

        if (token.isUsed()) {

            throw new RuntimeException(
                    "Token already used"
            );
        }

        // TOKEN EXPIRED

        if (token.getExpiryTime()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Token expired"
            );
        }

        // GET CANDIDATE

        Candidate candidate =
                candidateRepository.findById(
                        token.getCandidateId()
                ).orElseThrow(
                        () -> new RuntimeException(
                                "Candidate not found"
                        )
                );

        // FETCH REAL SLOTS

        return availabilityService.getFreeSlots(
                candidate.getPanelEmail(),
                LocalDate.now()
                        .plusDays(1)
                        .toString()
        );
    }
}