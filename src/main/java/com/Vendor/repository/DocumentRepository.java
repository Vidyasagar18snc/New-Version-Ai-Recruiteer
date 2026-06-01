package com.Vendor.repository;

import com.Vendor.model.Documents;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface DocumentRepository extends MongoRepository<Documents, String> {
    List<Documents> findByCandidateId(String candidateId);
}