package com.Vendor.service;

import com.Vendor.model.Employee;
import com.Vendor.model.Job;
import com.Vendor.model.Onboarding;
import com.Vendor.util.EmailMessageUtil;
import com.google.api.client.util.Value;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService{

    @Autowired
    private JavaMailSender mailSender;
    @Value("${app.interview.base-url}")
    private String testBaseUrl;

    public void sendStatusEmail(String toEmail,String name,String status){

        String body;

        if("NOT_MATCH".equalsIgnoreCase(status)){
            body=EmailMessageUtil.skillExpNotMatchMessage(name);
        }else{
            body=EmailMessageUtil.buildStatusMessage(name,status);
        }

        sendEmail(toEmail,"Application Status Update",body);
    }
    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

            System.out.println("HTML Email sent successfully!");

        } catch (Exception e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }

    public void sendInterviewEmail(String toEmail,String name,String interviewLink,Object interviewTime){

        if(toEmail==null||toEmail.isEmpty()){
            return;
        }

        String body=EmailMessageUtil.buildInterviewMessage(name,interviewLink,interviewTime);

        sendEmail(toEmail,"Interview Invitation",body);
    }

    public void sendResultEmail(String to,String name,int score,int total,double percentage,int rank,String status){

        if(to==null||to.isEmpty()){
            return;
        }

        String body=EmailMessageUtil.buildResultMessage(name,score,total,percentage,rank,status);

        sendEmail(to,"Test Result",body);
    }

    public void sendInterviewerNotification(String to,String candidateName,String role,String meetLink,LocalDateTime time){
        String body=EmailMessageUtil.buildInterviewerNotificationMessage(candidateName,role,meetLink,time);
        sendHtmlEmail(to, "Interview Assigned - " + candidateName, body);
    }

    public void sendInterviewerSlotSelectionMail(String to,String panelName,String panelPassword,String candidateName,String role){
        String body=EmailMessageUtil.buildPanelSlotSelectionMessage(to,panelName,panelPassword,candidateName,role);
        sendEmail(to,"Interview Panel Assignment",body);
    }

    public void sendOfferEmail(String email, String name, String url, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Offer Letter - Next Steps");

            String body = EmailMessageUtil.buildOfferEmailBody(name, url);

            // Send HTML email
            helper.setText(body, true);

            if (pdfBytes != null && pdfBytes.length > 0) {
                ByteArrayResource resource = new ByteArrayResource(pdfBytes);
                helper.addAttachment("Offer_Letter.pdf", resource);
            }

            mailSender.send(message);
            System.out.println("Email with PDF attachment sent successfully!");

        } catch (Exception e) {
            e.printStackTrace();

            String body = EmailMessageUtil.buildOfferEmailBody(name, url);
            sendHtmlEmail(email, "Offer Letter - Next Steps", body);
        }
    }
    public void sendTestLink(String email,String testLink){
        if(email==null||email.isEmpty()){
            return;
        }
        String body=EmailMessageUtil.buildTestLinkMessage(testLink);
        sendHtmlEmail(email,"Online Test Invitation",body);
    }
    public void sendCandidateSlotSelectionMail(
            String to,
            String candidateName,
            String role,
            List<String> freeSlots,
            String accessToken) {

        String body = EmailMessageUtil.buildCandidateSlotSelectionMessage(
                candidateName,
                role,
                freeSlots,
                accessToken
        );
        sendHtmlEmail(to, "Select Your Interview Slot", body);
    }

    private void sendEmail(String toEmail,String subject,String body){

        SimpleMailMessage message=new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Email sent successfully!");
    }
    public void sendHrOfferAcceptedMail(
            String hrEmail,
            String candidateName,
            String role
    ) {

        String body = String.format(
                EmailMessageUtil.HR_OFFER_ACCEPTED_BODY,
                candidateName,
                role
        );

        sendEmail(
                hrEmail,
                EmailMessageUtil.HR_OFFER_ACCEPTED_SUBJECT,
                body
        );
    }

    public void sendHrOfferRejectedMail(
            String hrEmail,
            String candidateName,
            String role,
            String rejectionReason

    ) {

        String body = String.format(
                EmailMessageUtil.HR_OFFER_REJECTED_BODY,
                candidateName,
                role,
                rejectionReason
        );

        sendEmail(
                hrEmail,
                EmailMessageUtil.HR_OFFER_REJECTED_SUBJECT,
                body
        );
    }

    public void sendCandidateOfferAcceptedMail(
            String email,
            String candidateName,
            String candidateId
    ) {

        String onboardingLink =
                "http://localhost:4200/uploaddocuments/" + candidateId;

        String body = EmailMessageUtil.buildCandidateOfferAcceptedBody(
                candidateName,
                onboardingLink
        );

        sendHtmlEmail(
                email,
                "Welcome to HG Infotech - Complete Your Onboarding",
                body
        );
    }
    public void sendCandidateOfferRejectedMail(String email, String candidateName) {
        String body = String.format(EmailMessageUtil.CANDIDATE_OFFER_REJECTED_BODY, candidateName);
        sendEmail(email, EmailMessageUtil.CANDIDATE_OFFER_REJECTED_SUBJECT, body);
    }

    public void sendHrDocumentUploadedMail(
            String hrEmail,
            String candidateName,
            String documentType
    ) {
        String body = String.format(EmailMessageUtil.DOCUMENT_UPLOADED_BODY, candidateName, documentType);
        sendEmail(hrEmail, EmailMessageUtil.DOCUMENT_UPLOADED_SUBJECT, body);
    }

    public void sendDocumentRejectedMail(
            String email,
            String candidateName,
            String documentType,
            String remarks
    ) {

        String body = String.format(EmailMessageUtil.DOCUMENT_REJECTED_BODY,
                candidateName,
                documentType,
                remarks);
        sendEmail(
                email,
                EmailMessageUtil.DOCUMENT_REJECTED_SUBJECT,
                body
        );
    }

    public void sendAllDocumentsVerifiedMail(
            String email,
            String candidateName
    ) {

        String body = String.format(
                EmailMessageUtil.ALL_DOCUMENT_VERIFIED_BODY,
                candidateName
        );

        sendEmail(
                email,
                EmailMessageUtil.ALL_DOCUMENT_VERIFIED_SUBJECT,
                body
        );
    }

    public void sendKTMail(
            String toMail,
            String employeeName,
            String documentUrl
    ) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(toMail);

        message.setSubject(
                "Knowledge Transfer Document Submitted"
        );

        message.setText(
                "Hello " + employeeName + ",\n\n"

                        + "A Knowledge Transfer document has been submitted successfully.\n\n"

                        + "Document Link:\n"
                        + documentUrl
                        + "\n\n"

                        + "Please review the document and complete the KT process if required.\n\n"

                        + "Regards,\n"
                        + "HRMS System"
        );

        mailSender.send(
                message
        );
    }
    public void sendJobNotification(Employee employee, Job job) {

        String subject = "Exciting Opportunity - Employee Referral Hiring";

        String body = String.format(
                "Dear %s,%n%n" +
                        "We are excited to announce a new job opening within our organization.%n%n" +
                        "Position Details%n" +
                        "================%n" +
                        "%-12s : %s%n" +
                        "%-12s : %s Years%n" +
                        "%-12s : %s%n" +
                        "%-12s : %s%n%n" +
                        "We encourage you to refer qualified candidates from your professional network for this opportunity.%n%n" +
                        "Employee referrals are highly valued and may be eligible for referral rewards as per the company referral policy.%n%n" +
                        "For more information, please connect with the HR team or log in to the Employee Referral Portal.%n%n" +
                        "Best Regards,%n%n" +
                        "Hg Infotech",

                employee.getEmployeeName(),
                "Job Title", job.getTitle(),
                "Experience", job.getExperience(),
                "Location", job.getLocation(),
                "Skills", String.join(", ", job.getSkills())
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(employee.getPersonalEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }    public void sendForgotPasswordOtp(String email,
                                      String employeeName,
                                      String otp) {

        String subject = "Password Reset OTP";

        String body =
                "Dear " + employeeName + ",\n\n" +
                        "Your OTP for password reset is: " + otp + "\n\n" +
                        "This OTP is valid for 10 minutes.\n\n" +
                        "Regards,\nHR Team";

        sendEmail(email, subject, body);
    }
    public void sendBackgroundVerificationMail(
            String hrEmail,
            Onboarding onboarding
    ) {
        String subject = "Employment Background Verification Request";

        String body = String.format("""
            Dear HR Team,
            Greetings from HG Infotech.

            We are conducting a background verification for a candidate who has been selected to join our organization.

            Candidate Details:
            Candidate Name : %s
            Candidate ID : %s
            Email : %s
            Role : %s
            Experience : %s

            We kindly request your assistance in verifying the candidate's employment history with your organization.

            Please confirm:
            1. Employment duration
            2. Last designation held
            3. Reason for separation
            4. Any performance or disciplinary concerns

            Your response will help us complete our background verification process.

            Thank you for your cooperation.

            Regards,
            HR Team
            HG Infotech
            """,
                onboarding.getCandidateName(),
                onboarding.getCandidateId(),
                onboarding.getEmail(),
                onboarding.getRole(),
                onboarding.getExperience()
        );

        sendEmail(hrEmail, subject, body);
    }

    public void sendPanelApprovalMail(
            String panelEmail,
            String candidateId,
            String candidateName,
            String role,
            String selectedSlot
    ) {

        String acceptUrl = "http://localhost:8081/api/panel/accept?candidateId=" + candidateId;
        String declineUrl = "http://localhost:8081/api/panel/decline?candidateId=" + candidateId;

        String subject = "Interview Approval Required";

        String body = """
    <!DOCTYPE html>
    <html>
    <body style="font-family: Arial, sans-serif; color:#333;">

        <h2 style="color:#2c3e50;">Interview Approval Required</h2>

        <p>Dear Panel,</p>

        <p>A candidate has selected an interview slot. Please review the details below:</p>

        <table style="border-collapse: collapse;">
            <tr>
                <td><strong>Candidate Name:</strong></td>
                <td>%s</td>
            </tr>
            <tr>
                <td><strong>Role:</strong></td>
                <td>%s</td>
            </tr>
            <tr>
                <td><strong>Selected Slot:</strong></td>
                <td>%s</td>
            </tr>
        </table>

        <br>

        <a href="%s"
           style="
                background-color:#28a745;
                color:white;
                padding:12px 24px;
                text-decoration:none;
                border-radius:6px;
                font-weight:bold;
                display:inline-block;
                margin-right:10px;">
            ✅ Accept
        </a>

        <br><br>

        <div style="margin-top:20px; padding:16px; border:1px solid #ddd; border-radius:8px; width:420px;">
            <h3 style="margin-top:0; color:#dc3545;">Reject Interview Slot</h3>

            <label for="rejectReason"><strong>Select Reject Reason:</strong></label>
            <br><br>

     <select id="rejectReason" name="rejectReason"
                         style="width:100%; padding:10px; border:1px solid #ccc; border-radius:6px;">
                     <option value="">-- Select Reason --</option>
                     <option value="High workload and limited panel bandwidth">High workload and limited panel bandwidth</option>
                     <option value="Panel discussion required before proceeding">Panel discussion required before proceeding</option>
                     <option value="Scheduling conflict with existing commitments">Scheduling conflict with existing commitments</option>
                     <option value="Panel member unavailable for the selected slot">Panel member unavailable for the selected slot</option>
                     <option value="Interview needs to be aligned with the relevant technical panel">Interview needs to be aligned with the relevant technical panel</option>
                     <option value="Additional internal evaluation is required">Additional internal evaluation is required</option>
                     <option value="Role expectations need further clarification with the hiring team">Role expectations need further clarification with the hiring team</option>
                     <option value="Interview to be rescheduled based on panel availability">Interview to be rescheduled based on panel availability</option>
                     <option value="Candidate profile requires discussion with the hiring team">Candidate profile requires discussion with the hiring team</option>
                     <option value="Other">Other</option>
                 </select>

            <br><br>

            <a href="%s"
               style="
                    background-color:#dc3545;
                    color:white;
                    padding:12px 24px;
                    text-decoration:none;
                    border-radius:6px;
                    font-weight:bold;
                    display:inline-block;">
                ❌ Reject
            </a>
        </div>

        <br><br>

        <p>Regards,<br>
        <strong>AI Recruiter System</strong></p>

    </body>
    </html>
    """.formatted(
                candidateName,
                role,
                selectedSlot,
                acceptUrl,
                declineUrl
        );

        sendHtmlEmail(panelEmail, subject, body);
    }

    public void sendEmployeeIdMail(
            String email,
            String candidateName,
            String employeeId,
            String role
    ) {

        String subject = "Employee ID Generated";

        String body =
                "Dear " + candidateName + ",\n\n" +
                        "Congratulations! Your onboarding process has been completed successfully.\n\n" +
                        "Employee Name : " + candidateName + "\n" +
                        "Employee ID   : " + employeeId + "\n" +
                        "Role          : " + role + "\n\n" +
                        "Official credentials will be shared separately by the HR team.\n\n" +
                        "Regards,\n" +
                        "HR Team";

        sendEmail(email, subject, body);
    }
}