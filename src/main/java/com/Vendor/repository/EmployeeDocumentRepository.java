package com.Vendor.repository;


import com.Vendor.model.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmployeeDocumentRepository
        extends MongoRepository<EmployeeDocument, String> {
    List<EmployeeDocument> findByCandidateId(String candidateId);
}