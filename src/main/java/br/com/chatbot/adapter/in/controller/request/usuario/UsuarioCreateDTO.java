package br.com.chatbot.adapter.in.controller.request.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UsuarioCreateDTO(
        @Schema(
        description = "Número de celular do usuário que será registrado",
        example = "11912123434")
        @NotBlank
        @Pattern(regexp = "\\d{9,13}")
        String celular) {

}
