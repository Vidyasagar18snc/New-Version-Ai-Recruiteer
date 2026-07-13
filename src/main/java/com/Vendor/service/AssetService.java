package com.Vendor.service;

import com.Vendor.dto.AssetAssignment;
import com.Vendor.model.Asset;
import com.Vendor.model.Employee;
import com.Vendor.repository.AssetAssignmentRepository;
import com.Vendor.repository.AssetRepository;
import com.Vendor.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetAssignmentRepository assetAssignmentRepository;

    public Asset createAsset(Asset asset) {
        asset.setStatus("AVAILABLE");
        return assetRepository.save(asset);
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public String assignAsset(
            String assetId,
            String employeeId,
            String assignedBy,
            String assetType,
            String condition,
            String remarks,
            String accessories) {

        Asset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        if (!"AVAILABLE".equals(asset.getStatus())) {
            throw new RuntimeException(asset.getAssetName() + " already assigned");
        }

        Employee employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<AssetAssignment> existingAssignments =
                assetAssignmentRepository.findByEmployeeIdAndAssetIdAndStatus(
                        employeeId,
                        assetId,
                        "ASSIGNED"
                );

        if (!existingAssignments.isEmpty()) {
            throw new RuntimeException(
                    employee.getEmployeeName() + " already has this asset assigned"
            );
        }
        AssetAssignment assignment = new AssetAssignment();
        assignment.setEmployeeId(employee.getEmployeeId());
        assignment.setEmployeeName(employee.getEmployeeName());
        assignment.setAssetId(asset.getAssetId());
        assignment.setAssetName(asset.getAssetName());
        assignment.setBrand(asset.getBrand());
        assignment.setCategory(asset.getCategory());
        assignment.setSerialNumber(asset.getSerialNumber());
        assignment.setAssignedDate(LocalDate.now());
        assignment.setStatus("ASSIGNED");
        assignment.setAssignedBy(assignedBy);
        assignment.setAssetType(assetType);
        assignment.setCondition(condition);
        assignment.setRemarks(remarks);
        assignment.setAccessories(accessories);
        assetAssignmentRepository.save(assignment);
        asset.setStatus("ASSIGNED");
        assetRepository.save(asset);
        return "Asset Assigned Successfully";
    }

    public List<AssetAssignment> getEmployeeAssets(String employeeId) {
        return assetAssignmentRepository.findByEmployeeId(employeeId);
    }

    public String returnAsset(String assetId, String employeeId) {
        AssetAssignment assignment = assetAssignmentRepository
                .findByAssetIdAndEmployeeIdAndStatus(assetId, employeeId, "ASSIGNED")
                .orElseThrow(() -> new RuntimeException("Assigned asset not found"));
        if ("RETURNED".equals(assignment.getStatus())) {
            throw new RuntimeException("Asset already returned");
        }
        assignment.setStatus("RETURNED");
        assignment.setReturnedDate(LocalDate.now());
        assetAssignmentRepository.save(assignment);
        Asset asset = assetRepository.findByAssetId(assetId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setStatus("AVAILABLE");
        assetRepository.save(asset);
        return "Asset Returned Successfully";
    }

    public Asset updateAsset(String id, Asset updatedAsset) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setAssetName(updatedAsset.getAssetName());
        asset.setCategory(updatedAsset.getCategory());
        asset.setBrand(updatedAsset.getBrand());
        asset.setSerialNumber(updatedAsset.getSerialNumber());
        return assetRepository.save(asset);
    }

    public String deleteAsset(String id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        assetRepository.delete(asset);
        return "Asset Deleted Successfully";
    }
    public List<AssetAssignment> getAllAssignments() {
        return assetAssignmentRepository.findAll();
    }
    public List<AssetAssignment> getAssignedAssets() {
        return assetAssignmentRepository.findByStatus("ASSIGNED");
    }

    public List<AssetAssignment> getEmployeeAssetHistory(String employeeId) {
        return assetAssignmentRepository.findByEmployeeId(employeeId);
    }

    public List<AssetAssignment> getAssetHistory(String assetId) {
        return assetAssignmentRepository.findByAssetId(assetId);
    }
}