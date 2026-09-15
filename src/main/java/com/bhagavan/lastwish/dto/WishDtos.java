package com.bhagavan.lastwish.dto;

import jakarta.validation.constraints.NotBlank;

public final class WishDtos {
    private WishDtos() {}
    public record CreateWishRequest(@NotBlank String title, @NotBlank String content) {}
    public record UpdateWishRequest(@NotBlank String title, @NotBlank String content) {}
}
