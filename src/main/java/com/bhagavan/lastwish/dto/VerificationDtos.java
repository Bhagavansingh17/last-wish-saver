package com.bhagavan.lastwish.dto;

import jakarta.validation.constraints.NotNull;

public final class VerificationDtos {
    private VerificationDtos() {}
    public record CreateVerificationRequest(@NotNull Long beneficiaryId, String note) {}
    public record DecisionRequest(boolean approve, String decisionNote) {}
}
