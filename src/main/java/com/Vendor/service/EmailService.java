package com.Vendor.service;

import com.Vendor.model.Employee;
import com.Vendor.model.Job;
import com.Vendor.util.EmailMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmailService{

    @Autowired
    private JavaMailSender mailSender;

    public void sendStatusEmail(String toEmail,String name,String status){

        String body;

        if("NOT_MATCH".equalsIgnoreCase(status)){
            body=EmailMessageUtil.skillExpNotMatchMessage(name);
        }else{
            body=EmailMessageUtil.buildStatusMessage(name,status);
        }

        sendEmail(toEmail,"Application Status Update",body);
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

        sendEmail(to,"Interview Assigned - "+candidateName,body);
    }

    public void sendInterviewerSlotSelectionMail(String to,String panelName,String panelPassword,String candidateName,String role){

        String body=EmailMessageUtil.buildPanelSlotSelectionMessage(to,panelName,panelPassword,candidateName,role);

        sendEmail(to,"Interview Panel Assignment",body);
    }

    public void sendOfferEmail(String email,String name,String url){

        String body=EmailMessageUtil.buildOfferEmailBody(name,url);

        sendEmail(email,"Offer Letter - Next Steps",body);
    }

    public void sendTestLink(String email,String testLink){

        if(email==null||email.isEmpty()){
            return;
        }

        String body=EmailMessageUtil.buildTestLinkMessage(testLink);

        sendEmail(email,"Online Test Invitation",body);
    }

    public void sendCandidateSlotSelectionMail(String to,String candidateName,String role,List<String> freeSlots,String accessToken){

        String body=EmailMessageUtil.buildCandidateSlotSelectionMessage(candidateName,role,freeSlots,accessToken);

        sendEmail(to,"Select Your Interview Slot",body);
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

        String body = String.format(
                EmailMessageUtil.CANDIDATE_OFFER_ACCEPTED_BODY,
                candidateName,
                onboardingLink
        );

        sendEmail(
                email,
                EmailMessageUtil.CANDIDATE_OFFER_ACCEPTED_SUBJECT,
                body
        );
    }

    public void sendCandidateOfferRejectedMail(
            String email,
            String candidateName
    ) {

        String body = String.format(
                EmailMessageUtil.CANDIDATE_OFFER_REJECTED_BODY,
                candidateName
        );

        sendEmail(
                email,
                EmailMessageUtil.CANDIDATE_OFFER_REJECTED_SUBJECT,
                body
        );
    }

    public void sendHrDocumentUploadedMail(
            String hrEmail,
            String candidateName,
            String documentType
    ) {

        String body = String.format(
                EmailMessageUtil.DOCUMENT_UPLOADED_BODY,
                candidateName,
                documentType
        );

        sendEmail(
                hrEmail,
                EmailMessageUtil.DOCUMENT_UPLOADED_SUBJECT,
                body
        );
    }

    public void sendDocumentRejectedMail(
            String email,
            String candidateName,
            String documentType,
            String remarks
    ) {

        String body = String.format(
                EmailMessageUtil.DOCUMENT_REJECTED_BODY,
                candidateName,
                documentType,
                remarks
        );

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

    public void sendEmployeeCredentialsMail(
            String to,
            String candidateName,
            String employeeId,
            String officialEmail,
            String password
    ) {

        String body = String.format(
                EmailMessageUtil.EMPLOYEE_CREDENTIALS_BODY,
                candidateName,
                employeeId,
                officialEmail,
                password

        );

        sendEmail(
                to,
                EmailMessageUtil.EMPLOYEE_CREDENTIALS_SUBJECT,
                body
        );
    }

    public void sendAssetAssignmentMail(
            String employeeName,
            String employeeId,
            String department,
            String joiningDate
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        // HR MAIL
        message.setTo("mnatikarsagar@gmail.com");

        message.setSubject("New Employee Asset Assignment Required");

        message.setText(
                "Hello HR Team,\n\n" +
                        "A new employee has joined the organization.\n\n" +
                        "Employee Details:\n" +
                        "Employee Name: " + employeeName + "\n" +
                        "Employee ID: " + employeeId + "\n" +
                        "Department: " + department + "\n" +
                        "Joining Date: " + joiningDate + "\n\n" +
                        "Please assign required company assets.\n\n" +
                        "Regards,\n" +
                        "HRMS System"
        );

        mailSender.send(message);
    }
    public void sendKTMail(
            String toMail,
            String employeeName,
            String projectName,
            String transferredTo) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toMail);
        message.setSubject("Knowledge Transfer Initiated");

        message.setText(
                "Hello,\n\n"
                        +
                        "Knowledge Transfer has been initiated.\n\n"
                        +
                        "Employee: "
                        + employeeName + "\n"
                        +
                        "Project: "
                        + projectName + "\n"
                        +
                        "Transferred To: "
                        + transferredTo + "\n\n"
                        +
                        "Please complete KT process.\n\n"
                        +

                        "Regards,\n"

                        +
                        "HRMS System"
        );

        mailSender.send(
                message
        );
    }
    public void sendJobNotification(Employee employee, Job job) {

        String subject = "Exciting Opportunity - Employee Referral Hiring";

        String body =
                "Dear " + employee.getEmployeeName() + ",\n\n" +

                        "We are excited to announce a new job opening in our organization.\n\n" +

                        "Position Details:\n" +
                        "----------------------------------\n" +
                        "Job Title   : " + job.getTitle() + "\n" +
                        "Experience  : " + job.getExperience() + "\n" +
                        "Location    : " + job.getLocation() + "\n" +
                        "Skills      : " + job.getSkills() + "\n\n" +

                        "If you know suitable candidates from your network, " +
                        "you can refer them for this opportunity.\n\n" +

                        "Employee referrals are highly appreciated and may " +
                        "be eligible for referral rewards/bonus as per company policy.\n\n" +

                        "Please connect with the HR team or login to the portal " +
                        "for more details.\n\n" +

                        "Best Regards,\n" +
                        "Talent Acquisition Team";

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(employee.getPersonalEmail());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}