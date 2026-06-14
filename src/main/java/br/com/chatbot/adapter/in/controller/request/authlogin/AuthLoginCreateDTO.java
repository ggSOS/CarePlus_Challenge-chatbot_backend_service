package br.com.chatbot.adapter.in.controller.request.authlogin;

import br.com.chatbot.application.core.domain.enums.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthLoginCreateDTO(
        @NotBlank String login,
        @NotBlank String senha,
        
        @Schema(
            description = "Nível de permissão do usuário no sistema",
            example = "OWNER",
            allowableValues = {"OWNER", "ADMIN", "USER" })
        @NotNull
        Perfil perfil) {

}
