package com.Vendor.repository;

import com.Vendor.model.OfferRequestDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OfferRepository
        extends MongoRepository<OfferRequestDTO, String> {

    Optional<OfferRequestDTO>
    findByOfferToken(String token);
    long countByOfferStatusAndCreatedAtBetween(String offerStatus, Date startDate, Date endDate);

    long countByCreatedAtBetween(Date startDate, Date endDate);
    List<OfferRequestDTO> findByOfferStatusAndCreatedAtBetween(
            String offerStatus,
            Date startDate,
            Date endDate
    );

    long countByOfferStatusAndRespondedAtBetween(String accepted, Date startDate, Date endDate);
}