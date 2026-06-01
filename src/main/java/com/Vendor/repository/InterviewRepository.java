package com.Vendor.repository;

import com.Vendor.model.Interview;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface InterviewRepository extends MongoRepository<Interview, String> {

    boolean existsByInterviewerIdAndStartTime(
            String interviewerId,
            Date startTime
    );

    List<Interview> findByStartTimeBetween(
            Date start,
            Date end
    );

    List<Interview> findByInterviewerId(
            String interviewerId
    );

    boolean existsByInterviewerEmailAndStartTime(
            String panelEmail,
            Date start
    );

    boolean existsByCandidateId(
            String candidateId
    );


   }