package com.Vendor.service;

import com.Vendor.dto.OCRResult;
import org.springframework.beans.factory.annotation.Value;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRService {

    @Value("${tesseract.data-path}")
    private String tessDataPath;

    public OCRResult scanDocument(MultipartFile file) {
        try {
            String contentType = file.getContentType();

            if (contentType == null) {
                throw new RuntimeException("Invalid file");
            }

            ITesseract tesseract = getTesseract();
            String ocrText;

            if (contentType.startsWith("image/")) {

                String originalName = file.getOriginalFilename();

                if (originalName == null || !originalName.contains(".")) {
                    throw new RuntimeException("Invalid file name");
                }

                String ext = originalName.substring(originalName.lastIndexOf("."));

                File tempFile = File.createTempFile(
                        "ocr_" + UUID.randomUUID(),
                        ext
                );

                file.transferTo(tempFile);
                ocrText = tesseract.doOCR(tempFile);

            } else if ("application/pdf".equals(contentType)) {

                File tempPdf = File.createTempFile("ocr_pdf_", ".pdf");
                file.transferTo(tempPdf);

                try (PDDocument document = PDDocument.load(tempPdf)) {

                    PDFRenderer renderer = new PDFRenderer(document);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < document.getNumberOfPages(); i++) {
                        BufferedImage image = renderer.renderImageWithDPI(i, 300);
                        sb.append(tesseract.doOCR(image)).append("\n");
                    }

                    ocrText = sb.toString();
                }

            } else {
                throw new RuntimeException("Unsupported file type");
            }

            return buildResult(ocrText);

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "OCR Failed : "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage(),
                    e
            );
        }
    }

    public OCRResult scanFile(File file) {
        try {
            ITesseract tesseract = getTesseract();
            String ocrText;

            if (file.getName().toLowerCase().endsWith(".pdf")) {

                try (PDDocument document = PDDocument.load(file)) {

                    PDFRenderer renderer = new PDFRenderer(document);
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < document.getNumberOfPages(); i++) {
                        BufferedImage image = renderer.renderImageWithDPI(i, 300);
                        sb.append(tesseract.doOCR(image)).append("\n");
                    }

                    ocrText = sb.toString();
                }

            } else {
                ocrText = tesseract.doOCR(file);
            }

            return buildResult(ocrText);

        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(
                    "OCR Failed : "
                            + e.getClass().getSimpleName()
                            + " - "
                            + e.getMessage()
            );
        }
    }

    private OCRResult buildResult(String text) {
        text = text.toUpperCase();

        OCRResult result = new OCRResult();
        result.setRawText(text);

        if (text.contains("INCOME TAX")) {

            result.setDocumentType("PAN");
            result.setName(extractName(text));
            result.setPan(extractPan(text));

            result.setStatus(
                    "NOT FOUND".equals(result.getPan())
                            ? "REJECTED"
                            : "VERIFIED"
            );

        } else if (
                text.contains("GOVERNMENT OF INDIA")
                        || text.matches(".*\\d{4}\\s?\\d{4}\\s?\\d{4}.*")
        ) {

            result.setDocumentType("AADHAAR");
            result.setName(extractName(text));
            result.setAadhaar(extractAadhaar(text));

            result.setStatus(
                    "NOT FOUND".equals(result.getAadhaar())
                            ? "REJECTED"
                            : "VERIFIED"
            );

        } else {

            result.setDocumentType("BANK");
            result.setName(extractName(text));
            result.setAccountNumber(extractAccount(text));

            result.setStatus(
                    "NOT FOUND".equals(result.getAccountNumber())
                            ? "REVIEW"
                            : "VERIFIED"
            );
        }

        return result;
    }

    private String extractPan(String text) {
        Matcher matcher = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]").matcher(text);
        return matcher.find() ? matcher.group() : "NOT FOUND";
    }

    private String extractAadhaar(String text) {
        Matcher matcher = Pattern.compile("\\d{4}\\s?\\d{4}\\s?\\d{4}").matcher(text);

        return matcher.find()
                ? matcher.group().replaceAll("\\s", "")
                : "NOT FOUND";
    }

    private String extractAccount(String text) {
        Matcher matcher = Pattern.compile("\\d{9,18}").matcher(text);
        return matcher.find() ? matcher.group() : "NOT FOUND";
    }

    private String extractName(String text) {
        String[] lines = text.split("\\n");

        for (String line : lines) {
            line = line.trim();

            if (line.length() < 4) {
                continue;
            }

            if (line.contains("INCOME")
                    || line.contains("GOVERNMENT")
                    || line.contains("INDIA")) {
                continue;
            }

            if (line.matches("[A-Z ]+")) {
                return line;
            }
        }

        return "NOT FOUND";
    }

    private ITesseract getTesseract() {
        ITesseract tesseract = new Tesseract();

        File tessDir = new File(tessDataPath);

        if (!tessDir.exists()) {
            throw new RuntimeException(
                    "Tesseract data path not found : " + tessDataPath
            );
        }

        tesseract.setDatapath(tessDataPath);
        tesseract.setLanguage("eng");

        return tesseract;
    }
}