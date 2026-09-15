package com.bhagavan.lastwish.dto;

import jakarta.validation.constraints.*;

public final class BeneficiaryDtos {
    private BeneficiaryDtos() {}
    public record CreateBeneficiaryRequest(@NotBlank String name, @Email @NotBlank String email,
                                           String relationship) {}
    public record GrantAccessRequest(Long beneficiaryId, String accessLevel) {}
}
