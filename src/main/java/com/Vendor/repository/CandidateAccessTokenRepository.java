package com.Vendor.repository;

import com.Vendor.dto.CandidateAccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CandidateAccessTokenRepository
        extends MongoRepository<CandidateAccessToken, String> {

    Optional<CandidateAccessToken>
    findByToken(String token);
}