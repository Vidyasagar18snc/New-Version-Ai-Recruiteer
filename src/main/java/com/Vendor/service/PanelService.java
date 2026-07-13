package com.Vendor.service;

import com.Vendor.model.Panel;
import com.Vendor.repository.CandidateRepository;
import com.Vendor.repository.PanelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PanelService {

    private final PanelRepository panelRepository;
    private final CandidateRepository candidateRepository;

    public List<Panel> assignPanel(String role) {
        List<Panel> matchedPanels = panelRepository.findAll()
                .stream()
                .filter(panel ->
                        role.toLowerCase().contains(panel.getRole().toLowerCase())
                                || panel.getRole().toLowerCase().contains(role.toLowerCase())
                )
                .filter(Panel::isAvailable)
                .sorted(
                        Comparator.comparingLong(
                                this::getAssignedCandidateCount
                        )
                )
                .toList();

        if (matchedPanels.isEmpty()) {

            System.out.println(
                    "No matching panels found"
            );

            return Collections.emptyList();
        }

        System.out.println(
                "========== PANEL LOAD =========="
        );

        matchedPanels.forEach(panel -> {

            long count =
                    getAssignedCandidateCount(panel);

            System.out.println(
                    panel.getName()
                            + " -> "
                            + count
            );
        });

        System.out.println(
                "Preferred Panel : "
                        + matchedPanels.get(0).getName()
        );

        return matchedPanels;
    }

    private long getAssignedCandidateCount(Panel panel) {

        return candidateRepository.countByAssignedPanelId(
                panel.getId()
        );
    }
    public Panel addPanel(Panel panel) {

        System.out.println("Job Title: " + panel.getJobTitle());

        return panelRepository.save(panel);
    }
}