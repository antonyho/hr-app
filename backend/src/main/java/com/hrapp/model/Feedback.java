package com.hrapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private EmployeeProfile profile;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "feedback_by", nullable = false)
    private User feedbackBy;

    @NotBlank
    @Column(name = "feedback_text", nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Column(name = "polished_feedback", columnDefinition = "TEXT")
    private String polishedFeedback;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Feedback() {}

    public Feedback(EmployeeProfile profile, User feedbackBy, String feedbackText) {
        this.profile = profile;
        this.feedbackBy = feedbackBy;
        this.feedbackText = feedbackText;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public EmployeeProfile getProfile() { return profile; }
    public void setProfile(EmployeeProfile profile) { this.profile = profile; }

    public User getFeedbackBy() { return feedbackBy; }
    public void setFeedbackBy(User feedbackBy) { this.feedbackBy = feedbackBy; }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }

    public String getPolishedFeedback() { return polishedFeedback; }
    public void setPolishedFeedback(String polishedFeedback) { this.polishedFeedback = polishedFeedback; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}