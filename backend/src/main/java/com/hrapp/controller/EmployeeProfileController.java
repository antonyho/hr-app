package com.hrapp.controller;

import com.hrapp.dto.ProfileBasicDto;
import com.hrapp.dto.ProfileDetailDto;
import com.hrapp.security.JwtUtil;
import com.hrapp.service.EmployeeProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class EmployeeProfileController {
    
    @Autowired
    private EmployeeProfileService employeeProfileService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/basic")
    public ResponseEntity<List<ProfileBasicDto>> getAllBasicProfiles(@RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        List<ProfileBasicDto> profiles = employeeProfileService.getAllBasicProfiles(currentUserId);
        return ResponseEntity.ok(profiles);
    }
    
    @GetMapping("/detailed")
    public ResponseEntity<List<ProfileDetailDto>> getAllDetailProfiles(@RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        List<ProfileDetailDto> profiles = employeeProfileService.getAllDetailProfiles(currentUserId);
        return ResponseEntity.ok(profiles);
    }
    
    @GetMapping("/{profileId}/basic")
    public ResponseEntity<ProfileBasicDto> getBasicProfile(
            @PathVariable UUID profileId,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        ProfileBasicDto profile = employeeProfileService.getBasicProfile(profileId, currentUserId);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/{profileId}/detailed")
    public ResponseEntity<ProfileDetailDto> getDetailProfile(
            @PathVariable UUID profileId,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        ProfileDetailDto profile = employeeProfileService.getDetailProfile(profileId, currentUserId);
        return ResponseEntity.ok(profile);
    }
    
    @GetMapping("/me")
    public ResponseEntity<ProfileDetailDto> getMyProfile(@RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        ProfileDetailDto profile = employeeProfileService.getMyProfile(currentUserId);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileDetailDto> updateProfile(
            @PathVariable UUID profileId,
            @Valid @RequestBody ProfileDetailDto profileDto,
            @RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        ProfileDetailDto updatedProfile = employeeProfileService.updateProfile(profileId, profileDto, currentUserId);
        return ResponseEntity.ok(updatedProfile);
    }
    
    private UUID getCurrentUserId(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtUtil.getUserIdFromToken(token);
    }
}