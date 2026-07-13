package com.Vendor.service;

import com.Vendor.dto.DocumentInfo;
import com.Vendor.dto.OCRResult;
import com.Vendor.model.Onboarding;
import com.Vendor.repository.EmployeeDocumentRepository;
import com.Vendor.repository.OnboardingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final S3Service s3Service;
    private final EmployeeDocumentRepository documentRepository;
    private final OnboardingRepository onboardingRepository;
    private final EmailService emailService;
    private final OCRService ocrService;
    private final EmployeeService employeeService;

    public void uploadDocuments(
            MultipartFile[] files,
            String candidateId,
            String candidateName
    ) throws Exception {

        Onboarding onboarding = onboardingRepository
                .findByCandidateId(candidateId)
                .orElseThrow(() ->
                        new RuntimeException("Onboarding not found")
                );

        List<DocumentInfo> documentList = new ArrayList<>();

        for (MultipartFile file : files) {

            // Validate File Type
            String contentType = file.getContentType();

            if (contentType == null ||
                    !(contentType.equals("application/pdf")
                            || contentType.startsWith("image/"))) {

                throw new RuntimeException(
                        "Only PDF, JPG, JPEG and PNG files are allowed"
                );
            }

            // Generate S3 File Name
            String fileName = "onboarding/"
                    + candidateId
                    + "/"
                    + System.currentTimeMillis()
                    + "_"
                    + file.getOriginalFilename();

            // Upload To S3
            s3Service.uploadFile(
                    file.getBytes(),
                    fileName,
                    contentType
            );

            OCRResult result;

            try {

                result = ocrService.scanDocument(file);

            } catch (Exception ex) {

                System.out.println(
                        "OCR Failed : " + ex.getMessage()
                );

                result = new OCRResult();

                result.setDocumentType("UNKNOWN");
                result.setStatus("PENDING");
            }

            // Create Document Info
            DocumentInfo document = new DocumentInfo();

            document.setDocumentType(
                    result.getDocumentType()
            );

            document.setFileName(
                    file.getOriginalFilename()
            );

            document.setS3Key(
                    fileName
            );

            document.setVerificationStatus(
                    "PENDING"
            );

            document.setUploadedAt(
                    LocalDateTime.now()
            );

            documentList.add(document);
        }

        // Save Documents
        onboarding.setDocuments(documentList);
        onboarding.setDocumentsSubmitted(true);

        onboardingRepository.save(onboarding);

        // Notify HR
        emailService.sendHrDocumentUploadedMail(
                onboarding.getHrEmail(),
                candidateName,
                "Multiple Documents"
        );
    }

    public List<Onboarding> getAllDocuments() {

        List<Onboarding> onboardings = onboardingRepository.findAll();

        onboardings.forEach(onboarding -> {

            if (onboarding.getDocuments() != null) {

                // Generate Fresh URLs
                onboarding.getDocuments().forEach(document -> {

                    String freshUrl = s3Service.generatePresignedUrl(
                            document.getS3Key()
                    );

                    document.setFileUrl(freshUrl);
                });

                boolean allVerified = onboarding.getDocuments()
                        .stream()
                        .allMatch(document ->
                                "VERIFIED".equalsIgnoreCase(
                                        document.getVerificationStatus()
                                )
                        );

                boolean anyRejected = onboarding.getDocuments()
                        .stream()
                        .anyMatch(document ->
                                "REJECTED".equalsIgnoreCase(
                                        document.getVerificationStatus()
                                )
                        );

                // Update Onboarding Status
                if (allVerified) {
                    onboarding.setOnboardingStatus("VERIFIED");
                } else if (anyRejected) {
                    onboarding.setOnboardingStatus("REJECTED");
                } else {
                    onboarding.setOnboardingStatus("PENDING");
                }
            }
        });

        return onboardings;
    }

    public void verifyAllDocuments(String candidateId) {

        Onboarding onboarding =
                onboardingRepository
                        .findByCandidateId(candidateId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Onboarding not found"
                                )
                        );

        try {

            boolean allVerified = true;

            for (DocumentInfo document : onboarding.getDocuments()) {

                String freshUrl =
                        s3Service.generatePresignedUrl(
                                document.getS3Key()
                        );

                URL url =
                        new URL(freshUrl);

                InputStream inputStream =
                        url.openStream();

                File tempFile =
                        File.createTempFile(
                                "ocr_",
                                document.getFileName()
                        );

                Files.copy(
                        inputStream,
                        tempFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                OCRResult result =
                        ocrService.scanFile(tempFile);

                if (
                        "VERIFIED".equalsIgnoreCase(
                                result.getStatus()
                        )
                ) {

                    document.setVerificationStatus(
                            "VERIFIED"
                    );

                    document.setVerificationRemarks(
                            "OCR verification successful"
                    );
                } else {

                    allVerified = false;

                    document.setVerificationStatus(
                            "REJECTED"
                    );
                    document.setVerificationRemarks(
                            "Invalid or unclear document"
                    );
                }

                document.setVerifiedAt(
                        LocalDateTime.now()
                );
            }

            if (allVerified) {

                onboarding.setHrVerified(true);

                onboarding.setOnboardingStatus(
                        "DOCUMENTS_VERIFIED"
                );

                onboardingRepository.save(onboarding);

                emailService.sendAllDocumentsVerifiedMail(
                        onboarding.getEmail(),
                        onboarding.getCandidateName()
                );

                employeeService.createEmployeeAfterDelay(
                        onboarding
                );

            } else {

                onboarding.setHrVerified(false);

                onboarding.setOnboardingStatus(
                        "REJECTED"
                );

                onboardingRepository.save(onboarding);

                emailService.sendDocumentRejectedMail(
                        onboarding.getEmail(),
                        onboarding.getCandidateName(),
                        "One Or More Documents",
                        "Some uploaded documents failed OCR verification"
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Verification failed : "
                            + e.getMessage()
            );
        }
    }
}