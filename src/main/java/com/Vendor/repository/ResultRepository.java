package com.Vendor.repository;

import com.Vendor.model.TestResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResultRepository extends MongoRepository<TestResult,String> {

    List<TestResult> findByTestIdOrderByRankAsc( String testId);
     List<TestResult> findAll();

}
