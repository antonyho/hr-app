package com.hrapp.service;

import com.hrapp.dto.AbsenceRequestDto;
import com.hrapp.dto.ApprovalRequestDto;
import com.hrapp.dto.CreateAbsenceRequestDto;
import com.hrapp.model.AbsenceRequest;
import com.hrapp.model.AbsenceStatus;
import com.hrapp.model.EmployeeProfile;
import com.hrapp.model.User;
import com.hrapp.model.UserRole;
import com.hrapp.repository.AbsenceRequestRepository;
import com.hrapp.repository.EmployeeProfileRepository;
import com.hrapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AbsenceRequestService {
    
    @Autowired
    private AbsenceRequestRepository absenceRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    public AbsenceRequestDto createAbsenceRequest(CreateAbsenceRequestDto requestDto, UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate dates
        if (requestDto.getEndDate().isBefore(requestDto.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }
        
        AbsenceRequest absenceRequest = new AbsenceRequest(
            currentUser,
            requestDto.getStartDate(),
            requestDto.getEndDate(),
            requestDto.getReason()
        );
        
        AbsenceRequest savedRequest = absenceRequestRepository.save(absenceRequest);
        return convertToDto(savedRequest);
    }
    
    public List<AbsenceRequestDto> getMyAbsenceRequests(UUID currentUserId) {
        return absenceRequestRepository.findByEmployeeId(currentUserId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<AbsenceRequestDto> getAllAbsenceRequests(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only managers can see all requests
        if (currentUser.getRole() != UserRole.MANAGER) {
            throw new AccessDeniedException("Access denied");
        }
        
        return absenceRequestRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<AbsenceRequestDto> getPendingAbsenceRequests(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only managers can see pending requests for approval
        if (currentUser.getRole() != UserRole.MANAGER) {
            throw new AccessDeniedException("Access denied");
        }
        
        return absenceRequestRepository.findByStatus(AbsenceStatus.PENDING)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public AbsenceRequestDto getAbsenceRequest(UUID requestId, UUID currentUserId) {
        AbsenceRequest request = absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Absence request not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check access permissions
        if (!canAccessRequest(request, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }
        
        return convertToDto(request);
    }
    
    public AbsenceRequestDto approveOrRejectRequest(UUID requestId, ApprovalRequestDto approvalDto, UUID currentUserId) {
        AbsenceRequest request = absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Absence request not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only managers can approve/reject requests
        if (currentUser.getRole() != UserRole.MANAGER) {
            throw new AccessDeniedException("Access denied");
        }
        
        // Can only approve/reject pending requests
        if (request.getStatus() != AbsenceStatus.PENDING) {
            throw new RuntimeException("Can only approve or reject pending requests");
        }
        
        request.setStatus(approvalDto.getStatus());
        request.setApprovedBy(currentUser);
        request.setApprovedAt(LocalDateTime.now());
        request.setComments(approvalDto.getComments());
        
        AbsenceRequest savedRequest = absenceRequestRepository.save(request);
        return convertToDto(savedRequest);
    }
    
    public void deleteAbsenceRequest(UUID requestId, UUID currentUserId) {
        AbsenceRequest request = absenceRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Absence request not found"));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Can only delete own pending requests
        if (!request.getEmployee().getId().equals(currentUserId) || request.getStatus() != AbsenceStatus.PENDING) {
            throw new AccessDeniedException("Can only delete your own pending requests");
        }
        
        absenceRequestRepository.delete(request);
    }
    
    private boolean canAccessRequest(AbsenceRequest request, User currentUser) {
        // Managers can see all requests
        if (currentUser.getRole() == UserRole.MANAGER) {
            return true;
        }
        
        // Employees can only see their own requests
        return request.getEmployee().getId().equals(currentUser.getId());
    }
    
    private AbsenceRequestDto convertToDto(AbsenceRequest request) {
        AbsenceRequestDto dto = new AbsenceRequestDto();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setRequestedAt(request.getRequestedAt());
        dto.setApprovedAt(request.getApprovedAt());
        dto.setComments(request.getComments());
        
        // Set employee name
        EmployeeProfile employeeProfile = employeeProfileRepository.findByUserId(request.getEmployee().getId())
                .orElse(null);
        if (employeeProfile != null) {
            dto.setEmployeeName(employeeProfile.getFirstName() + " " + employeeProfile.getLastName());
        }
        
        // Set approver info
        if (request.getApprovedBy() != null) {
            dto.setApprovedBy(request.getApprovedBy().getId());
            EmployeeProfile approverProfile = employeeProfileRepository.findByUserId(request.getApprovedBy().getId())
                    .orElse(null);
            if (approverProfile != null) {
                dto.setApprovedByName(approverProfile.getFirstName() + " " + approverProfile.getLastName());
            }
        }
        
        return dto;
    }
}