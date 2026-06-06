package com.Vendor.controller;

import com.Vendor.dto.DeboardingRequest;
import com.Vendor.dto.KnowledgeTransferRequest;
import com.Vendor.service.DeboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") @RequiredArgsConstructor
public class DeboardingController {
    private final DeboardingService deboardingService;
    @PostMapping("/ktinitiate")
    public ResponseEntity<?> initiateKT(

            @RequestBody
            KnowledgeTransferRequest request
    ) {

        return ResponseEntity.ok(java.util.Map.of("message",

                        deboardingService
                                .initiateKT(
                                        request
                                )
                )
        );
    }

    @PostMapping("/ktcomplete")

    public ResponseEntity<?> completeKT(

            @RequestParam
            String employeeId
    ) {

        return ResponseEntity.ok(

                java.util.Map.of(

                        "message",

                        deboardingService
                                .completeKT(
                                        employeeId
                                )
                )
        );
    }
    @PostMapping("/Deboardinitiate")

    public ResponseEntity<?> initiateDeboarding(

            @RequestBody
            DeboardingRequest request
    ) {

        return ResponseEntity.ok(

                java.util.Map.of(

                        "message",

                        deboardingService
                                .initiateDeboarding(
                                        request
                                )
                )
        );
    }

    @GetMapping("/Deboardingall")

    public ResponseEntity<?> getAllDeboarding(){

        return ResponseEntity.ok(

                deboardingService.getAllDeboarding()
        );
    }



}
