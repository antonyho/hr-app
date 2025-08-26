package com.hrapp.repository;

import com.hrapp.model.AbsenceRequest;
import com.hrapp.model.AbsenceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, UUID> {
    List<AbsenceRequest> findByEmployeeId(UUID employeeId);
    List<AbsenceRequest> findByStatus(AbsenceStatus status);
    List<AbsenceRequest> findByEmployeeIdAndStatus(UUID employeeId, AbsenceStatus status);
    List<AbsenceRequest> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<AbsenceRequest> findByApprovedById(UUID approvedById);
}