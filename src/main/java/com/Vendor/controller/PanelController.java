package com.Vendor.controller;

import com.Vendor.dto.LoginRequest;
import com.Vendor.model.Candidate;
import com.Vendor.model.Panel;
import com.Vendor.repository.CandidateRepository;
import com.Vendor.repository.PanelRepository;
import com.Vendor.service.AvailabilityService;
import com.Vendor.service.PanelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PanelController {

    private final PanelService panelService;
    private final AvailabilityService availabilityService;
    private final CandidateRepository candidateRepository;
    private final PanelRepository panelRepository;

    @PostMapping("/add")
    public Panel addPanel(@RequestBody Panel panel) {

        return panelService.addPanel(panel);
    }

    @GetMapping("/by-role")
    public List<Panel> getPanels(
            @RequestParam String role
    ) {

        return panelService.assignPanel(role);
    }
    @GetMapping("/free-slots")
    public List<String> getSlots(
            @RequestParam String email,
            @RequestParam String date
    ) throws Exception {

        return availabilityService.getFreeSlots(
                email,
                date
        );
    }
    @GetMapping("/shortlisted")
    public List<Candidate> getShortlistedCandidates() {

        return candidateRepository
                .findByStatusIgnoreCase(
                        "Shortlisted"
                );
    }
    @GetMapping("/assigned-panel/{candidateId}")
    public ResponseEntity<Panel> getAssignedPanel(
            @PathVariable String candidateId
    ){

        Candidate candidate=
                candidateRepository
                        .findById(candidateId)
                        .orElseThrow(()->
                                new RuntimeException(
                                        "Candidate not found"
                                ));

        if(candidate.getAssignedPanelId()==null){

            return ResponseEntity.notFound().build();
        }

        Panel panel=
                panelRepository
                        .findById(
                                candidate.getAssignedPanelId()
                        )
                        .orElseThrow(()->
                                new RuntimeException(
                                        "Panel not found"
                                ));

        return ResponseEntity.ok(panel);
    }
    @PostMapping("/panel/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ){

        System.out.println(
                "Login Email : "
                        +request.getEmail()
        );

        System.out.println(
                "Login Password : "
                        +request.getPassword()
        );

        Optional<Panel> optionalPanel=

                panelRepository
                        .findByEmailAndPassword(

                                request.getEmail(),

                                request.getPassword()
                        );

        if(optionalPanel.isEmpty()){

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Invalid Credentials"
                    );
        }

        Panel panel=
                optionalPanel.get();

        return ResponseEntity.ok(panel);
    }
    @GetMapping("/my-candidates/{panelId}")

    public ResponseEntity<List<Candidate>>
    getMyCandidates(

            @PathVariable String panelId
    ){

        List<Candidate> candidates=

                candidateRepository
                        .findByAssignedPanelId(
                                panelId
                        );

        return ResponseEntity.ok(
                candidates
        );
    }
}
