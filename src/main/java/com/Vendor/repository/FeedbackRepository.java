package com.Vendor.repository;

import com.Vendor.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    @Override
    List<Feedback> findAll();
}