package com.Vendor.service;

import com.Vendor.model.OfferRequestDTO;
import com.Vendor.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import freemarker.template.Template;
import freemarker.template.Configuration;

import java.io.StringWriter;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final EmailService emailService;
    private final Configuration config;
    private final S3Service s3Service;
    private final UrlShortenerService urlShortenerService;
    private  final OfferRepository offerRepository;

    public String sendOffer(OfferRequestDTO dto) {

        try {

            String html = generateHtml(dto);

            byte[] pdf = generatePdf(html);

            String fileName = "offers/Offer_Letter_"
                    + dto.getName() + "_"
                    + System.currentTimeMillis() + ".pdf";

            s3Service.uploadFile(pdf, fileName);

            String pdfUrl =
                    s3Service.generatePresignedUrl(fileName);

            // GENERATE TOKEN
            String token = UUID.randomUUID().toString();

            // SAVE DATA
            dto.setOfferPdfUrl(pdfUrl);
            dto.setOfferToken(token);
            dto.setOfferStatus("SENT");
            dto.setCreatedAt(LocalDateTime.now());

            offerRepository.save(dto);

            // FRONTEND URL
            String frontendUrl =
                    "http://localhost:4200/offer-response/" + token;

            // SEND EMAIL
            emailService.sendOfferEmail(
                    dto.getEmail(),
                    dto.getName(),
                    frontendUrl
            );

            return "Offer sent successfully";

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error sending offer: "
                            + e.getMessage()
            );
        }
    }
    private String generateHtml(OfferRequestDTO dto) throws Exception {

        Map<String, Object> model = new HashMap<>();

        model.put("name", dto.getName());
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

        Template template = config.getTemplate("offer-letter.ftl");

        StringWriter writer = new StringWriter();
        template.process(model, writer);

        return writer.toString();
    }

    // ✅ HTML → PDF
    private byte[] generatePdf(String html) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(out);
        builder.run();

        return out.toByteArray();
    }

}
