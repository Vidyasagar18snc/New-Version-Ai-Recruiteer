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
                .toList();

        if (matchedPanels.isEmpty()) {
            return Collections.emptyList();
        }
        System.out.println("========== PANEL LOAD ==========");

        matchedPanels.forEach(panel -> {
            long count = getAssignedCandidateCount(panel);

            System.out.println(
                    panel.getName() + " -> " + count
            );
        });

        Panel selectedPanel = matchedPanels.stream()
                .min(Comparator.comparingLong(this::getAssignedCandidateCount))
                .orElse(null);

        if (selectedPanel != null) {

            System.out.println(
                    "Selected Panel : " + selectedPanel.getName()
            );

            return List.of(selectedPanel);
        }

        return Collections.emptyList();
    }

    private long getAssignedCandidateCount(Panel panel) {

        return candidateRepository.countByAssignedPanelId(
                panel.getId()
        );
    }

    public Panel addPanel(Panel panel) {

        return panelRepository.save(panel);
    }
}