package com.Vendor.service;

import com.Vendor.dto.OCRResult;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

@Service
public class OCRService {

    // ================= MULTIPART FILE OCR =================

    public OCRResult scanDocument(
            MultipartFile file
    ) {

        try {

            String contentType =
                    file.getContentType();

            if (contentType == null)
                throw new RuntimeException(
                        "Invalid file"
                );

            ITesseract tesseract =
                    new Tesseract();

            tesseract.setDatapath(
                    "C:/Program Files/Tesseract-OCR/tessdata"
            );

            tesseract.setLanguage(
                    "eng+hin+tam"
            );

            String ocrText = "";

            // IMAGE

            if (contentType.startsWith("image/")) {

                String ext =
                        file.getOriginalFilename()
                                .substring(
                                        file.getOriginalFilename()
                                                .lastIndexOf(".")
                                );

                File temp =
                        File.createTempFile(
                                "ocr_" + UUID.randomUUID(),
                                ext
                        );

                file.transferTo(temp);

                ocrText =
                        tesseract.doOCR(temp);
            }

            // PDF

            else if (
                    "application/pdf"
                            .equals(contentType)
            ) {

                File tempPdf =
                        File.createTempFile(
                                "ocr_pdf_",
                                ".pdf"
                        );

                file.transferTo(tempPdf);

                PDDocument doc =
                        PDDocument.load(tempPdf);

                PDFRenderer renderer =
                        new PDFRenderer(doc);

                StringBuilder sb =
                        new StringBuilder();

                for (int i = 0;
                     i < doc.getNumberOfPages();
                     i++) {

                    BufferedImage image =

                            renderer.renderImageWithDPI(
                                    i,
                                    300
                            );

                    sb.append(

                            tesseract.doOCR(image)

                    ).append("\n");
                }

                doc.close();

                ocrText = sb.toString();
            }

            else {

                throw new RuntimeException(
                        "Unsupported file type"
                );
            }

            return buildResult(
                    ocrText
            );

        } catch (Exception e) {

            throw new RuntimeException(

                    "OCR Failed: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // ================= FILE OCR =================

    public OCRResult scanFile(
            File file
    ) {

        try {

            ITesseract tesseract =
                    new Tesseract();

            tesseract.setDatapath(
                    "C:/Program Files/Tesseract-OCR/tessdata"
            );

            tesseract.setLanguage(
                    "eng+hin+tam"
            );

            String ocrText = "";

            // PDF

            if (file.getName()
                    .toLowerCase()
                    .endsWith(".pdf")) {

                PDDocument doc =
                        PDDocument.load(file);

                PDFRenderer renderer =
                        new PDFRenderer(doc);

                StringBuilder sb =
                        new StringBuilder();

                for (int i = 0;
                     i < doc.getNumberOfPages();
                     i++) {

                    BufferedImage image =

                            renderer.renderImageWithDPI(
                                    i,
                                    300
                            );

                    sb.append(

                            tesseract.doOCR(image)

                    ).append("\n");
                }

                doc.close();

                ocrText = sb.toString();
            }

            // IMAGE

            else {

                ocrText =
                        tesseract.doOCR(file);
            }

            return buildResult(
                    ocrText
            );

        } catch (Exception e) {

            throw new RuntimeException(

                    "OCR Failed : "
                            + e.getMessage()
            );
        }
    }

    // ================= RESULT BUILDER =================

    private OCRResult buildResult(
            String text
    ) {

        text = text.toUpperCase();

        OCRResult result =
                new OCRResult();

        result.setRawText(text);

        // PAN

        if (text.contains("INCOME TAX")) {

            result.setDocumentType("PAN");

            result.setName(
                    extractName(text)
            );

            result.setPan(
                    extractPan(text)
            );

            result.setStatus(

                    result.getPan()
                            .equals("NOT FOUND")

                            ? "REJECTED"

                            : "VERIFIED"
            );
        }

        // AADHAAR

        else if (

                text.contains(
                        "GOVERNMENT OF INDIA"
                )

                        ||

                        text.matches(
                                ".*\\d{4}\\s?\\d{4}\\s?\\d{4}.*"
                        )
        ) {

            result.setDocumentType(
                    "AADHAAR"
            );

            result.setName(
                    extractName(text)
            );

            result.setAadhaar(
                    extractAadhaar(text)
            );

            result.setStatus(

                    result.getAadhaar()
                            .equals("NOT FOUND")

                            ? "REJECTED"

                            : "VERIFIED"
            );
        }

        // BANK

        else {

            result.setDocumentType(
                    "BANK"
            );

            result.setName(
                    extractName(text)
            );

            result.setAccountNumber(
                    extractAccount(text)
            );

            result.setStatus(

                    result.getAccountNumber()
                            .equals("NOT FOUND")

                            ? "REVIEW"

                            : "VERIFIED"
            );
        }

        return result;
    }

    private String extractPan(
            String text
    ) {

        Matcher m =

                Pattern.compile(
                        "[A-Z]{5}[0-9]{4}[A-Z]"
                ).matcher(text);

        return m.find()
                ? m.group()
                : "NOT FOUND";
    }

    private String extractAadhaar(
            String text
    ) {

        Matcher m =

                Pattern.compile(
                        "\\d{4}\\s?\\d{4}\\s?\\d{4}"
                ).matcher(text);

        return m.find()

                ? m.group()
                .replaceAll("\\s", "")

                : "NOT FOUND";
    }

    private String extractAccount(
            String text
    ) {

        Matcher m =

                Pattern.compile(
                        "\\d{9,18}"
                ).matcher(text);

        return m.find()
                ? m.group()
                : "NOT FOUND";
    }

    private String extractName(
            String text
    ) {

        String[] lines =
                text.split("\\n");

        for (String line : lines) {

            line = line.trim();

            if (line.length() < 4)
                continue;

            if (

                    line.contains("INCOME")

                            ||

                            line.contains("GOVERNMENT")

                            ||

                            line.contains("INDIA")
            )

                continue;

            if (line.matches("[A-Z ]+")) {

                return line;
            }
        }

        return "NOT FOUND";
    }
}