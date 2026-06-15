package br.com.chatbot.adapter.in.controller.request.authlogin;

import br.com.chatbot.application.core.domain.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthLoginCreateDTO(
        @Schema(
        description = "Login para autenticação",
        example = "adminExample")
        @NotBlank
        String login,

        @Schema(
        description = "Senha para autenticação(example)",
        example = "$2a$10$ScUnzqa4iSF5ZG8NTj9fNOiq/e2cfUNfyrf3Ul5QaiXoPnfyRveVK")
        @NotBlank
        String senha,

        @Schema(
            description = "Nível de permissão do usuário no sistema",
            example = "ADMIN",
            allowableValues = {"OWNER", "ADMIN", "USER" })
        @NotNull
        Perfil perfil) {

}
