package com.Vendor.util;

import java.time.LocalDateTime;
import java.util.List;

public class EmailMessageUtil {
    public static String buildStatusMessage(String name, String status) {
        return buildStatusMessage(name, status, null);
    }

    public static String buildStatusMessage(String name, String status, String interviewLink) {
        if ("Shortlisted".equalsIgnoreCase(status)) {
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
    public static String skillExpNotMatchMessage(String name) {

        return "Hi " + name + ",\n\n"
                + "Thank you for applying.\n\n"
                + "After reviewing your profile, we found that your skills and experience "
                + "do not match our current job requirements.\n\n"
                + "So, we are unable to shortlist your application at this time.\n\n"
                + "We encourage you to apply again in the future.\n\n"
                + "Best Regards,\nHR Team";
    }

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

        if ("TOP_PERFORMER".equalsIgnoreCase(status)) {

            message.append("🏆 Outstanding Performance!\n\n")
                    .append("Congratulations! You are among the top performers in this assessment.\n")
                    .append("Your performance has been exceptional, and you have demonstrated strong skills and potential.\n\n")
                    .append("We are excited to fast-track your application to the next round.\n")
                    .append("The upcoming stage will be a Face-to-Face interview.\n\n")
                    .append("Our team will share the interview details (date, time, and meeting link) shortly.\n")
                    .append("Stay tuned for further communication.\n");

        }

        else if ("PASS".equalsIgnoreCase(status)) {

            message.append("🎉 Congratulations! You have successfully cleared the test.\n\n")
                    .append("We are pleased to inform you that you have been shortlisted for the next round.\n")
                    .append("The next stage will be a Face-to-Face interview.\n\n")
                    .append("Our team will share the interview date, time, and venue details with you shortly.\n")
                    .append("Please keep an eye on your email for further communication.\n");

        }

        else if ("REVIEW".equalsIgnoreCase(status)) {

            message.append("👍 Good effort! Your performance is currently under review.\n\n")
                    .append("Our team will evaluate your results and get back to you soon with the next steps.\n");

        }
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
    public static String buildInterviewerNotificationMessage(
            String candidateName,
            String role,
            String meetLink,
            LocalDateTime time) {

        String feedbackLink = "http://localhost:4200/interviewFeedback";

        return """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial,sans-serif;color:#333;line-height:1.6;">

            <h2 style="color:#2E86DE;">
                 Interview Assignment
            </h2>

            <p>Dear Interviewer,</p>

            <p>
                You have been assigned to conduct an interview for the following candidate.
            </p>

            <table style="border-collapse:collapse;">
                <tr>
                    <td><strong>Candidate Name</strong></td>
                    <td>: %s</td>
                </tr>
                <tr>
                    <td><strong>Role</strong></td>
                    <td>: %s</td>
                </tr>
                <tr>
                    <td><strong>Interview Time</strong></td>
                    <td>: %s</td>
                </tr>
            </table>

            <br>

            <a href="%s"
               style="
                    background:#28a745;
                    color:#fff;
                    text-decoration:none;
                    padding:10px 18px;
                    border-radius:5px;
                    font-size:14px;
                    font-weight:600;
                    display:inline-block;">
                🎥 Join Meeting
            </a>

            &nbsp;&nbsp;

            <a href="%s"
               style="
                    background:#007BFF;
                    color:#fff;
                    text-decoration:none;
                    padding:10px 18px;
                    border-radius:5px;
                    font-size:14px;
                    font-weight:600;
                    display:inline-block;">
                📝 Submit Feedback
            </a>

            <br><br>

            <p>
                Please complete the interview and submit your feedback immediately after the interview.
            </p>

            <hr>

            <p style="font-size:13px;color:#666;">
                If the buttons don't work, use the links below:
            </p>

            <p>
                <strong>Meeting:</strong><br>
                %s
            </p>

            <p>
                <strong>Feedback:</strong><br>
                %s
            </p>

            <br>

            <p>
                Regards,<br>
                <strong>AI Recruitment Team</strong>
            </p>

        </body>
        </html>
        """.formatted(
                candidateName,
                role,
                time,
                meetLink,
                feedbackLink,
                meetLink,
                feedbackLink
        );
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

    public static String buildOfferEmailBody(String candidateName, String offerUrl) {

        return """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Offer Letter</title>
</head>

<body style="margin:0;padding:30px;background:#f4f6f9;font-family:Arial,Helvetica,sans-serif;">

<table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;">
<tr>
<td align="center">

<table width="650" cellpadding="0" cellspacing="0"
style="background:#ffffff;border-radius:10px;overflow:hidden;box-shadow:0 4px 15px rgba(0,0,0,.1);">

<tr>
<td style="background:#0d6efd;padding:25px;text-align:center;color:#ffffff;">

<h1 style="margin:0;">HG Infotech</h1>

<p style="margin-top:10px;font-size:16px;">
Offer Letter Notification
</p>

</td>
</tr>

<tr>
<td style="padding:40px;">

<p style="font-size:16px;">
Dear <strong>%s</strong>,
</p>

<p style="font-size:15px;line-height:28px;color:#555;">
Congratulations!
<br><br>

We are delighted to inform you that you have successfully completed our recruitment process.

We are pleased to extend an employment offer to you.

<b>Your Offer Letter has been attached to this email as a PDF.</b>

You may also view your offer letter online by clicking the button below.
</p>

<div style="text-align:center;margin:40px 0;">

<a href="%s"
style="
background:#0d6efd;
color:#ffffff;
padding:15px 35px;
text-decoration:none;
font-size:16px;
font-weight:bold;
border-radius:6px;
display:inline-block;">

View Offer Letter

</a>

</div>

<p style="font-size:14px;color:#666;">
If the button doesn't work, copy and paste the following link into your browser:
</p>

<p style="word-break:break-all;">
<a href="%s">%s</a>
</p>

<hr style="margin:35px 0;border:none;border-top:1px solid #ddd;">

<p style="font-size:14px;color:#666;line-height:24px;">
We are excited to have you join the HG Infotech family.

Please review the attached Offer Letter carefully and complete the required response at your earliest convenience.

If you have any questions, feel free to contact our HR team.
</p>

<p style="margin-top:35px;">
Regards,<br><br>

<strong>Human Resources</strong><br>
HG Infotech
</p>

</td>
</tr>

</table>

</td>
</tr>
</table>

</body>
</html>
""".formatted(candidateName, offerUrl, offerUrl, offerUrl);
    }       public static String buildTestLinkMessage(String testLink) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f6f8; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 30px auto; background: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08);">
                    
                    <div style="background: #1f3c88; color: #ffffff; padding: 20px; text-align: center;">
                        <h2 style="margin: 0;">Online Test Invitation</h2>
                    </div>

                    <div style="padding: 30px; color: #333333; line-height: 1.6;">
                        <p>Dear Candidate,</p>

                        <p>
                            You have been invited to complete an online assessment as part of our recruitment process.
                            Please click the button below to begin your test.
                        </p>

                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s"
                               style="background-color: #1f3c88; color: #ffffff; text-decoration: none; padding: 14px 28px; border-radius: 6px; font-size: 16px; font-weight: bold; display: inline-block;">
                                Start Test
                            </a>
                        </div>

                        <p>
                            Please ensure you complete the assessment within the given time frame and use a stable internet connection.
                        </p>

                        <p>If you face any issues accessing the test, please contact the recruitment team.</p>

                        <p>Best regards,<br>
                        Recruitment Team</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(testLink);
    }

    public static String buildCandidateSlotSelectionMessage(
            String candidateName,
            String role,
            List<String> freeSlots,
            String accessToken) {

        String slotLink = "http://localhost:4200/slots?token=" + accessToken;

        StringBuilder slots = new StringBuilder();

        for (String slot : freeSlots) {
            slots.append("<li>")
                    .append(slot)
                    .append("</li>");
        }

        return """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial,sans-serif;color:#333;line-height:1.6;">

            <h2 style="color:#2E86DE;">
                🎉 Congratulations!
            </h2>

            <p>Dear <strong>%s</strong>,</p>

            <p>
                We are pleased to inform you that you have been
                <strong>shortlisted</strong> for the position of
                <strong>%s</strong>.
            </p>

            <p>
                Please choose one of the available interview slots below.
            </p>
            <h3>Available Interview Slots</h3>
            <ul>
                %s
            </ul>
            <br>
            <a href="%s"
                           style="
                                background-color:#007BFF;
                                color:#ffffff;
                                text-decoration:none;
                                padding:10px 18px;
                                border-radius:5px;
                                font-size:14px;
                                font-weight:600;
                                display:inline-block;
                                font-family:Arial,sans-serif;">
                            📅 Select Interview Slot
                        </a>

            <br><br>

            <p>
                <strong>Note:</strong> This link will expire in <strong>24 hours</strong>.
            </p>

            <p>
                If the button doesn't work, copy and paste the following URL into your browser:
            </p>

            <p style="word-break:break-all;">
                %s
            </p>

            <br>

            <p>
                Regards,<br>
                <strong>AI Recruitment Team</strong>
            </p>

        </body>
        </html>
        """.formatted(
                candidateName,
                role,
                slots.toString(),
                slotLink,
                slotLink
        );
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
                    + "- offer letter\n"
                    + "- Releaving letter\n\n"
                    + "-3Month Salary slip"
                    + "Regards,\n"
                    + "HR Team";


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

    public static final String ALL_DOCUMENT_VERIFIED_SUBJECT =
            "All Documents Verified Successfully";

    public static final String ALL_DOCUMENT_VERIFIED_BODY =
            "Hello %s,\n\n"
                    + "All your uploaded onboarding documents have been verified successfully.\n\n"
                    + "Your onboarding process is almost completed.\n\n"
                    + "HR team will shortly share your joining instructions and employee details.\n\n"
                    + "Regards,\n"
                    + "HR Team";

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

    public static String buildCandidateOfferAcceptedBody(
            String candidateName,
            String uploadUrl
    ) {

        return """
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
</head>

<body style="margin:0;padding:30px;background:#f4f6f9;font-family:Arial,Helvetica,sans-serif;">

<table width="100%%" cellpadding="0" cellspacing="0">
<tr>
<td align="center">

<table width="650" cellpadding="0" cellspacing="0"
style="background:#ffffff;border-radius:10px;overflow:hidden;
box-shadow:0 3px 12px rgba(0,0,0,.12);">

<tr>
<td style="background:#198754;color:white;padding:25px;text-align:center;">

<h2 style="margin:0;">Welcome to HG Infotech</h2>

<p style="margin-top:10px;">
Your Onboarding Journey Begins
</p>

</td>
</tr>

<tr>
<td style="padding:40px;">

<p>Dear <strong>%s</strong>,</p>

<p style="line-height:28px;color:#555;">

Congratulations!

We are delighted to welcome you to <strong>HG Infotech</strong>.

Thank you for accepting our employment offer. Your onboarding process has now officially begun.

To proceed with the next step, please upload the required onboarding documents by clicking the button below.

</p>

<div style="text-align:center;margin:35px 0;">

<a href="%s"
style="
background:#198754;
color:white;
padding:15px 35px;
text-decoration:none;
border-radius:6px;
font-weight:bold;
display:inline-block;">

Upload Documents

</a>

</div>

<p style="font-weight:bold;">Required Documents</p>

<ul style="line-height:28px;color:#555;">
<li>Aadhaar Card</li>
<li>PAN Card</li>
<li>Updated Resume</li>
<li>Signed Offer Letter</li>
<li>Relieving Letter (if applicable)</li>
<li>Last 3 Months' Salary Slips (if applicable)</li>
</ul>
<p style="color:#666;">
If the button above doesn't work, copy and paste the following link into your browser:
</p>
<p style="word-break:break-all;">
<a href="%s">%s</a>
</p>

<hr style="margin:35px 0;border:none;border-top:1px solid #ddd;">
<p style="color:#666;line-height:24px;">
Please upload all required documents as soon as possible to avoid any delay in your onboarding process.
If you have any questions, feel free to contact the HR team.

</p>

<p>

Best Regards,<br>
<strong>Human Resources</strong><br>
HG Infotech
</p>
</td>
</tr>
</table>
</td>
</tr>
</table>
</body>
</html>
""".formatted(
                candidateName,
                uploadUrl,
                uploadUrl,
                uploadUrl
        );
    }

    private static String urlEncode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}
