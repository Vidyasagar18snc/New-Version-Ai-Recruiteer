package com.Vendor.service;

import com.Vendor.dto.LoginRequest;
import com.Vendor.model.Employee;
import com.Vendor.model.Onboarding;
import com.Vendor.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

            Thread.sleep(1 * 60 * 1000);

            createEmployee(onboarding);

        } catch (Exception e) {

            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(null);
    }
    public void createEmployee(Onboarding onboarding) {
        String employeeId =
                generateEmployeeId();
        String officialEmail =
                onboarding.getCandidateName()
                        .toLowerCase()
                        .replace(" ", ".")
                        + "@company.com";
        String password =
                generatePassword();

        Employee employee =
                new Employee();

        employee.setEmployeeId(employeeId);

        employee.setEmployeeName(
                onboarding.getCandidateName()
        );

        employee.setPersonalEmail(
                onboarding.getEmail()
        );

        employee.setOfficialEmail(
                officialEmail
        );

        employee.setDepartment(
                onboarding.getDepartment()
        );

        employee.setRole(
                onboarding.getRole()
        );

        employee.setJoiningDate(
                LocalDate.parse(
                        onboarding.getJoiningDate()
                )
        );

        employee.setStatus("ACTIVE");

        employee.setPassword(password);

        employee.setFirstLogin(true);

        employeeRepository.save(employee);

        emailService.sendEmployeeCredentialsMail(
                onboarding.getEmail(),
                onboarding.getCandidateName(),
                employeeId,
                officialEmail,
                password
        );
        emailService.sendAssetAssignmentMail(

                employee.getEmployeeName(),

                employee.getEmployeeId(),

                employee.getDepartment(),

                employee.getJoiningDate().toString()
        );
    }

    private String generateEmployeeId() {

        return "EMP"
                + (1000
                + employeeRepository.count()
                + 1);
    }

    private String generatePassword() {

        String chars =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789";

        StringBuilder password =
                new StringBuilder();

        Random random =
                new Random();

        for (int i = 0; i < 8; i++) {

            password.append(
                    chars.charAt(
                            random.nextInt(
                                    chars.length()
                            )
                    )
            );
        }

        return password.toString();
    }

    public Object login(LoginRequest request) {

        // FIND EMPLOYEE

        Employee employee =

                employeeRepository
                        .findByOfficialEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Invalid email"
                                )
                        );

        // PASSWORD CHECK

        if (

                employee.getPassword() == null ||

                        !employee.getPassword()
                                .equals(
                                        request.getPassword()
                                )
        ) {

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        // FIRST LOGIN FLOW

        if (

                Boolean.TRUE.equals(
                        employee.getFirstLogin()
                )
        ) {

            return java.util.Map.of(

                    "message",

                    "RESET_PASSWORD_REQUIRED",

                    "firstLogin",

                    true,

                    "email",

                    employee.getOfficialEmail()
            );
        }

        // NORMAL LOGIN

        return java.util.Map.of(

                "message",

                "LOGIN_SUCCESS",

                "firstLogin",

                false,

                "employeeId",
                employee.getEmployeeId(),

                "employeeName",

                employee.getEmployeeName(),

                "department",

                employee.getDepartment()
        );
    }
    public String resetPassword(String email, String newPassword) {
        Employee employee = employeeRepository
                        .findByOfficialEmail(
                                email
                        )
                        .orElseThrow(() ->

                                new RuntimeException(
                                        "Employee not found"
                                )
                        );

        // UPDATE PASSWORD

        employee.setPassword(
                newPassword
        );

        // LOGIN COMPLETED

        employee.setFirstLogin(
                false
        );

        employeeRepository.save(
                employee
        );

        return "Password Reset Successful";
    }

    public Employee getEmployee(
            String employeeId
    ) {

        return (Employee) employeeRepository
                .findByEmployeeId(employeeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Employee not found"
                        )
                );
    }
    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll();
    }

    public long getTotalEmployeesCount() {
        return employeeRepository.count();
    }
}
