package com.bhagavan.lastwish.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name="wish_access", uniqueConstraints=@UniqueConstraint(columnNames={"wish_id","beneficiary_id"}))
@Getter @Setter @NoArgsConstructor
public class WishAccess {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    private Wish wish;

    @ManyToOne(optional=false)
    private Beneficiary beneficiary;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Enums.AccessLevel accessLevel = Enums.AccessLevel.VIEW;

    @Column(nullable=false)
    private boolean enabled = true;

    @Column(nullable=false)
    private LocalDateTime grantedAt = LocalDateTime.now();
}
