package com.hrapp.controller;

import com.hrapp.dto.AbsenceRequestDto;
import com.hrapp.dto.ApprovalRequestDto;
import com.hrapp.dto.CreateAbsenceRequestDto;
import com.hrapp.security.JwtUtil;
import com.hrapp.service.AbsenceRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/absence-requests")
public class AbsenceRequestController {
    
    @Autowired
    private AbsenceRequestService absenceRequestService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping
    public ResponseEntity<AbsenceRequestDto> createAbsenceRequest(
            @Valid @RequestBody CreateAbsenceRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        try {
            UUID currentUserId = getCurrentUserId(token);
            AbsenceRequestDto createdRequest = absenceRequestService.createAbsenceRequest(requestDto, currentUserId);
            return ResponseEntity.ok(createdRequest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<AbsenceRequestDto>> getMyAbsenceRequests(@RequestHeader("Authorization") String token) {
        UUID currentUserId = getCurrentUserId(token);
        List<AbsenceRequestDto> requests = absenceRequestService.getMyAbsenceRequests(currentUserId);
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<AbsenceRequestDto>> getAllAbsenceRequests(@RequestHeader("Authorization") String token) {
        try {
            UUID currentUserId = getCurrentUserId(token);
            List<AbsenceRequestDto> requests = absenceRequestService.getAllAbsenceRequests(currentUserId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<AbsenceRequestDto>> getPendingAbsenceRequests(@RequestHeader("Authorization") String token) {
        try {
            UUID currentUserId = getCurrentUserId(token);
            List<AbsenceRequestDto> requests = absenceRequestService.getPendingAbsenceRequests(currentUserId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
    
    @GetMapping("/{requestId}")
    public ResponseEntity<AbsenceRequestDto> getAbsenceRequest(
            @PathVariable UUID requestId,
            @RequestHeader("Authorization") String token) {
        try {
            UUID currentUserId = getCurrentUserId(token);
            AbsenceRequestDto request = absenceRequestService.getAbsenceRequest(requestId, currentUserId);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
    
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<AbsenceRequestDto> approveOrRejectRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody ApprovalRequestDto approvalDto,
            @RequestHeader("Authorization") String token) {
        try {
            UUID currentUserId = getCurrentUserId(token);
            AbsenceRequestDto updatedRequest = absenceRequestService.approveOrRejectRequest(requestId, approvalDto, currentUserId);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
    
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> deleteAbsenceRequest(
            @PathVariable UUID requestId,
            @RequestHeader("Authorization") String token) {
        try {
            UUID currentUserId = getCurrentUserId(token);
            absenceRequestService.deleteAbsenceRequest(requestId, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
    
    private UUID getCurrentUserId(String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return jwtUtil.getUserIdFromToken(token);
    }
}