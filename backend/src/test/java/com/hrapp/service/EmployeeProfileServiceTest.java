package com.hrapp.service;

import com.hrapp.dto.ProfileBasicDto;
import com.hrapp.dto.ProfileDetailDto;
import com.hrapp.model.EmployeeProfile;
import com.hrapp.model.User;
import com.hrapp.model.UserRole;
import com.hrapp.repository.EmployeeProfileRepository;
import com.hrapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeProfileServiceTest {

    @Mock
    private EmployeeProfileRepository employeeProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeProfileService employeeProfileService;

    private User managerUser;
    private User employeeUser;
    private EmployeeProfile testProfile;

    @BeforeEach
    void setUp() {
        managerUser = new User("manager@test.com", "hash", UserRole.MANAGER);
        managerUser.setId(UUID.randomUUID());

        employeeUser = new User("employee@test.com", "hash", UserRole.EMPLOYEE);
        employeeUser.setId(UUID.randomUUID());

        testProfile = new EmployeeProfile(employeeUser, "EMP001", "John", "Doe");
        testProfile.setId(UUID.randomUUID());
        testProfile.setDepartment("Engineering");
        testProfile.setPosition("Developer");
        testProfile.setPhone("123-456-7890");
    }

    @Test
    void getAllBasicProfiles_ReturnsAllProfiles() {
        // Arrange
        List<EmployeeProfile> profiles = Arrays.asList(testProfile);
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));
        when(employeeProfileRepository.findAll()).thenReturn(profiles);

        // Act
        List<ProfileBasicDto> result = employeeProfileService.getAllBasicProfiles(employeeUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ProfileBasicDto dto = result.get(0);
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals("EMP001", dto.getEmployeeId());
        assertEquals("Engineering", dto.getDepartment());
        assertEquals("Developer", dto.getPosition());
    }

    @Test
    void getAllDetailProfiles_AsManager_ReturnsAllDetailedProfiles() {
        // Arrange
        List<EmployeeProfile> profiles = Arrays.asList(testProfile);
        when(userRepository.findById(managerUser.getId())).thenReturn(Optional.of(managerUser));
        when(employeeProfileRepository.findAll()).thenReturn(profiles);

        // Act
        List<ProfileDetailDto> result = employeeProfileService.getAllDetailProfiles(managerUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        ProfileDetailDto dto = result.get(0);
        assertEquals("John", dto.getFirstName());
        assertEquals("123-456-7890", dto.getPhone()); // Sensitive data included
    }

    @Test
    void getAllDetailProfiles_AsEmployee_ThrowsAccessDeniedException() {
        // Arrange
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act & Assert
        assertThrows(AccessDeniedException.class, 
                () -> employeeProfileService.getAllDetailProfiles(employeeUser.getId()));
    }

    @Test
    void getBasicProfile_ValidAccess_ReturnsBasicProfile() {
        // Arrange
        when(employeeProfileRepository.findById(testProfile.getId())).thenReturn(Optional.of(testProfile));
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act
        ProfileBasicDto result = employeeProfileService.getBasicProfile(testProfile.getId(), employeeUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("EMP001", result.getEmployeeId());
    }

    @Test
    void getDetailProfile_AsOwner_ReturnsDetailedProfile() {
        // Arrange
        when(employeeProfileRepository.findById(testProfile.getId())).thenReturn(Optional.of(testProfile));
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));

        // Act
        ProfileDetailDto result = employeeProfileService.getDetailProfile(testProfile.getId(), employeeUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("123-456-7890", result.getPhone());
    }

    @Test
    void getDetailProfile_AsManager_ReturnsDetailedProfile() {
        // Arrange
        when(employeeProfileRepository.findById(testProfile.getId())).thenReturn(Optional.of(testProfile));
        when(userRepository.findById(managerUser.getId())).thenReturn(Optional.of(managerUser));

        // Act
        ProfileDetailDto result = employeeProfileService.getDetailProfile(testProfile.getId(), managerUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("123-456-7890", result.getPhone());
    }

    @Test
    void getMyProfile_ReturnsOwnProfile() {
        // Arrange
        when(employeeProfileRepository.findByUserId(employeeUser.getId())).thenReturn(Optional.of(testProfile));

        // Act
        ProfileDetailDto result = employeeProfileService.getMyProfile(employeeUser.getId());

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("123-456-7890", result.getPhone());
    }

    @Test
    void updateProfile_AsOwner_UpdatesSuccessfully() {
        // Arrange
        ProfileDetailDto updateDto = new ProfileDetailDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setDepartment("Marketing");
        updateDto.setPosition("Manager");

        when(employeeProfileRepository.findById(testProfile.getId())).thenReturn(Optional.of(testProfile));
        when(userRepository.findById(employeeUser.getId())).thenReturn(Optional.of(employeeUser));
        when(employeeProfileRepository.save(any(EmployeeProfile.class))).thenReturn(testProfile);

        // Act
        ProfileDetailDto result = employeeProfileService.updateProfile(testProfile.getId(), updateDto, employeeUser.getId());

        // Assert
        assertNotNull(result);
        verify(employeeProfileRepository).save(testProfile);
        assertEquals("Jane", testProfile.getFirstName());
        assertEquals("Smith", testProfile.getLastName());
    }

    @Test
    void getBasicProfile_ProfileNotFound_ThrowsException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(employeeProfileRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, 
                () -> employeeProfileService.getBasicProfile(nonExistentId, employeeUser.getId()));
    }
}