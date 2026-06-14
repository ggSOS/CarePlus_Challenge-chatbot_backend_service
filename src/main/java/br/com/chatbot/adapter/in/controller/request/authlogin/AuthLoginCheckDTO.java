package br.com.chatbot.adapter.in.controller.request.authlogin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthLoginCheckDTO(
    @Schema(
        description = "Login para autenticação",
        example = "ownerLogin")
    @NotBlank
    String login,

    @Schema(
        description = "Senha para autenticação",
        example = "owner")
    @NotBlank
    String senha) {
}
