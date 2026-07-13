package com.Vendor.service;
import com.Vendor.dto.DepartmentOverviewResponse;
import com.Vendor.dto.LoginRequest;
import com.Vendor.dto.RetentionRateResponse;
import com.Vendor.dto.SignupRequest;
import com.Vendor.model.Employee;
import com.Vendor.model.Onboarding;
import com.Vendor.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;

    @Async
    public CompletableFuture<Void> createEmployeeAfterDelay(Onboarding onboarding) {
        try {
            Thread.sleep(60 * 1000);
            createEmployeeIdOnly(onboarding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }
    public void createEmployeeIdOnly(Onboarding onboarding) {
        String employeeId = generateEmployeeId();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(onboarding.getCandidateName());
        employee.setPersonalEmail(onboarding.getEmail());
        employee.setDepartment(onboarding.getDepartment());
        employee.setRole(onboarding.getRole());
        employee.setJoiningDate(LocalDate.parse(onboarding.getJoiningDate()));
        employee.setStatus("PENDING_CREDENTIALS");
        employee.setEmploymentType(onboarding.getEmploymentType());
        employeeRepository.save(employee);
        emailService.sendEmployeeIdMail(
                onboarding.getEmail(),
                onboarding.getCandidateName(),
                employeeId,
                onboarding.getRole()
        );
    }

    private String generateEmployeeId() {
        return "EMP" + (1000 + employeeRepository.count() + 1);
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public Object login(LoginRequest request) {
        Employee employee = employeeRepository.findByOfficialEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (employee.getPassword() == null ||
                !employee.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (Boolean.TRUE.equals(employee.getFirstLogin())) {
            return Map.of(
                    "message", "RESET_PASSWORD_REQUIRED",
                    "firstLogin", true,
                    "email", employee.getOfficialEmail()
            );
        }

        return Map.of(
                "message", "LOGIN_SUCCESS",
                "firstLogin", false,
                "employeeId", employee.getEmployeeId(),
                "employeeName", employee.getEmployeeName(),
                "department", employee.getDepartment()
        );
    }

    public String resetPassword(String email, String newPassword) {
        Employee employee = employeeRepository.findByOfficialEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee.setPassword(newPassword);
        employee.setFirstLogin(false);
        employeeRepository.save(employee);
        return "Password Reset Successful";
    }

    public Employee getEmployee(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public long getTotalEmployeesCount() {
        return employeeRepository.count();
    }


    public String forgotPassword(String email) {

        Employee employee = employeeRepository.findByOfficialEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        employee.setResetOtp(otp);
        employee.setOtpExpiryTime(LocalDateTime.now().plusMinutes(10));
        employeeRepository.save(employee);
        emailService.sendForgotPasswordOtp(
                employee.getOfficialEmail(),
                employee.getEmployeeName(),
                otp);
        return "OTP sent successfully";
    }
    public String forgotPasswordReset(String email,
                                      String otp,
                                      String newPassword) {
        Employee employee = employeeRepository.findByOfficialEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        if (employee.getResetOtp() == null ||
                !employee.getResetOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        if (employee.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        employee.setPassword(newPassword);
        employee.setResetOtp(null);
        employee.setOtpExpiryTime(null);
        employeeRepository.save(employee);
        return "Password reset successful";
    }

    public String register(SignupRequest request) {
        if (employeeRepository.findByOfficialEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        String employeeId = generateEmployeeId();
        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setEmployeeName(request.getUsername());
        employee.setOfficialEmail(request.getEmail());
        employee.setPassword(request.getPassword());
        employee.setDepartment(request.getDepartment());
        employee.setRole(request.getDepartment());
        employee.setJoiningDate(LocalDate.now());
        employee.setStatus("ACTIVE");
        employee.setFirstLogin(false);
        employeeRepository.save(employee);
        return "Account created successfully";
    }
    public double getRetentionRate() {
        long totalEmployees = employeeRepository.count();

        if (totalEmployees == 0) {
            return 0.0;
        }

        long activeEmployees = employeeRepository.countByStatus("ACTIVE");
        double rate = ((double) activeEmployees / totalEmployees) * 100;

        return round(rate);
    }

    public double getLastMonthRetentionRate() {
        return getRetentionRate();
    }

    public double getRetentionRateChange() {
        return round(getRetentionRate() - getLastMonthRetentionRate());
    }

    public RetentionRateResponse getRetentionSummary() {
        double currentMonthRate = getRetentionRate();
        double lastMonthRate = getLastMonthRetentionRate();
        double change = round(currentMonthRate - lastMonthRate);

        return new RetentionRateResponse(currentMonthRate, lastMonthRate, change);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
    public DepartmentOverviewResponse getDepartmentOverview() {

        long engineering = employeeRepository.countByDepartment("Engineering");
        long sales = employeeRepository.countByDepartment("Admin");
        long hr = employeeRepository.countByDepartment("HR");
        long operations = employeeRepository.countByDepartment("Operations");
        long finance = employeeRepository.countByDepartment("Finance");

        long total = employeeRepository.count();

        return new DepartmentOverviewResponse(
                total,
                engineering,
                sales,
                hr,
                operations,
                finance
        );
    }

}