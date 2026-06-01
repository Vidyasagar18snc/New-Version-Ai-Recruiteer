package com.Vendor.repository;

import com.Vendor.dto.AssetAssignment;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

import java.util.Optional;

public interface AssetAssignmentRepository
        extends MongoRepository<AssetAssignment,String> {

    List<AssetAssignment>

    findByEmployeeId(
            String employeeId
    );

    List<AssetAssignment>

    findByStatus(
            String status
    );

    List<AssetAssignment>

    findByAssetId(
            String assetId
    );

    Optional<AssetAssignment>

    findByAssetIdAndEmployeeIdAndStatus(

            String assetId,

            String employeeId,

            String status
    );

    // DUPLICATE ASSIGNMENT CHECK

    List<AssetAssignment>

    findByEmployeeIdAndAssetIdAndStatus(

            String employeeId,

            String assetId,

            String status
    );
}