package com.Vendor.repository;

import com.Vendor.model.TestViolation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestViolationRepository extends MongoRepository<TestViolation,String> {

    long countByToken(String token);

    List<TestViolation> findByToken(String token);
}