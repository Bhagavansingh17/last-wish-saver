package com.bhagavan.lastwish.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="wishes")
@Getter @Setter @NoArgsConstructor
public class Wish {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @Column(nullable=false, length=120)
    private String title;

    @Lob @Column(nullable=false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Enums.WishStatus status = Enums.WishStatus.ACTIVE;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}
