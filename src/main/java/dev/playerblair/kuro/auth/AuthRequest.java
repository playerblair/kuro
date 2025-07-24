package dev.playerblair.kuro.auth;

import jakarta.validation.constraints.NotEmpty;

public record AuthRequest(
        @NotEmpty String username,
        @NotEmpty String password
) {
}
