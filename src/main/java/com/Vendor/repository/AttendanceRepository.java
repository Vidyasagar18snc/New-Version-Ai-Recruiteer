package com.Vendor.repository;

import com.Vendor.model.Attendance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
@Repository
public interface AttendanceRepository
        extends MongoRepository<Attendance, String> {

    // CHECK ALREADY EXISTS

    boolean existsByEmployeeIdAndDate(String employeeId, LocalDate date);

    // FIND TODAY ATTENDANCE

    Optional<Attendance>findByEmployeeIdAndDate(String employeeId, LocalDate date);

    // TOTAL ATTENDANCE COUNT

    long countByEmployeeId(String employeeId);
}