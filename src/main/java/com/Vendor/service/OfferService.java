package com.Vendor.service;

import com.Vendor.dto.OfferAcceptanceRateResponse;
import com.Vendor.dto.TimeToHireResponse;
import com.Vendor.model.OfferRequestDTO;
import com.Vendor.model.Onboarding;
import com.Vendor.repository.OfferRepository;
import com.Vendor.repository.OnboardingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import freemarker.template.Template;
import freemarker.template.Configuration;

import java.io.StringWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final EmailService emailService;
    private final Configuration config;
    private final S3Service s3Service;
    private final UrlShortenerService urlShortenerService;
    private final OfferRepository offerRepository;
    private final OnboardingRepository onboardingRepository;

    public String sendOffer(OfferRequestDTO dto) {
        try {
            String html = generateHtml(dto);
            byte[] pdf = generatePdf(html);

            String fileName = "offers/Offer_Letter_"
                    + dto.getName() + "_"
                    + System.currentTimeMillis() + ".pdf";

            s3Service.uploadFile(pdf, fileName, "application/pdf");
            String pdfUrl = s3Service.generatePresignedUrl(fileName);

            String token = UUID.randomUUID().toString();

            dto.setOfferPdfUrl(pdfUrl);
            dto.setOfferToken(token);
            dto.setOfferStatus("SENT");
            dto.setCreatedAt(LocalDateTime.now());

            offerRepository.save(dto);

            String frontendUrl = "http://localhost:4200/offer-response/" + token;

            emailService.sendOfferEmail(
                    dto.getEmail(),
                    dto.getName(),
                    frontendUrl,
                    pdf
            );

            return "Offer sent successfully";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error sending offer: " + e.getMessage());
        }
    }

    public String generateHtml(OfferRequestDTO dto) throws Exception {
        Map<String, Object> model = new HashMap<>();

        model.put("name", dto.getName());
        model.put("email", dto.getEmail());
        model.put("role", dto.getRole());
        model.put("salary", dto.getSalary());
        model.put("joiningDate", dto.getJoiningDate());
        model.put("companyName", dto.getCompanyName());
        model.put("companyAddress", dto.getCompanyAddress());
        model.put("address", dto.getAddress());
        model.put("location", dto.getLocation());
        model.put("date", java.time.LocalDate.now());
        model.put("department", dto.getDepartment());
        model.put("employmentType", dto.getEmploymentType());
        model.put("hrEmail", dto.getHrEmail());
        model.put("hrPhone", dto.getHrPhone());
        model.put("hrSignatoryName", dto.getHrSignatoryName());
        model.put("hrSignatoryTitle", dto.getHrSignatoryTitle());
        model.put("candidatePhone", dto.getCandidatePhone());
        model.put("reportingManager", dto.getReportingManager());
        model.put("probationPeriod", dto.getProbationPeriod());
        model.put("noticePeriod", dto.getNoticePeriod());
        model.put("offerValidity", dto.getOfferValidity());
        model.put("additionalNotes", dto.getAdditionalNotes());
        model.put("payFrequency", dto.getPayFrequency());
        model.put("currency", dto.getCurrency());

        long count = offerRepository.count();
        long sequentialNum = 125 + count;
        String formattedNum = String.format("%05d", sequentialNum);
        model.put("candidateId", "CAN-2026-" + formattedNum);
        model.put("offerRefNo", "HG/HR/2026/OL-" + formattedNum);

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dateFormatter =
                java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.ENGLISH);
        model.put("formattedDate", today.format(dateFormatter));

        String formattedJoiningDate = dto.getJoiningDate();
        try {
            java.time.LocalDate jDate = java.time.LocalDate.parse(dto.getJoiningDate());
            formattedJoiningDate = jDate.format(dateFormatter);
        } catch (Exception ignored) {}
        model.put("formattedJoiningDate", formattedJoiningDate);

        String formattedValidityDate = dto.getOfferValidity();
        try {
            java.time.LocalDate vDate = java.time.LocalDate.parse(dto.getOfferValidity());
            formattedValidityDate = vDate.format(dateFormatter);
        } catch (Exception ignored) {}
        model.put("formattedValidityDate", formattedValidityDate);

        double salaryInput = dto.getSalary();
        long basic = Math.round(salaryInput * 0.50);
        long hra = Math.round(salaryInput * 0.25);
        long special = Math.round(salaryInput * 0.125);
        long conveyance = Math.round(salaryInput * 0.02);
        long medical = Math.round(salaryInput * 0.0125);
        long performance = Math.round(salaryInput * 0.05);
        long pf = Math.round(salaryInput * 0.036);
        long gratuity = Math.round(salaryInput * 0.024);
        long totalCTC = basic + hra + special + conveyance + medical + performance + pf + gratuity;

        model.put("salaryBasic", basic);
        model.put("salaryHra", hra);
        model.put("salarySpecial", special);
        model.put("salaryConveyance", conveyance);
        model.put("salaryMedical", medical);
        model.put("salaryPerformance", performance);
        model.put("salaryPf", pf);
        model.put("salaryGratuity", gratuity);
        model.put("salaryTotalCTC", totalCTC);

        Template template = config.getTemplate("offer-letter.ftl");
        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }

    private byte[] generatePdf(String html) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }

    // ── OFFER ACCEPTANCE RATE ─────────────────────────────────────────────

    public double getOfferAcceptanceRate() {
        LocalDate now = LocalDate.now();

        Date startDate = toDate(now.withDayOfMonth(1));
        Date endDate = toEndOfDay(now.withDayOfMonth(now.lengthOfMonth()));

        long totalOffers = offerRepository.countByCreatedAtBetween(startDate, endDate);

        if (totalOffers == 0) {
            return 0.0;
        }

        long acceptedOffers = offerRepository.countByOfferStatusAndRespondedAtBetween(
                "ACCEPTED",
                startDate,
                endDate
        );

        double rate = ((double) acceptedOffers / totalOffers) * 100;
        return round(rate);
    }

    public double getLastMonthOfferAcceptanceRate() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        Date startDate = toDate(lastMonth.withDayOfMonth(1));
        Date endDate = toEndOfDay(lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));

        long totalOffers = offerRepository.countByCreatedAtBetween(startDate, endDate);

        if (totalOffers == 0) {
            return 0.0;
        }

        long acceptedOffers = offerRepository.countByOfferStatusAndRespondedAtBetween(
                "ACCEPTED",
                startDate,
                endDate
        );

        double rate = ((double) acceptedOffers / totalOffers) * 100;
        return round(rate);
    }

    public OfferAcceptanceRateResponse getOfferAcceptanceSummary() {
        double currentMonthRate = getOfferAcceptanceRate();
        double lastMonthRate = getLastMonthOfferAcceptanceRate();
        double change = round(currentMonthRate - lastMonthRate);

        return new OfferAcceptanceRateResponse(currentMonthRate, lastMonthRate, change);
    }


    public long getTimeToHire() {
        LocalDate now = LocalDate.now();

        Date startDate = toDate(now.withDayOfMonth(1));
        Date endDate = toEndOfDay(now.withDayOfMonth(now.lengthOfMonth()));

        List<OfferRequestDTO> offers = offerRepository
                .findByOfferStatusAndCreatedAtBetween("ACCEPTED", startDate, endDate);

        return calculateAverageOverallHiringDays(offers);
    }

    public long getLastMonthTimeToHire() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        Date startDate = toDate(lastMonth.withDayOfMonth(1));
        Date endDate = toEndOfDay(lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));

        List<OfferRequestDTO> offers = offerRepository
                .findByOfferStatusAndCreatedAtBetween("ACCEPTED", startDate, endDate);

        return calculateAverageOverallHiringDays(offers);
    }

    public long getTimeToHireChange() {
        return getTimeToHire() - getLastMonthTimeToHire();
    }

    public TimeToHireResponse getTimeToHireSummary() {
        long currentMonthDays = getTimeToHire();
        long lastMonthDays = getLastMonthTimeToHire();
        long change = currentMonthDays - lastMonthDays;

        return new TimeToHireResponse(currentMonthDays, lastMonthDays, change);
    }

    private long calculateAverageOverallHiringDays(List<OfferRequestDTO> offers) {
        if (offers == null || offers.isEmpty()) {
            return 0;
        }

        long totalDays = 0;
        int validCount = 0;

        for (OfferRequestDTO offer : offers) {
            if (offer.getCreatedAt() == null || offer.getEmail() == null) {
                continue;
            }

            Optional<Onboarding> onboardingOpt = onboardingRepository.findByEmail(offer.getEmail());

            if (onboardingOpt.isPresent()) {
                Onboarding onboarding = onboardingOpt.get();

                if (onboarding.getCreatedAt() != null) {
                    LocalDate offerCreatedDate = offer.getCreatedAt().toLocalDate();
                    LocalDate onboardingCreatedDate = onboarding.getCreatedAt().toLocalDate();

                    long days = ChronoUnit.DAYS.between(offerCreatedDate, onboardingCreatedDate);

                    if (days >= 0) {
                        totalDays += days;
                        validCount++;
                    }
                }
            }
        }

        if (validCount == 0) {
            return 0;
        }

        return Math.round((double) totalDays / validCount);
    }


    private Date toDate(LocalDate localDate) {
        return Date.from(
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }

    private Date toEndOfDay(LocalDate localDate) {
        return Date.from(
                localDate.atTime(23, 59, 59)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}