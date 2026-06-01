package com.Vendor.service;

import com.Vendor.dto.AssetAssignment;
import com.Vendor.dto.DeboardingRequest;
import com.Vendor.dto.KnowledgeTransferRequest;
import com.Vendor.model.DeboardingRecord;
import com.Vendor.model.Employee;
import com.Vendor.model.KnowledgeTransfer;
import com.Vendor.repository.AssetAssignmentRepository;
import com.Vendor.repository.DeboardingRepository;
import com.Vendor.repository.EmployeeRepository;
import com.Vendor.repository.KnowledgeTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service @RequiredArgsConstructor
public class DeboardingService {
    private final EmployeeRepository employeeRepository;
    private final DeboardingRepository deboardingRepository;
    private final KnowledgeTransferRepository knowledgeTransferRepository;
    private final AssetAssignmentRepository assetAssignmentRepository;
    private final EmailService emailService;

    public String initiateKT(


            KnowledgeTransferRequest request
    ) {

        Employee employee =

                employeeRepository

                        .findByEmployeeId(

                                request.getEmployeeId()
                        )

                        .orElseThrow(() ->

                                new RuntimeException(

                                        "Employee not found"
                                )
                        );

        KnowledgeTransfer kt =
                new KnowledgeTransfer();

        kt.setEmployeeId(
                employee.getEmployeeId()
        );

        kt.setEmployeeName(
                employee.getEmployeeName()
        );

        kt.setDepartment(
                employee.getDepartment()
        );

        kt.setProjectName(
                request.getProjectName()
        );

        kt.setTaskDetails(
                request.getTaskDetails()
        );

        kt.setDocumentationLink(
                request.getDocumentationLink()
        );

        kt.setCredentialsShared(
                request.getCredentialsShared()
        );

        kt.setTransferredTo(
                request.getTransferredTo()
        );

        kt.setRemarks(
                request.getRemarks()
        );

        kt.setStatus(
                "PENDING"
        );

        kt.setKtDate(
                LocalDate.now()
        );

        knowledgeTransferRepository.save(
                kt
        );
        // SEND MAIL TO EMPLOYEE

        emailService.sendKTMail(
                employee.getOfficialEmail(),
                employee.getEmployeeName(),
                kt.getProjectName(),
                kt.getTransferredTo()
        );

        Employee transferEmployee = employeeRepository.findByEmployeeName(kt.getTransferredTo())

                        .orElse(null);

        if(transferEmployee != null){

            emailService.sendKTMail(

                    transferEmployee.getOfficialEmail(),

                    employee.getEmployeeName(),

                    kt.getProjectName(),

                    kt.getTransferredTo()
            );
        }

// SEND MAIL TO HR

        List<Employee> hrEmployees =

                employeeRepository

                        .findByDepartment(
                                "HR"
                        );

        for(Employee hr : hrEmployees){

            emailService.sendKTMail(

                    hr.getOfficialEmail(),

                    employee.getEmployeeName(),

                    kt.getProjectName(),

                    kt.getTransferredTo()
            );
        }

        return "Knowledge Transfer Initiated";
    }
    public String completeKT(

            String employeeId
    ) {

        KnowledgeTransfer kt =

                knowledgeTransferRepository

                        .findTopByEmployeeIdOrderByKtDateDesc(

                                employeeId
                        )

                        .orElseThrow(() ->

                                new RuntimeException(

                                        "KT record not found"
                                )
                        );

        kt.setStatus(
                "COMPLETED"
        );

        knowledgeTransferRepository.save(
                kt
        );

        return "KT Completed Successfully";
    }
    public String initiateDeboarding(

            DeboardingRequest request
    ) {

        Employee employee =

                employeeRepository

                        .findByEmployeeId(

                                request.getEmployeeId()
                        )

                        .orElseThrow(() ->

                                new RuntimeException(

                                        "Employee not found"
                                )
                        );

        // CHECK ASSETS

        List<AssetAssignment> assignedAssets =

                assetAssignmentRepository

                        .findByEmployeeId(

                                employee.getEmployeeId()
                        )

                        .stream()

                        .filter(asset ->

                                asset.getStatus()
                                        .equals("ASSIGNED")
                        )

                        .toList();

        if(

                !assignedAssets.isEmpty()
        ){

            throw new RuntimeException(

                    "Employee still has assigned assets"
            );
        }

        // CHECK KT

        List<KnowledgeTransfer> ktList =

                knowledgeTransferRepository

                        .findByEmployeeId(

                                employee.getEmployeeId()
                        );

        if(

                ktList.isEmpty()
        ){

            throw new RuntimeException(

                    "Knowledge Transfer pending"
            );
        }

        KnowledgeTransfer latestKT =

                ktList.get(
                        ktList.size()-1
                );

        if(

                !latestKT.getStatus()
                        .equals("COMPLETED")
        ){

            throw new RuntimeException(

                    "Knowledge Transfer not completed"
            );
        }

        // CREATE RECORD

        DeboardingRecord record =
                new DeboardingRecord();

        record.setEmployeeId(
                employee.getEmployeeId()
        );

        record.setEmployeeName(
                employee.getEmployeeName()
        );

        record.setDepartment(
                employee.getDepartment()
        );

        record.setReason(
                request.getReason()
        );

        record.setLastWorkingDate(

                LocalDate.parse(
                        request.getLastWorkingDate()
                )
        );

        record.setRemarks(
                request.getRemarks()
        );

        record.setInitiatedBy(
                request.getInitiatedBy()
        );

        record.setInitiatedDate(
                LocalDate.now()
        );

        record.setStatus(
                "COMPLETED"
        );

        deboardingRepository.save(
                record
        );

        // DEACTIVATE EMPLOYEE

        employee.setStatus(
                "INACTIVE"
        );

        employeeRepository.save(
                employee
        );

        return "Deboarding Completed Successfully";
    }
    public List<DeboardingRecord> getAllDeboarding(){

        return deboardingRepository.findAll();
    }
}
