package com.Vendor.repository;

import com.Vendor.model.DeboardingRecord;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeboardingRepository
        extends MongoRepository<DeboardingRecord,String> {
}