package com.Vendor.repository;

import com.Vendor.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findByOfficialEmail(String officialEmail);
    Optional<Employee> findByEmployeeId(String employeeId);

    List<Employee> findByDepartment(String hr);

    Optional<Employee>

    findByEmployeeName(
            String employeeName
    );}