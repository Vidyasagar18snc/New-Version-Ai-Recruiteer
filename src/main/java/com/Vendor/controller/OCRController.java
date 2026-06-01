package com.Vendor.controller;
import com.Vendor.dto.OCRResult;
import com.Vendor.service.OCRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ocr")
public class OCRController {

    @Autowired
    private OCRService ocrService;

    @PostMapping("/upload")
    public OCRResult upload(@RequestParam("file") MultipartFile file) {

        return ocrService.scanDocument(file);
    }
}