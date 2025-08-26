package com.hrapp.repository;

import com.hrapp.model.EmployeeProfile;
import com.hrapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, UUID> {
    Optional<EmployeeProfile> findByUserId(UUID userId);
    Optional<EmployeeProfile> findByEmployeeId(String employeeId);
    List<EmployeeProfile> findByManagerId(UUID managerId);
    boolean existsByEmployeeId(String employeeId);
}