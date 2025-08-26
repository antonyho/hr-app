package com.hrapp.service;

import com.hrapp.dto.ProfileBasicDto;
import com.hrapp.dto.ProfileDetailDto;
import com.hrapp.exception.ProfileNotFoundException;
import com.hrapp.exception.UserNotFoundException;
import com.hrapp.model.EmployeeProfile;
import com.hrapp.model.User;
import com.hrapp.model.UserRole;
import com.hrapp.repository.EmployeeProfileRepository;
import com.hrapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeProfileService {
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<ProfileBasicDto> getAllBasicProfiles(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + currentUserId));
        
        return employeeProfileRepository.findAll()
                .stream()
                .map(this::convertToBasicDto)
                .collect(Collectors.toList());
    }
    
    public List<ProfileDetailDto> getAllDetailProfiles(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + currentUserId));
        
        // Only managers can see all detailed profiles
        if (currentUser.getRole() != UserRole.MANAGER) {
            throw new AccessDeniedException("Access denied");
        }
        
        return employeeProfileRepository.findAll()
                .stream()
                .map(this::convertToDetailDto)
                .collect(Collectors.toList());
    }
    
    public ProfileBasicDto getBasicProfile(UUID profileId, UUID currentUserId) {
        EmployeeProfile profile = employeeProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + currentUserId));
        
        // Check basic access permissions
        if (!canAccessProfile(profile, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }
        
        return convertToBasicDto(profile);
    }
    
    public ProfileDetailDto getDetailProfile(UUID profileId, UUID currentUserId) {
        EmployeeProfile profile = employeeProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + currentUserId));
        
        // Check detailed access permissions (manager or owner only)
        if (!canAccessDetailedProfile(profile, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }
        
        return convertToDetailDto(profile);
    }
    
    public ProfileDetailDto getMyProfile(UUID currentUserId) {
        EmployeeProfile profile = employeeProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found for user ID: " + currentUserId));
        
        return convertToDetailDto(profile);
    }
    
    public ProfileDetailDto updateProfile(UUID profileId, ProfileDetailDto profileDto, UUID currentUserId) {
        EmployeeProfile existingProfile = employeeProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException("Profile not found with ID: " + profileId));
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + currentUserId));
        
        // Check update permissions
        if (!canUpdateProfile(existingProfile, currentUser)) {
            throw new AccessDeniedException("Access denied");
        }
        
        updateProfileFromDto(existingProfile, profileDto);
        EmployeeProfile savedProfile = employeeProfileRepository.save(existingProfile);
        
        return convertToDetailDto(savedProfile);
    }
    
    private boolean canAccessProfile(EmployeeProfile profile, User currentUser) {
        // Managers can access all profiles
        if (currentUser.getRole() == UserRole.MANAGER) {
            return true;
        }
        
        // Users can access their own profile
        if (profile.getUser().getId().equals(currentUser.getId())) {
            return true;
        }
        
        // Employees can access coworker basic profiles
        return currentUser.getRole() == UserRole.EMPLOYEE;
    }
    
    private boolean canAccessDetailedProfile(EmployeeProfile profile, User currentUser) {
        // Managers can access all detailed profiles
        if (currentUser.getRole() == UserRole.MANAGER) {
            return true;
        }
        
        // Users can only access their own detailed profile
        return profile.getUser().getId().equals(currentUser.getId());
    }
    
    private boolean canUpdateProfile(EmployeeProfile profile, User currentUser) {
        // Managers can update all profiles
        if (currentUser.getRole() == UserRole.MANAGER) {
            return true;
        }
        
        // Users can only update their own profile
        return profile.getUser().getId().equals(currentUser.getId());
    }
    
    private ProfileBasicDto convertToBasicDto(EmployeeProfile profile) {
        ProfileBasicDto dto = new ProfileBasicDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setEmployeeId(profile.getEmployeeId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setDepartment(profile.getDepartment());
        dto.setPosition(profile.getPosition());
        dto.setManagerId(profile.getManager() != null ? profile.getManager().getId() : null);
        
        // Add manager name if available
        if (profile.getManager() != null) {
            EmployeeProfile managerProfile = employeeProfileRepository.findByUserId(profile.getManager().getId())
                    .orElse(null);
            if (managerProfile != null) {
                dto.setManagerName(managerProfile.getFirstName() + " " + managerProfile.getLastName());
            }
        }
        
        return dto;
    }
    
    private ProfileDetailDto convertToDetailDto(EmployeeProfile profile) {
        ProfileDetailDto dto = new ProfileDetailDto();
        dto.setId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setEmployeeId(profile.getEmployeeId());
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setDepartment(profile.getDepartment());
        dto.setPosition(profile.getPosition());
        dto.setHireDate(profile.getHireDate());
        dto.setPhone(profile.getPhone());
        dto.setAddress(profile.getAddress());
        dto.setEmergencyContactName(profile.getEmergencyContactName());
        dto.setEmergencyContactPhone(profile.getEmergencyContactPhone());
        dto.setManagerId(profile.getManager() != null ? profile.getManager().getId() : null);
        dto.setCreatedAt(profile.getCreatedAt());
        dto.setUpdatedAt(profile.getUpdatedAt());
        
        // Add manager name if available
        if (profile.getManager() != null) {
            EmployeeProfile managerProfile = employeeProfileRepository.findByUserId(profile.getManager().getId())
                    .orElse(null);
            if (managerProfile != null) {
                dto.setManagerName(managerProfile.getFirstName() + " " + managerProfile.getLastName());
            }
        }
        
        return dto;
    }
    
    private void updateProfileFromDto(EmployeeProfile profile, ProfileDetailDto dto) {
        profile.setFirstName(dto.getFirstName());
        profile.setLastName(dto.getLastName());
        profile.setDepartment(dto.getDepartment());
        profile.setPosition(dto.getPosition());
        profile.setHireDate(dto.getHireDate());
        profile.setPhone(dto.getPhone());
        profile.setAddress(dto.getAddress());
        profile.setEmergencyContactName(dto.getEmergencyContactName());
        profile.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        
        // Only managers can change manager assignment
        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId()).orElse(null);
            profile.setManager(manager);
        }
    }
}