package com.Vendor.repository;


import com.Vendor.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByTitleIgnoreCase(String title);
Optional<Job> findById(Long id);}