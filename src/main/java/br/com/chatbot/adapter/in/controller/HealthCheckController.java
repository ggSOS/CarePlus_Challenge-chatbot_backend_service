package br.com.chatbot.adapter.in.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/health-check")
@Tag(name = "- Health-Check", description = "Checagem do funcionamento da API")

public class HealthCheckController {
    
    @GetMapping
    public String healthCheck(){
        return "API funcionando como esperado!";
    }
}
