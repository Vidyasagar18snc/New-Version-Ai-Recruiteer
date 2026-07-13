package com.Vendor.repository;

import com.Vendor.model.DeboardingRecord;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface DeboardingRepository
        extends MongoRepository<DeboardingRecord,String> {
    long countByStatus(String status);
    long countByStatusAndLastWorkingDateBetween(String status, Date startDate, Date endDate);

}