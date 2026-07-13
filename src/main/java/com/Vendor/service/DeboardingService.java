package com.Vendor.service;
import com.Vendor.dto.AssetAssignment;
import com.Vendor.dto.AttritionRateResponse;
import com.Vendor.dto.DeboardingRequest;
import com.Vendor.model.DeboardingRecord;
import com.Vendor.model.Employee;
import com.Vendor.model.KnowledgeTransfer;
import com.Vendor.repository.AssetAssignmentRepository;
import com.Vendor.repository.DeboardingRepository;
import com.Vendor.repository.EmployeeRepository;
import com.Vendor.repository.KnowledgeTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeboardingService {

    private final EmployeeRepository employeeRepository;
    private final DeboardingRepository deboardingRepository;
    private final KnowledgeTransferRepository knowledgeTransferRepository;
    private final AssetAssignmentRepository assetAssignmentRepository;
    private final EmailService emailService;
    private final S3Service s3Service;

    // ── INITIATE KT ──────────────────────────────────────────────────────────

    public String initiateKT(
            String employeeId,
            String transferToEmployeeId,
            MultipartFile file
    ) throws Exception {

        Employee fromEmployee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        Employee toEmployee = employeeRepository
                .findByEmployeeId(transferToEmployeeId)
                .orElseThrow(() -> new RuntimeException("Transfer-to employee not found: " + transferToEmployeeId));

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "KT_Document.pdf";
        }

        String fileName = "kt/" + employeeId + "/" + System.currentTimeMillis() + "_" + originalName;

        s3Service.uploadFile(file.getBytes(), fileName, file.getContentType());
        String documentUrl = s3Service.generatePresignedUrl(fileName);

        KnowledgeTransfer kt = new KnowledgeTransfer();
        kt.setEmployeeId(fromEmployee.getEmployeeId());
        kt.setEmployeeName(fromEmployee.getEmployeeName());
        kt.setDepartment(fromEmployee.getDepartment());
        kt.setTransferToEmployeeId(toEmployee.getEmployeeId());
        kt.setTransferToEmployeeName(toEmployee.getEmployeeName());
        kt.setDocumentName(originalName);
        kt.setDocumentS3Key(fileName);
        kt.setDocumentUrl(documentUrl);
        kt.setStatus("PENDING");
        kt.setKtDate(LocalDate.now());

        knowledgeTransferRepository.save(kt);

        return "Knowledge Transfer Initiated Successfully from "
                + fromEmployee.getEmployeeName()
                + " → "
                + toEmployee.getEmployeeName();
    }

    // ── COMPLETE KT ──────────────────────────────────────────────────────────

    public String completeKT(String employeeId) {

        List<KnowledgeTransfer> ktList = knowledgeTransferRepository.findAllByEmployeeId(employeeId);

        if (ktList.isEmpty()) {
            throw new RuntimeException("KT record not found for employee: " + employeeId);
        }

        KnowledgeTransfer kt = ktList.get(ktList.size() - 1);
        kt.setStatus("COMPLETED");
        knowledgeTransferRepository.save(kt);

        Employee employee = employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        emailService.sendKTMail(
                employee.getPersonalEmail(),
                employee.getEmployeeName(),
                kt.getDocumentUrl()
        );

        return "KT Completed Successfully — transferred to " + kt.getTransferToEmployeeName();
    }

    // ── INITIATE DEBOARDING ──────────────────────────────────────────────────

    public String initiateDeboarding(DeboardingRequest request) {

        Employee employee = employeeRepository
                .findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        List<AssetAssignment> assignedAssets = assetAssignmentRepository
                .findByEmployeeId(employee.getEmployeeId())
                .stream()
                .filter(asset -> "ASSIGNED".equalsIgnoreCase(asset.getStatus()))
                .toList();

        if (!assignedAssets.isEmpty()) {
            throw new RuntimeException("Employee still has assigned assets");
        }

        List<KnowledgeTransfer> ktList = knowledgeTransferRepository
                .findAllByEmployeeId(employee.getEmployeeId());

        if (ktList.isEmpty()) {
            throw new RuntimeException("Knowledge Transfer pending");
        }

        KnowledgeTransfer latestKT = ktList.get(ktList.size() - 1);
        if (!"COMPLETED".equalsIgnoreCase(latestKT.getStatus())) {
            throw new RuntimeException("Knowledge Transfer not completed");
        }

        DeboardingRecord record = new DeboardingRecord();
        record.setEmployeeId(employee.getEmployeeId());
        record.setEmployeeName(employee.getEmployeeName());
        record.setDepartment(employee.getDepartment());
        record.setReason(request.getReason());
        record.setLastWorkingDate(LocalDate.parse(request.getLastWorkingDate()));
        record.setRemarks(request.getRemarks());
        record.setInitiatedBy(request.getInitiatedBy());
        record.setInitiatedDate(LocalDate.now());
        record.setStatus("COMPLETED");

        deboardingRepository.save(record);

        employee.setStatus("INACTIVE");
        employeeRepository.save(employee);

        return "Deboarding Completed Successfully";
    }

    // ── BASIC QUERIES ────────────────────────────────────────────────────────

    public List<DeboardingRecord> getAllDeboarding() {
        return deboardingRepository.findAll();
    }

    public long getCompletedDeboardingCount() {
        return deboardingRepository.countByStatus("COMPLETED");
    }

    public List<KnowledgeTransfer> getAllKnowledgeTransfers() {
        return knowledgeTransferRepository.findAll();
    }

    public long getTotalKTCount() {
        return knowledgeTransferRepository.count();
    }

    public long getTotalDeboardingCount() {
        return deboardingRepository.count();
    }

    public long getTotalAlumniCount() {
        return deboardingRepository.countByStatus("COMPLETED");
    }

    // ── ATTRITION LOGIC ──────────────────────────────────────────────────────

    public double getAttritionRate() {
        long totalEmployees = employeeRepository.count();

        if (totalEmployees == 0) {
            return 0.0;
        }

        LocalDate now = LocalDate.now();
        Date startDate = toDate(now.withDayOfMonth(1));
        Date endDate = toDate(now.withDayOfMonth(now.lengthOfMonth()));

        long completedDeboardingsThisMonth =
                deboardingRepository.countByStatusAndLastWorkingDateBetween(
                        "COMPLETED",
                        startDate,
                        endDate
                );

        double rate = ((double) completedDeboardingsThisMonth / totalEmployees) * 100;
        return round(rate);
    }

    public double getLastMonthAttritionRate() {
        long totalEmployees = employeeRepository.count();

        if (totalEmployees == 0) {
            return 0.0;
        }

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        Date startDate = toDate(lastMonth.withDayOfMonth(1));
        Date endDate = toDate(lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));

        long completedDeboardingsLastMonth =
                deboardingRepository.countByStatusAndLastWorkingDateBetween(
                        "COMPLETED",
                        startDate,
                        endDate
                );

        double rate = ((double) completedDeboardingsLastMonth / totalEmployees) * 100;
        return round(rate);
    }

    public double getAttritionRateChange() {
        return round(getAttritionRate() - getLastMonthAttritionRate());
    }

    public AttritionRateResponse getAttritionSummary() {
        double currentMonthRate = getAttritionRate();
        double lastMonthRate = getLastMonthAttritionRate();
        double change = round(currentMonthRate - lastMonthRate);

        return new AttritionRateResponse(currentMonthRate, lastMonthRate, change);
    }

    // ── HELPER METHODS ───────────────────────────────────────────────────────

    private Date toDate(LocalDate localDate) {
        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

