package com.Vendor.repository;

import com.Vendor.model.OfferRequestDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OfferRepository
        extends MongoRepository<OfferRequestDTO, String> {

    Optional<OfferRequestDTO>
    findByOfferToken(String token);
}