package br.com.chatbot.application.core.domain.model;

import br.com.chatbot.adapter.in.controller.request.usuario.UsuarioCreateDTO;

public class Usuario {
    private Long id;
    private String celular;
    private Boolean ativo;

    public Usuario() {
    }

    public Usuario(
            Long id,
            String celular,
            Boolean ativo) {
        this.id = id;
        this.celular = celular;
        this.ativo = ativo;
    }

    public Usuario(UsuarioCreateDTO dados) {
        this.celular = dados.celular();
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public String getCelular() {
        return celular;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void excluir() {
        this.celular = "unknown";
        this.ativo = false;
    }
}
