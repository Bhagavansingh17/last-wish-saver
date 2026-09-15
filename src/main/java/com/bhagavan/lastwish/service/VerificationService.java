package com.bhagavan.lastwish.service;

import com.bhagavan.lastwish.dto.VerificationDtos;
import com.bhagavan.lastwish.model.*;
import com.bhagavan.lastwish.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VerificationService {

    private final VerificationRequestRepository requests;
    private final BeneficiaryRepository beneficiaries;
    private final WishRepository wishes;
    private final WishAccessRepository access;
    private final AccessLogRepository logs;
    private final CurrentUser current;

    public VerificationService(
            VerificationRequestRepository r,
            BeneficiaryRepository b,
            WishRepository w,
            WishAccessRepository a,
            AccessLogRepository l,
            CurrentUser c) {

        requests = r;
        beneficiaries = b;
        wishes = w;
        access = a;
        logs = l;
        current = c;
    }

    @Transactional
    public VerificationRequest create(
            Long wishId,
            VerificationDtos.CreateVerificationRequest r) {

        Beneficiary b = beneficiaries.findById(r.beneficiaryId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Beneficiary not found"));

        Wish w = wishes.findById(wishId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Wish not found"));

        // Only the beneficiary can submit the request
        if (!b.getEmail().equalsIgnoreCase(current.get().getEmail())) {
            throw new IllegalArgumentException(
                    "Only the beneficiary can submit this request");
        }

        // Beneficiary must have access to the wish
        if (access.findByWishIdAndBeneficiaryId(wishId, b.getId()).isEmpty()) {
            throw new IllegalArgumentException(
                    "No access grant exists");
        }

        VerificationRequest v = new VerificationRequest();
        v.setBeneficiary(b);
        v.setWish(w);
        v.setNote(r.note());

        return requests.save(v);
    }

    public List<VerificationRequest> mine() {

        User user = current.get();

        Beneficiary beneficiary = beneficiaries.findByEmail(user.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No beneficiary profile found"));

        return requests.findByBeneficiary_IdOrderByCreatedAtDesc(
                beneficiary.getId());
    }

    @Transactional
    public VerificationRequest decide(
            Long id,
            VerificationDtos.DecisionRequest r) {

        VerificationRequest v = requests.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Request not found"));

        // Only wish owner can approve/reject
        if (!v.getWish().getOwner().getId().equals(current.get().getId())) {
            throw new IllegalArgumentException(
                    "Only the wish owner can decide");
        }

        v.setStatus(
                r.approve()
                        ? Enums.VerificationStatus.APPROVED
                        : Enums.VerificationStatus.REJECTED
        );

        v.setDecisionNote(r.decisionNote());
        v.setDecidedAt(LocalDateTime.now());

        return requests.save(v);
    }
}