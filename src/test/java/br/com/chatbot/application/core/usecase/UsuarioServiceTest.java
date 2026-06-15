package br.com.chatbot.application.core.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import br.com.chatbot.adapter.in.controller.mapper.UsuarioDTOMapper;
import br.com.chatbot.adapter.in.controller.request.usuario.UsuarioCreateDTO;
import br.com.chatbot.adapter.in.controller.response.usuario.UsuarioDetailedResponseDTO;
import br.com.chatbot.adapter.in.controller.response.usuario.UsuarioResponseDTO;
import br.com.chatbot.adapter.out.repository.entity.UsuarioEntity;
import br.com.chatbot.adapter.out.repository.mapper.UsuarioEntityMapper;
import br.com.chatbot.adapter.out.repository.persistance.UsuarioRepository;
import br.com.chatbot.application.core.domain.model.Usuario;
import br.com.chatbot.exception.type.usuario.UsuarioNotFoundException;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository   repository;
    @Mock private UsuarioDTOMapper    dtoMapper;
    @Mock private UsuarioEntityMapper entityMapper;

    @InjectMocks
    private UsuarioService service;

    private UsuarioEntity buildEntity() {
        return new UsuarioEntity(1L, "11999999999", true);
    }

    private UsuarioEntity buildEntityInativo() {
        return new UsuarioEntity(1L, "11999999999", false);
    }

    private Usuario buildDomain() {
        // Ajuste conforme o construtor da sua classe de domínio Usuario
        return new Usuario(1L, "11999999999", true);
    }

    private UsuarioCreateDTO buildCreateDTO() {
        // Ajuste conforme o construtor/record do seu DTO
        return new UsuarioCreateDTO("11999999999");
    }

    private UsuarioResponseDTO buildResponseDTO() {
        return new UsuarioResponseDTO(1L, "11999999999");
    }

    private UsuarioDetailedResponseDTO buildDetailedResponseDTO() {
        return new UsuarioDetailedResponseDTO(1L, "11999999999", true);
    }

    // =========================================================================
    // cadastrarUsuario
    // =========================================================================
    @Nested
    @DisplayName("cadastrarUsuario")
    class CadastrarUsuario {

        @Test
        @DisplayName("deve cadastrar e retornar o DTO detalhado quando os dados são válidos")
        void deveCadastrarComSucesso() {
            UsuarioCreateDTO           dto      = buildCreateDTO();
            Usuario                    domain   = buildDomain();
            UsuarioEntity              entity   = buildEntity();
            UsuarioDetailedResponseDTO expected = buildDetailedResponseDTO();

            when(dtoMapper.toDomain(dto)).thenReturn(domain);
            when(entityMapper.toEntity(domain)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(entity);
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toDetailedResponseDTO(domain)).thenReturn(expected);

            UsuarioDetailedResponseDTO result = service.cadastrarUsuario(dto);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            verify(repository).save(entity);
        }
    }

    // =========================================================================
    // listarUsuariosAtivos
    // =========================================================================
    @Nested
    @DisplayName("listarUsuariosAtivos")
    class ListarUsuariosAtivos {

        @Test
        @DisplayName("deve retornar somente usuários ativos")
        void deveRetornarApenasAtivos() {
            Pageable               pageable = PageRequest.of(0, 10);
            UsuarioEntity          entity   = buildEntity();
            Usuario                domain   = buildDomain();
            UsuarioResponseDTO     dto      = buildResponseDTO();

            when(repository.findAllByAtivoTrue(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(dto);

            Page<UsuarioResponseDTO> result = service.listarUsuariosAtivos(pageable);

            assertThat(result).isNotEmpty();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("deve retornar página vazia quando não há usuários ativos")
        void deveRetornarPaginaVazia() {
            Pageable pageable = PageRequest.of(0, 10);
            when(repository.findAllByAtivoTrue(pageable)).thenReturn(Page.empty());

            assertThat(service.listarUsuariosAtivos(pageable)).isEmpty();
        }
    }

    // =========================================================================
    // listarUsuarios
    // =========================================================================
    @Nested
    @DisplayName("listarUsuarios")
    class ListarUsuarios {

        @Test
        @DisplayName("deve retornar todos os usuários (ativos e inativos)")
        void deveRetornarTodos() {
            Pageable                   pageable = PageRequest.of(0, 10);
            UsuarioEntity              entity   = buildEntity();
            Usuario                    domain   = buildDomain();
            UsuarioDetailedResponseDTO dto      = buildDetailedResponseDTO();

            when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity)));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toDetailedResponseDTO(domain)).thenReturn(dto);

            Page<UsuarioDetailedResponseDTO> result = service.listarUsuarios(pageable);

            assertThat(result).isNotEmpty();
        }
    }

    // =========================================================================
    // buscarUsuario
    // =========================================================================
    @Nested
    @DisplayName("buscarUsuario")
    class BuscarUsuario {

        @Test
        @DisplayName("deve retornar o DTO quando o id existe")
        void deveRetornarDTOQuandoIdExiste() {
            UsuarioEntity              entity   = buildEntity();
            Usuario                    domain   = buildDomain();
            UsuarioDetailedResponseDTO expected = buildDetailedResponseDTO();

            when(repository.findById(1L)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toDetailedResponseDTO(domain)).thenReturn(expected);

            UsuarioDetailedResponseDTO result = service.buscarUsuario(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar UsuarioNotFoundException quando o id não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarUsuario(99L))
                    .isInstanceOf(UsuarioNotFoundException.class)
                    .hasMessageContaining("99");

            verify(entityMapper, never()).toDomain(any(UsuarioEntity.class));
        }
    }

    // =========================================================================
    // deletarUsuario — soft delete (chama domain.excluir() e salva novamente)
    // =========================================================================
    @Nested
    @DisplayName("deletarUsuario")
    class DeletarUsuario {

        @Test
        @DisplayName("deve fazer soft delete: salvar com ativo=false, nunca chamar delete()")
        void deveFazerSoftDeleteComSucesso() {
            UsuarioEntity entityAtivo   = buildEntity();
            UsuarioEntity entityInativo = buildEntityInativo();
            Usuario       domain        = buildDomain();

            when(repository.findById(1L)).thenReturn(Optional.of(entityAtivo));
            when(entityMapper.toDomain(entityAtivo)).thenReturn(domain);
            when(entityMapper.toEntity(domain)).thenReturn(entityInativo);

            service.deletarUsuario(1L);

            // Deve salvar (soft delete), nunca deletar fisicamente
            verify(repository).save(entityInativo);
            verify(repository, never()).delete(any());
        }

        @Test
        @DisplayName("deve lançar UsuarioNotFoundException quando o id não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deletarUsuario(99L))
                    .isInstanceOf(UsuarioNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).save(any());
            verify(repository, never()).delete(any());
        }
    }
}