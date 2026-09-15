package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.VerificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRequestRepository
        extends JpaRepository<VerificationRequest, Long> {

    List<VerificationRequest>
    findByBeneficiary_IdOrderByCreatedAtDesc(Long beneficiaryId);
}