package br.com.chatbot.adapter.in.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.chatbot.adapter.in.controller.request.mensagem.MensagemCreateDTO;
import br.com.chatbot.adapter.in.controller.response.mensagem.MensagemResponseDTO;
import br.com.chatbot.application.core.usecase.MensagemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mensagens")
@Tag(name = "Mensagens", description = "Endpoints para gerenciamento das Mensagens")
public class MensagemController {
    private final MensagemService service;

    @PostMapping
    public ResponseEntity<MensagemResponseDTO> cadastrarMensagem(
            @RequestBody @Valid MensagemCreateDTO createDto,
            UriComponentsBuilder uriBuilder) {
        MensagemResponseDTO responseDto = service.cadastrarMensagem(createDto);
        URI uri = uriBuilder
                .path("/mensagens/{id}")
                .buildAndExpand(responseDto.id())
                .toUri();
        return ResponseEntity
                .created(uri)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<MensagemResponseDTO>> listarMensagens(
            @PageableDefault(size = 10, sort = { "celularUsuario" }) Pageable paginacao) {
        return ResponseEntity.ok(service.listarMensagens(paginacao));
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<Page<MensagemResponseDTO>> listarMensagensPorUsuario(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = { "nome" }) Pageable paginacao) {
        return ResponseEntity.ok(service.listarMensagensPorUsuario(id, paginacao));
    }

    @GetMapping("/mensagem/{id}")
    public ResponseEntity<MensagemResponseDTO> busacarMensagem(@PathVariable Long id) {
        return ResponseEntity.ok(service.busacarMensagem(id));
    }
}
