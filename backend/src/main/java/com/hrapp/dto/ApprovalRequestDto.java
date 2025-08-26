package com.hrapp.dto;

import com.hrapp.model.AbsenceStatus;
import jakarta.validation.constraints.NotNull;

public class ApprovalRequestDto {
    @NotNull
    private AbsenceStatus status;
    
    private String comments;

    public ApprovalRequestDto() {}

    // Getters and Setters
    public AbsenceStatus getStatus() { return status; }
    public void setStatus(AbsenceStatus status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}