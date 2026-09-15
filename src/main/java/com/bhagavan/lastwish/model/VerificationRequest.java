package com.bhagavan.lastwish.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name="verification_requests")
@Getter @Setter @NoArgsConstructor
public class VerificationRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Beneficiary beneficiary;

    @ManyToOne(optional=false)
    private Wish wish;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Enums.VerificationStatus status = Enums.VerificationStatus.PENDING;

    @Column(length=500)
    private String note;

    @Column(length=500)
    private String decisionNote;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime decidedAt;
}
