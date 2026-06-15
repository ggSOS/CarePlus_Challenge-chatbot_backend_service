package br.com.chatbot.adapter.in.controller;

import java.net.URI;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.chatbot.adapter.in.controller.request.authlogin.AuthLoginCreateDTO;
import br.com.chatbot.adapter.in.controller.response.authlogin.AuthLoginResponseDTO;
import br.com.chatbot.application.core.usecase.AuthLoginService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth-logins")
@Tag(name = "Auth Logins", description = "Endpoints para gerenciamento das Autenticações")
public class AuthLoginController {

    private final AuthLoginService service;

    @PostMapping
    public ResponseEntity<AuthLoginResponseDTO> cadastrarAuthLogin(
            @RequestBody
            @Valid
            AuthLoginCreateDTO createDto,

            UriComponentsBuilder uriBuilder) {
        AuthLoginResponseDTO responseDto = service.cadastrarAuthLogin(createDto);
        URI uri = uriBuilder
                .path("/auth-logins/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<AuthLoginResponseDTO>> listarAuthLogins(
            @ParameterObject
            @PageableDefault(size = 10, sort = { "login" })
            Pageable paginacao) {
        return ResponseEntity.ok(service.listarAuthLogins(paginacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthLoginResponseDTO> buscarAuthLogin(
        @Parameter(description = "ID do Login que deseja buscar", example = "1")
        @PathVariable
        Long id) {
        return ResponseEntity.ok(service.buscarAuthLogin(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAuthLogin(
        @Parameter(description = "ID do Login que deseja eliminar", example = "1")
        @PathVariable
        Long id) {
        service.deletarAuthLogin(id);
        return ResponseEntity.noContent().build();
    }

}
