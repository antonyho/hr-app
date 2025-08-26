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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbsenceRequestServiceTest {

    @Mock
    private AbsenceRequestRepository absenceRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeProfileRepository employeeProfileRepository;

    @InjectMocks
    private AbsenceRequestService absenceRequestService;

    private User managerUser;
    private User employeeUser;
    private EmployeeProfile employeeProfile;
    private AbsenceRequest testRequest;

    @BeforeEach
    void setUp() {
        managerUser = new User("manager@test.com", "hash", UserRole.MANAGER);
        managerUser.setId(UUID.randomUUID());

        employeeUser = new User("employee@test.com", "hash", UserRole.EMPLOYEE);
        employeeUser.setId(UUID.randomUUID());

        employeeProfile = new EmployeeProfile(employeeUser, "EMP001", "John", "Doe");
        employeeProfile.setId(UUID.randomUUID());

        testRequest = new AbsenceRequest(
            employeeUser,
            LocalDate.of(2024, 1, 15),
            LocalDate.of(2024, 1, 17),
            "Vacation"
        );
        testRequest.setId(UUID.randomUUID());
    }

    @Test
    void createAbsenceRequest_ValidRequest_ReturnsDto() {
        // Arrange
        CreateAbsenceRequestDto requestDto = new CreateAbsenceRequestDto();
        requestDto.setStartDate(LocalDate.of(2024, 1, 15));
        requestDto.setEndDate(LocalDate.of(2024, 1, 17));
        requestDto.setReason("Vacation");

        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));
        when(absenceRequestRepository.save(any(AbsenceRequest.class))).thenReturn(testRequest);
        when(employeeProfileRepository.findByUserId(employeeUser.getId())).thenReturn(Optional.of(employeeProfile));

        // Act
        AbsenceRequestDto result = absenceRequestService.createAbsenceRequest(requestDto, employeeUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(testRequest.getId(), result.getId());
        assertEquals("Vacation", result.getReason());
        assertEquals(AbsenceStatus.PENDING, result.getStatus());
        verify(absenceRequestRepository).save(any(AbsenceRequest.class));
    }

    @Test
    void createAbsenceRequest_EndDateBeforeStartDate_ThrowsException() {
        // Arrange
        CreateAbsenceRequestDto requestDto = new CreateAbsenceRequestDto();
        requestDto.setStartDate(LocalDate.of(2024, 1, 17));
        requestDto.setEndDate(LocalDate.of(2024, 1, 15)); // End before start

        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> absenceRequestService.createAbsenceRequest(requestDto, employeeUser.getId()));
        assertEquals("End date cannot be before start date", exception.getMessage());
    }

    @Test
    void getMyAbsenceRequests_ReturnsUserRequests() {
        // Arrange
        List<AbsenceRequest> requests = Arrays.asList(testRequest);
        when(absenceRequestRepository.findByEmployeeId(employeeUser.getId())).thenReturn(requests);
        when(employeeProfileRepository.findByUserId(employeeUser.getId())).thenReturn(Optional.of(employeeProfile));

        // Act
        List<AbsenceRequestDto> result = absenceRequestService.getMyAbsenceRequests(employeeUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRequest.getId(), result.get(0).getId());
    }

    @Test
    void getAllAbsenceRequests_AsManager_ReturnsAllRequests() {
        // Arrange
        List<AbsenceRequest> requests = Arrays.asList(testRequest);
        when(userRepository.findById(managerUser.getId())).thenReturn(Optional.of(managerUser));
        when(absenceRequestRepository.findAll()).thenReturn(requests);
        when(employeeProfileRepository.findByUserId(employeeUser.getId())).thenReturn(Optional.of(employeeProfile));

        // Act
        List<AbsenceRequestDto> result = absenceRequestService.getAllAbsenceRequests(managerUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRequest.getId(), result.get(0).getId());
    }

    @Test
    void getAllAbsenceRequests_AsEmployee_ThrowsAccessDeniedException() {
        // Arrange
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> absenceRequestService.getAllAbsenceRequests(employeeUser.getId()));
    }

    @Test
    void approveOrRejectRequest_AsManager_UpdatesRequest() {
        // Arrange
        ApprovalRequestDto approvalDto = new ApprovalRequestDto();
        approvalDto.setStatus(AbsenceStatus.APPROVED);
        approvalDto.setComments("Approved for vacation");

        when(absenceRequestRepository.findById(testRequest.getId())).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(managerUser.getId())).thenReturn(Optional.of(managerUser));
        when(absenceRequestRepository.save(any(AbsenceRequest.class))).thenReturn(testRequest);
        when(employeeProfileRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(employeeProfile));

        // Act
        AbsenceRequestDto result = absenceRequestService.approveOrRejectRequest(
                testRequest.getId(), approvalDto, managerUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(AbsenceStatus.APPROVED, testRequest.getStatus());
        assertEquals("Approved for vacation", testRequest.getComments());
        assertNotNull(testRequest.getApprovedAt());
        verify(absenceRequestRepository).save(testRequest);
    }

    @Test
    void approveOrRejectRequest_AsEmployee_ThrowsAccessDeniedException() {
        // Arrange
        ApprovalRequestDto approvalDto = new ApprovalRequestDto();
        approvalDto.setStatus(AbsenceStatus.APPROVED);

        when(absenceRequestRepository.findById(testRequest.getId())).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> absenceRequestService.approveOrRejectRequest(
                        testRequest.getId(), approvalDto, employeeUser.getId()));
    }

    @Test
    void deleteAbsenceRequest_OwnPendingRequest_DeletesSuccessfully() {
        // Arrange
        when(absenceRequestRepository.findById(testRequest.getId())).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act
        absenceRequestService.deleteAbsenceRequest(testRequest.getId(), employeeUser.getId());

        // Assert
        verify(absenceRequestRepository).delete(testRequest);
    }

    @Test
    void deleteAbsenceRequest_ApprovedRequest_ThrowsAccessDeniedException() {
        // Arrange
        testRequest.setStatus(AbsenceStatus.APPROVED);
        when(absenceRequestRepository.findById(testRequest.getId())).thenReturn(Optional.of(testRequest));
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> absenceRequestService.deleteAbsenceRequest(testRequest.getId(), employeeUser.getId()));
    }

    @Test
    void getPendingAbsenceRequests_AsManager_ReturnsPendingRequests() {
        // Arrange
        List<AbsenceRequest> pendingRequests = Arrays.asList(testRequest);
        when(userRepository.findById(managerUser.getId())).thenReturn(Optional.of(managerUser));
        when(absenceRequestRepository.findByStatus(AbsenceStatus.PENDING)).thenReturn(pendingRequests);
        when(employeeProfileRepository.findByUserId(employeeUser.getId())).thenReturn(Optional.of(employeeProfile));

        // Act
        List<AbsenceRequestDto> result = absenceRequestService.getPendingAbsenceRequests(managerUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(AbsenceStatus.PENDING, result.get(0).getStatus());
    }
}