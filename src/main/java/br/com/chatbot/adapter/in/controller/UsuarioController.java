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

import br.com.chatbot.adapter.in.controller.request.usuario.UsuarioCreateDTO;
import br.com.chatbot.adapter.in.controller.response.usuario.UsuarioDetailedResponseDTO;
import br.com.chatbot.adapter.in.controller.response.usuario.UsuarioResponseDTO;
import br.com.chatbot.application.core.usecase.UsuarioService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Endpoints para gerenciamento dos Usuarios")
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioDetailedResponseDTO> cadastrarUsuario(
            @RequestBody
            @Valid
            UsuarioCreateDTO createDto,

            UriComponentsBuilder uriBuilder) {
        UsuarioDetailedResponseDTO responseDto = service.cadastrarUsuario(createDto);
        URI uri = uriBuilder
                .path("/usuarios/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listarUsuariosAtivos(
            @ParameterObject
            @PageableDefault(size = 10, sort = { "celular" })
            Pageable paginacao) {
        return ResponseEntity.ok(service.listarUsuariosAtivos(paginacao));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<UsuarioDetailedResponseDTO>> listarUsuarios(
            @ParameterObject
            @PageableDefault(size = 10, sort = { "celular" })
            Pageable paginacao) {
        return ResponseEntity.ok(service.listarUsuarios(paginacao));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UsuarioDetailedResponseDTO> buscarUsuario(
        @Parameter(description = "ID do Usuário que deseja buscar", example = "1")
        @PathVariable
        Long id) {
        return ResponseEntity.ok(service.buscarUsuario(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(
        @Parameter(description = "ID do Usuário que deseja desativar", example = "1")
        @PathVariable
        Long id) {
        service.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
