package com.bhagavan.lastwish.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name="access_logs")
@Getter @Setter @NoArgsConstructor
public class AccessLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User actor;

    private Long wishId;

    @Column(nullable=false, length=80)
    private String action;

    @Column(length=500)
    private String details;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
