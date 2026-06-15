package br.com.chatbot.application.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.chatbot.adapter.in.controller.mapper.MensagemDTOMapper;
import br.com.chatbot.adapter.in.controller.request.mensagem.MensagemCreateDTO;
import br.com.chatbot.adapter.in.controller.response.mensagem.MensagemResponseDTO;
import br.com.chatbot.adapter.out.repository.entity.MensagemEntity;
import br.com.chatbot.adapter.out.repository.entity.UsuarioEntity;
import br.com.chatbot.adapter.out.repository.mapper.MensagemEntityMapper;
import br.com.chatbot.adapter.out.repository.mapper.UsuarioEntityMapper;
import br.com.chatbot.adapter.out.repository.persistance.MensagemRepository;
import br.com.chatbot.adapter.out.repository.persistance.UsuarioRepository;
import br.com.chatbot.application.core.domain.enums.Role;
import br.com.chatbot.application.core.domain.model.Mensagem;
import br.com.chatbot.application.core.domain.model.Usuario;
import br.com.chatbot.exception.type.mensagem.MensagemNotFoundException;
import br.com.chatbot.exception.type.usuario.UsuarioNotFoundException;

@ExtendWith(MockitoExtension.class)
class MensagemServiceTest {

    @Mock private UsuarioRepository    usuarioRepository;
    @Mock private UsuarioEntityMapper  usuarioEntityMapper;
    @Mock private MensagemRepository   repository;
    @Mock private MensagemDTOMapper    dtoMapper;
    @Mock private MensagemEntityMapper entityMapper;

    @InjectMocks
    private MensagemService service;

    private UsuarioEntity buildUsuarioEntity() {
        return new UsuarioEntity(1L, "11999999999", true);
    }

    private Usuario buildUsuarioDomain() {
        return new Usuario(1L, "11999999999", true);
    }

    private MensagemCreateDTO buildCreateDTO() {
        return new MensagemCreateDTO(Role.USER, "Olá, mundo!", 1L);
    }

    private MensagemEntity buildMensagemEntity() {
        return new MensagemEntity(10L, Role.USER, "Olá, mundo!", LocalDateTime.now(), buildUsuarioEntity());
    }

    private Mensagem buildMensagemDomain() {
        return new Mensagem(10L, Role.USER, "Olá, mundo!", LocalDateTime.now(), buildUsuarioDomain());
    }

    private MensagemResponseDTO buildResponseDTO() {
        return new MensagemResponseDTO(10L, Role.USER, "Olá, mundo!", LocalDateTime.now(), "11965654343");
    }

    // =========================================================================
    // cadastrarMensagem
    // =========================================================================
    @Nested
    @DisplayName("cadastrarMensagem")
    class CadastrarMensagem {

        @Test
        @DisplayName("deve cadastrar e retornar o DTO quando o usuário existe")
        void deveCadastrarComSucesso() {
            MensagemCreateDTO   dto           = buildCreateDTO();
            UsuarioEntity       usuarioEntity = buildUsuarioEntity();
            Usuario             usuarioDomain = buildUsuarioDomain();
            MensagemEntity      entity        = buildMensagemEntity();
            Mensagem            domain        = buildMensagemDomain();
            MensagemResponseDTO expected      = buildResponseDTO();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEntity));
            when(usuarioEntityMapper.toDomain(usuarioEntity)).thenReturn(usuarioDomain);
            when(entityMapper.toEntity(any(Mensagem.class))).thenReturn(entity);
            when(repository.save(entity)).thenReturn(entity);
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(expected);

            MensagemResponseDTO result = service.cadastrarMensagem(dto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(10L);
            verify(repository).save(any(MensagemEntity.class));
        }

        @Test
        @DisplayName("deve lançar UsuarioNotFoundException quando o usuário não existe")
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            MensagemCreateDTO dto = buildCreateDTO();
            when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.cadastrarMensagem(dto))
                    .isInstanceOf(UsuarioNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado");

            verify(repository, never()).save(any());
        }
    }

    // =========================================================================
    // listarMensagens
    // =========================================================================
    @Nested
    @DisplayName("listarMensagens")
    class ListarMensagens {

        @Test
        @DisplayName("deve retornar página com registros")
        void deveRetornarPaginaComRegistros() {
            Pageable            pageable = PageRequest.of(0, 10);
            MensagemEntity      entity   = buildMensagemEntity();
            Mensagem            domain   = buildMensagemDomain();
            MensagemResponseDTO dto      = buildResponseDTO();

            when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(dto);

            Page<MensagemResponseDTO> result = service.listarMensagens(pageable);

            assertThat(result).isNotEmpty();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("deve retornar página vazia quando não há registros")
        void deveRetornarPaginaVazia() {
            Pageable pageable = PageRequest.of(0, 10);
            when(repository.findAll(pageable)).thenReturn(Page.empty());

            assertThat(service.listarMensagens(pageable)).isEmpty();
        }
    }

    // =========================================================================
    // listarMensagensPorUsuario
    // =========================================================================
    @Nested
    @DisplayName("listarMensagensPorUsuario")
    class ListarMensagensPorUsuario {

        @Test
        @DisplayName("deve retornar página quando o usuário existe")
        void deveRetornarPaginaQuandoUsuarioExiste() {
            Pageable            pageable = PageRequest.of(0, 10);
            MensagemEntity      entity   = buildMensagemEntity();
            Mensagem            domain   = buildMensagemDomain();
            MensagemResponseDTO dto      = buildResponseDTO();

            when(usuarioRepository.existsById(1L)).thenReturn(true);
            when(repository.findAllByUsuarioId(1L, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(dto);

            Page<MensagemResponseDTO> result = service.listarMensagensPorUsuario(1L, pageable);

            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("deve lançar UsuarioNotFoundException quando o usuário não existe")
        void deveLancarExcecaoQuandoUsuarioNaoExiste() {
            when(usuarioRepository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.listarMensagensPorUsuario(99L, PageRequest.of(0, 10)))
                    .isInstanceOf(UsuarioNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).findAllByUsuarioId(any(), any());
        }
    }

    // =========================================================================
    // buscarMensagem
    // =========================================================================
    @Nested
    @DisplayName("buscarMensagem")
    class BuscarMensagem {

        @Test
        @DisplayName("deve retornar o DTO quando o id existe")
        void deveRetornarDTOQuandoIdExiste() {
            MensagemEntity      entity   = buildMensagemEntity();
            Mensagem            domain   = buildMensagemDomain();
            MensagemResponseDTO expected = buildResponseDTO();

            when(repository.findById(10L)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(expected);

            MensagemResponseDTO result = service.busacarMensagem(10L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(10L);
        }

        @Test
        @DisplayName("deve lançar MensagemNotFoundException quando o id não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.busacarMensagem(99L))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }
}