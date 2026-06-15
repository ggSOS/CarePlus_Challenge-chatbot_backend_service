package br.com.chatbot.adapter.in.controller.mapper;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import br.com.chatbot.adapter.in.controller.request.usuario.UsuarioCreateDTO;
import br.com.chatbot.adapter.in.controller.response.usuario.UsuarioDetailedResponseDTO;
import br.com.chatbot.adapter.in.controller.response.usuario.UsuarioResponseDTO;
import br.com.chatbot.application.core.domain.model.Usuario;

@Component
@Service
public class UsuarioDTOMapper {
    public Usuario toDomain(UsuarioCreateDTO dados) {
        return new Usuario(
                dados);
    }

    public UsuarioResponseDTO toResponseDTO(Usuario dados) {
        return new UsuarioResponseDTO(
                dados.getId(),
                dados.getCelular());
    }

    public UsuarioDetailedResponseDTO toDetailedResponseDTO(Usuario dados) {
        return new UsuarioDetailedResponseDTO(
                dados.getId(),
                dados.getCelular(),
                dados.getAtivo());
    }
}
