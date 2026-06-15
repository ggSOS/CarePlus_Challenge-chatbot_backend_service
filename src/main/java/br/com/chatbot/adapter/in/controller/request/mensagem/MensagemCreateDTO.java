package br.com.chatbot.adapter.in.controller.request.mensagem;

import br.com.chatbot.application.core.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MensagemCreateDTO(
        @Schema(
        description = "Quem enviou a Mensagem",
        example = "USER",
        allowableValues = {"USER", "SYSTEM"})
        @NotNull
        Role role,

        @Schema(
        description = "Conteúdo da Mensagem",
        example = "Olá! Poderia me ajudar?")
        @NotBlank
        String content,

        @Schema(
        description = "ID do Usuário que enviou a Mensagem",
        example = "1")
        @NotNull
        Long idUsuario) {

}
