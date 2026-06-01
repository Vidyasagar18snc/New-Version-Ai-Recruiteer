package com.Vendor.util;

import java.time.LocalDateTime;
import java.util.List;

public class EmailMessageUtil {

    // ✅ OLD METHOD (kept for backward compatibility)
    public static String buildStatusMessage(String name, String status) {
        return buildStatusMessage(name, status, null);
    }

    // ✅ UPDATED METHOD (NOW SUPPORTS INTERVIEW LINK)
    public static String buildStatusMessage(String name, String status, String interviewLink) {

        if ("Shortlisted".equalsIgnoreCase(status)) {

            // 🔥 If interview link is available → include it
            if (interviewLink != null && !interviewLink.isEmpty()) {

                return "Hi " + name + ",\n\n"
                        + "🎉 Congratulations!\n"
                        + "You have been shortlisted for the interview.\n\n"

                        + "👉 Please join your virtual interview using the link below:\n\n"
                        + interviewLink + "\n\n"

                        + "⏰ Important Instructions:\n"
                        + "- Please join at the scheduled time\n"
                        + "- Allow camera & microphone access\n"
                        + "- Ensure stable internet connection\n\n"

                        + "We wish you all the best!\n\n"
                        + "Best Regards,\nHR Team";
            }

            // 🔥 Fallback (if link not passed)
            return "Hi " + name + ",\n\n"
                    + "🎉 Congratulations!\n"
                    + "You have been shortlisted for the interview.\n\n"
                    + "Our team will contact you soon with further details.\n\n"
                    + "Best Regards,\nHR Team";
        }

        else if ("Rejected".equalsIgnoreCase(status)) {
            return "Hi " + name + ",\n\n"
                    + "Thank you for applying.\n"
                    + "We regret to inform you that you are not selected.\n\n"
                    + "We wish you all the best for your future.\n\n"
                    + "Best Regards,\nHR Team";
        }

        else {
            return "Hi " + name + ",\n\n"
                    + "Your application is currently under review.\n"
                    + "We will update you soon.\n\n"
                    + "Best Regards,\nHR Team";
        }
    }

    // 🔥 EXISTING METHOD (UNCHANGED)
    public static String skillExpNotMatchMessage(String name) {

        return "Hi " + name + ",\n\n"
                + "Thank you for applying.\n\n"
                + "After reviewing your profile, we found that your skills and experience "
                + "do not match our current job requirements.\n\n"
                + "So, we are unable to shortlist your application at this time.\n\n"
                + "We encourage you to apply again in the future.\n\n"
                + "Best Regards,\nHR Team";
    }

    // 🔥 KEEP (optional if you still use test)
    public static String buildResultMessage(String name,
                                            int score,
                                            int total,
                                            double percentage,
                                            int rank,
                                            String status) {

        StringBuilder message = new StringBuilder();

        message.append("Hi ").append(name).append(",\n\n")
                .append("Your test has been successfully evaluated.\n\n")

                .append("📊 Test Result Summary:\n")
                .append("- Score: ").append(score).append(" / ").append(total).append("\n")
                .append("- Percentage: ").append(percentage).append("%\n")
                .append("- Rank: ").append(rank).append("\n")
                .append("- Status: ").append(status).append("\n\n");

        // ⭐ NEW BLOCK FOR TOP PERFORMER
        if ("TOP_PERFORMER".equalsIgnoreCase(status)) {

            message.append("🏆 Outstanding Performance!\n\n")
                    .append("Congratulations! You are among the top performers in this assessment.\n")
                    .append("Your performance has been exceptional, and you have demonstrated strong skills and potential.\n\n")
                    .append("We are excited to fast-track your application to the next round.\n")
                    .append("The upcoming stage will be a Face-to-Face interview.\n\n")
                    .append("Our team will share the interview details (date, time, and meeting link) shortly.\n")
                    .append("Stay tuned for further communication.\n");

        }
        // ✅ PASS (normal)
        else if ("PASS".equalsIgnoreCase(status)) {

            message.append("🎉 Congratulations! You have successfully cleared the test.\n\n")
                    .append("We are pleased to inform you that you have been shortlisted for the next round.\n")
                    .append("The next stage will be a Face-to-Face interview.\n\n")
                    .append("Our team will share the interview date, time, and venue details with you shortly.\n")
                    .append("Please keep an eye on your email for further communication.\n");

        }
        // ⚠️ REVIEW
        else if ("REVIEW".equalsIgnoreCase(status)) {

            message.append("👍 Good effort! Your performance is currently under review.\n\n")
                    .append("Our team will evaluate your results and get back to you soon with the next steps.\n");

        }
        // ❌ FAIL
        else {

            message.append("We appreciate your time and effort in completing the test.\n\n")
                    .append("After careful evaluation, we regret to inform you that you did not meet the qualifying criteria.\n\n")
                    .append("We wish you all the best in your career journey.\n");
        }

        message.append("\nBest Regards,\nHR Team");

        return message.toString();
    }
    public static String buildInterviewMessage(String name,String interviewLink,Object interviewTime){

        return "Hello "+name+",\n\n"
                +"Congratulations!\n"
                +"You have been shortlisted for the interview round.\n\n"
                +"Interview Link:\n"
                +interviewLink+"\n\n"
                +"Interview Time: "
                +interviewTime+"\n\n"
                +"Regards,\n"
                +"AI Recruitment Team";
    }

    public static String buildHRNotificationMessage(String candidateName, String role, String meetLink, LocalDateTime time){

        return "Dear HR,\n\n"
                +"A candidate has been shortlisted.\n\n"
                +"Name: "+candidateName+"\n"
                +"Role: "+role+"\n"
                +"Interview Time: "+time+"\n\n"
                +"Join Interview:\n"
                +meetLink+"\n\n"
                +"Please take the interview.\n\n"
                +"AI Recruiter System";
    }

    public static String buildInterviewerNotificationMessage(String candidateName,String role,String meetLink,LocalDateTime time){

        return "Hello,\n\n"
                +"You have been assigned an interview.\n\n"
                +"Candidate: "+candidateName+"\n"
                +"Role: "+role+"\n"
                +"Time: "+time+"\n"
                +"Meet Link: "+meetLink+"\n\n"
                +"Thanks";
    }

    public static String buildPanelSlotSelectionMessage(String to,String panelName,String panelPassword,String candidateName,String role){

        return "Hello "+panelName+",\n\n"
                +"You have been assigned to interview a candidate.\n\n"
                +"Candidate Name: "+candidateName+"\n"
                +"Role: "+role+"\n\n"
                +"Your Login Credentials:\n"
                +"Email: "+to+"\n"
                +"Password: "+panelPassword+"\n\n"
                +"Login URL:\n"
                +"http://localhost:4200/login\n\n"
                +"Please login and select your available interview slots.\n\n"
                +"Regards,\n"
                +"AI Recruitment Team";
    }

    public static String buildOfferEmailBody(String name,String url){

        return "Dear "+name+",\n\n"
                +"We are pleased to inform you that your offer letter has been generated.\n\n"
                +"Download Offer Letter:\n"
                +url+"\n\n"
                +"Best Regards,\n"
                +"HR Team";
    }

    public static String buildTestLinkMessage(String testLink){

        return "Dear Candidate,\n\n"
                +"Congratulations! You have been shortlisted.\n\n"
                +"Complete your online assessment using the link below:\n\n"
                +testLink+"\n\n"
                +"This link is valid for 48 hours.\n\n"
                +"Best Regards,\n"
                +"HR Team";
    }

    public static String buildCandidateSlotSelectionMessage(String candidateName, String role, List<String> freeSlots, String accessToken){

        StringBuilder body=new StringBuilder();

        body.append("Hello ")
                .append(candidateName)
                .append(",\n\n")
                .append("Congratulations!\n")
                .append("You have been shortlisted for the role: ")
                .append(role)
                .append("\n\n")
                .append("Available Interview Slots:\n\n");

        freeSlots.forEach(slot->
                body.append("• ")
                        .append(slot)
                        .append("\n")
        );

        body.append("\n")
                .append("Select your preferred slot:\n\n")
                .append("http://localhost:4200/slots?token=")
                .append(accessToken)
                .append("\n\n")
                .append("This link expires in 24 hours.\n\n")
                .append("Regards,\n")
                .append("AI Recruitment Team");

        return body.toString();
    }

    public static final String HR_OFFER_ACCEPTED_SUBJECT =
            "Candidate Accepted Offer Letter";

    public static final String HR_OFFER_ACCEPTED_BODY =
            "Hello HR Team,\n\n"
                    + "The candidate has accepted the offer letter.\n\n"
                    + "Candidate Name: %s\n"
                    + "Role: %s\n\n"
                    + "Please start onboarding process.\n\n"
                    + "Regards,\n"
                    + "AI Recruitment System";

    // OFFER REJECTED

    public static final String HR_OFFER_REJECTED_SUBJECT =
            "Candidate Rejected Offer Letter";

    public static final String HR_OFFER_REJECTED_BODY =
            "Hello HR Team,\n\n"
                    + "The candidate has rejected the offer letter.\n\n"
                    + "Candidate Name: %s\n"
                    + "Role: %s\n"
                    + "Rejection Reason: %s\n\n"
                    + "Please review recruitment status.\n\n"
                    + "Regards,\n"
                    + "AI Recruitment System";

    // CANDIDATE OFFER ACCEPTED

    public static final String CANDIDATE_OFFER_ACCEPTED_SUBJECT =
            "Welcome Onboard";

    public static final String CANDIDATE_OFFER_ACCEPTED_BODY =
            "Hello %s,\n\n"
                    + "Congratulations!\n\n"
                    + "You have successfully accepted the offer letter.\n\n"
                    + "Your onboarding process has started.\n\n"
                    + "Please upload required documents using below link:\n\n"
                    + "%s\n\n"
                    + "Required Documents:\n"
                    + "- Aadhaar Card\n"
                    + "- PAN Card\n"
                    + "- Resume\n"
                    + "- Education Certificates\n"
                    + "- Experience Certificates\n\n"
                    + "Regards,\n"
                    + "HR Team";

    // CANDIDATE OFFER REJECTED

    public static final String CANDIDATE_OFFER_REJECTED_SUBJECT =
            "Offer Response Confirmation";

    public static final String CANDIDATE_OFFER_REJECTED_BODY =
            "Hello %s,\n\n"
                    + "We have received your response regarding the offer letter.\n\n"
                    + "You have declined the offer.\n\n"
                    + "Thank you for your time and interest in our company.\n\n"
                    + "We wish you success in your future opportunities.\n\n"
                    + "Regards,\n"
                    + "HR Team";

    // DOCUMENT UPLOAD

    public static final String DOCUMENT_UPLOADED_SUBJECT =
            "Candidate Uploaded Onboarding Documents";

    public static final String DOCUMENT_UPLOADED_BODY =
            "Hello HR Team,\n\n"
                    + "Candidate has uploaded onboarding documents.\n\n"
                    + "Candidate Name: %s\n"
                    + "Uploaded Document: %s\n\n"
                    + "Please verify the uploaded document from onboarding portal.\n\n"
                    + "Regards,\n"
                    + "AI Recruitment System";

    // DOCUMENT REJECTED

    public static final String DOCUMENT_REJECTED_SUBJECT =
            "Document Verification Failed";

    public static final String DOCUMENT_REJECTED_BODY =
            "Hello %s,\n\n"
                    + "Your uploaded onboarding document could not be verified.\n\n"
                    + "Document Type: %s\n"
                    + "Reason: %s\n\n"
                    + "Please re-upload a valid and clear document through the onboarding portal.\n\n"
                    + "If you have any questions, please contact the HR team.\n\n"
                    + "Regards,\n"
                    + "HR Team";

    // ALL DOCUMENT VERIFIED

    public static final String ALL_DOCUMENT_VERIFIED_SUBJECT =
            "All Documents Verified Successfully";

    public static final String ALL_DOCUMENT_VERIFIED_BODY =
            "Hello %s,\n\n"
                    + "All your uploaded onboarding documents have been verified successfully.\n\n"
                    + "Your onboarding process is almost completed.\n\n"
                    + "HR team will shortly share your joining instructions and employee details.\n\n"
                    + "Regards,\n"
                    + "HR Team";

    // EMPLOYEE CREDENTIALS

    public static final String EMPLOYEE_CREDENTIALS_SUBJECT =
            "Welcome To The Company";

    public static final String EMPLOYEE_CREDENTIALS_BODY =
            "Hello %s,\n\n"
                    + "Congratulations!\n\n"
                    + "Your onboarding process has been completed successfully.\n\n"
                    + "Employee Details:\n\n"
                    + "Employee ID : %s\n"
                    + "Official Email : %s\n"
                    + "Temporary Password : %s\n\n"

                    + "Please login and change your password after first login.\n\n"
                    + "Portal Login Link : "
                             + "http://localhost:4200/PortalLogin\n\n"
                    + "Welcome aboard!\n\n"
                    + "Regards,\n"
                    + "HR Team";
}
