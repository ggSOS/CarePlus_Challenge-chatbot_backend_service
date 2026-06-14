package br.com.chatbot.adapter.in.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.chatbot.adapter.in.controller.request.authlogin.AuthLoginCheckDTO;
import br.com.chatbot.application.core.domain.model.AuthLogin;
import br.com.chatbot.config.security.dto.DadosTokenJWT;
import br.com.chatbot.config.security.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
@Tag(name = "- Autenticação", description = "Autenticação para acesso aos Endpoint")
public class AutenticacaoController {

    private final AuthenticationManager manager;

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid AuthLoginCheckDTO dados){
        UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(
                    dados.login(),
                    dados.senha());
        Authentication authentication = manager.authenticate(token);
        String tokenJWT = tokenService.gerarToken((AuthLogin) authentication.getPrincipal());
        return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));
    }
}
