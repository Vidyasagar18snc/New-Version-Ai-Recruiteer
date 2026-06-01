package com.Vendor.repository;

import com.Vendor.model.KnowledgeTransfer;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface KnowledgeTransferRepository
        extends MongoRepository<KnowledgeTransfer,String> {

    List<KnowledgeTransfer>

    findByEmployeeId(
            String employeeId
    );
    Optional<KnowledgeTransfer>

    findTopByEmployeeIdOrderByKtDateDesc(
            String employeeId
    );
}