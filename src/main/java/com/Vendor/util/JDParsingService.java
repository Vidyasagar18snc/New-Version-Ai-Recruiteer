package com.Vendor.util;

import com.Vendor.model.Job;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JDParsingService {

    // MAIN METHOD → returns Job (NOT saved)
    public Job processJD(MultipartFile file) {

        String jdText = extractText(file);

        String title = extractTitle(jdText);
        int experience = extractExperience(jdText);
        List<String> skills = extractSkills(jdText);
        String location = extractLocation(jdText);
        String budget = extractBudget(jdText);

        return Job.builder()
                .title(title)
                .experience(experience)
                .skills(skills)
                .location(location)
                . budget(budget)
                .description(jdText)
                .build();
    }

    //  TEXT EXTRACTION
    private String extractText(MultipartFile file) {
        try {
            Tika tika = new Tika();
            return tika.parseToString(file.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JD file", e);
        }
    }

    // ✅ TITLE EXTRACTION
    private String extractTitle(String text) {

        String[] lines = text.split("\\r?\\n");

        for (String line : lines) {
            line = line.trim();

            if (line.length() > 5 && line.length() < 80 &&
                    !line.toLowerCase().contains("job description") &&
                    !line.toLowerCase().contains("responsibilities") &&
                    !line.toLowerCase().contains("skills")) {

                return line;
            }
        }

        return "Not specified";
    }

    // ✅ EXPERIENCE EXTRACTION
    private int extractExperience(String text) {

        Pattern pattern = Pattern.compile(
                "(\\d+(\\.\\d+)?)(\\+)?\\s*(year|years|yr|yrs)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(text);

        double maxExp = 0;

        while (matcher.find()) {

            String value = matcher.group(1); // only number part
            double exp = Double.parseDouble(value);

            if (exp > maxExp) {
                maxExp = exp;
            }
        }

        return (int) Math.floor(maxExp);
    }
    //  SKILLS EXTRACTION (Dynamic)
    private List<String> extractSkills(String text) {

        List<String> skills = new ArrayList<>();

        String[] lines = text.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].toLowerCase();

            if (line.contains("skills") ||
                    line.contains("technologies") ||
                    line.contains("requirements")) {

                for (int j = i + 1; j < Math.min(i + 6, lines.length); j++) {

                    String skillLine = lines[j];

                    if (skillLine.contains(",") || skillLine.contains("-") || skillLine.contains("•")) {

                        String[] parts = skillLine.split("[,•-]");

                        for (String part : parts) {
                            String skill = part.trim();

                            if (skill.length() > 2 && skill.length() < 30) {
                                skills.add(skill);
                            }
                        }
                    }
                }
            }
        }

        return skills.stream().distinct().collect(Collectors.toList());
    }

    private String extractLocation(String text) {

        Pattern pattern = Pattern.compile(
                "location[:\\s]*([A-Za-z ,]+)",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return "Not specified";
    }
    private String extractBudget(String text) {

        Pattern[] patterns = {
                Pattern.compile("budget\\s*[:\\-]?\\s*(.+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("salary\\s*[:\\-]?\\s*(.+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("ctc\\s*[:\\-]?\\s*(.+)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(\\d+\\s*[-–]\\s*\\d+\\s*(LPA|Lakhs|Lakh))", Pattern.CASE_INSENSITIVE),
                Pattern.compile("(₹\\s*\\d+\\s*[-–]\\s*₹?\\s*\\d+\\s*(LPA|Lakhs|Lakh)?)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        return "Not specified";
    }
}