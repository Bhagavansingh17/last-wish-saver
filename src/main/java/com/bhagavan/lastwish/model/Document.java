package com.bhagavan.lastwish.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name="documents")
@Getter @Setter @NoArgsConstructor
public class Document {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private VerificationRequest verificationRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Enums.DocumentType type;

    @Column(nullable=false)
    private String originalFileName;

    @Column(nullable=false)
    private String storageKey;

    @Column(nullable=false)
    private LocalDateTime uploadedAt = LocalDateTime.now();
}
