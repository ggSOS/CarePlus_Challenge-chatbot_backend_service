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

import br.com.chatbot.adapter.in.controller.mapper.AuthLoginDTOMapper;
import br.com.chatbot.adapter.in.controller.request.authlogin.AuthLoginCreateDTO;
import br.com.chatbot.adapter.in.controller.response.authlogin.AuthLoginResponseDTO;
import br.com.chatbot.adapter.out.repository.entity.AuthLoginEntity;
import br.com.chatbot.adapter.out.repository.mapper.AuthLoginEntityMapper;
import br.com.chatbot.adapter.out.repository.persistance.AuthLoginRepository;
import br.com.chatbot.application.core.domain.enums.Perfil;
import br.com.chatbot.application.core.domain.model.AuthLogin;
import br.com.chatbot.exception.type.loginauth.AuthLoginNotFoundException;

@ExtendWith(MockitoExtension.class)
class AuthLoginServiceTest {

    // -------------------------------------------------------------------------
    // Mocks das dependências injetadas no service
    // -------------------------------------------------------------------------
    @Mock
    private AuthLoginRepository repository;

    @Mock
    private AuthLoginDTOMapper dtoMapper;

    @Mock
    private AuthLoginEntityMapper entityMapper;

    @InjectMocks
    private AuthLoginService service;

    // -------------------------------------------------------------------------
    // Objetos reutilizáveis nos testes
    // -------------------------------------------------------------------------
    private AuthLoginCreateDTO buildCreateDTO() {
        return new AuthLoginCreateDTO("admin", "$2a$10$z1uaGK/Ab6ju.VKZizz6He.r1dhgtTiTbh1lJjxlYnnVRN/y4d2ka", Perfil.ADMIN);
    }

    private AuthLogin buildDomain() {
        return new AuthLogin(null, "admin", "$2a$10$z1uaGK/Ab6ju.VKZizz6He.r1dhgtTiTbh1lJjxlYnnVRN/y4d2ka", Perfil.ADMIN);
    }

    private AuthLoginEntity buildEntity() {
        return new AuthLoginEntity(1L,"admin", "$2a$10$z1uaGK/Ab6ju.VKZizz6He.r1dhgtTiTbh1lJjxlYnnVRN/y4d2ka", Perfil.ADMIN);
    }

    private AuthLoginResponseDTO buildResponseDTO() {
        return new AuthLoginResponseDTO(1L, "admin", "senha123", Perfil.ADMIN);
    }

    // =========================================================================
    // cadastrarAuthLogin
    // =========================================================================
    @Nested
    @DisplayName("cadastrarAuthLogin")
    class CadastrarAuthLogin {

        @Test
        @DisplayName("deve cadastrar e retornar o DTO quando os dados são válidos")
        void deveCadastrarComSucesso() {
            // Arrange
            AuthLoginCreateDTO dto    = buildCreateDTO();
            AuthLogin          domain = buildDomain();
            AuthLoginEntity    entity = buildEntity();
            AuthLoginResponseDTO expected = buildResponseDTO();

            when(dtoMapper.toDomain(dto)).thenReturn(domain);
            when(entityMapper.toEntity(domain)).thenReturn(entity);
            when(repository.save(entity)).thenReturn(entity);
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(expected);

            // Act
            AuthLoginResponseDTO result = service.cadastrarAuthLogin(dto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.login()).isEqualTo("admin");

            verify(repository).save(entity);
        }
    }

    // =========================================================================
    // listarAuthLogins
    // =========================================================================
    @Nested
    @DisplayName("listarAuthLogins")
    class ListarAuthLogins {

        @Test
        @DisplayName("deve retornar página de DTOs quando há registros")
        void deveRetornarPaginaComRegistros() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            AuthLoginEntity    entity   = buildEntity();
            AuthLogin          domain   = buildDomain();
            AuthLoginResponseDTO dto    = buildResponseDTO();

            Page<AuthLoginEntity> pageEntity = new PageImpl<>(List.of(entity));

            when(repository.findAll(pageable)).thenReturn(pageEntity);
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(dto);

            // Act
            Page<AuthLoginResponseDTO> result = service.listarAuthLogins(pageable);

            // Assert
            assertThat(result).isNotEmpty();
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("deve retornar página vazia quando não há registros")
        void deveRetornarPaginaVazia() {
            Pageable pageable = PageRequest.of(0, 10);
            when(repository.findAll(pageable)).thenReturn(Page.empty());

            Page<AuthLoginResponseDTO> result = service.listarAuthLogins(pageable);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // buscarAuthLogin
    // =========================================================================
    @Nested
    @DisplayName("buscarAuthLogin")
    class BuscarAuthLogin {

        @Test
        @DisplayName("deve retornar o DTO quando o id existe")
        void deveRetornarDTOQuandoIdExiste() {
            // Arrange
            AuthLoginEntity      entity   = buildEntity();
            AuthLogin            domain   = buildDomain();
            AuthLoginResponseDTO expected = buildResponseDTO();

            when(repository.findById(1L)).thenReturn(Optional.of(entity));
            when(entityMapper.toDomain(entity)).thenReturn(domain);
            when(dtoMapper.toResponseDTO(domain)).thenReturn(expected);

            // Act
            AuthLoginResponseDTO result = service.buscarAuthLogin(1L);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar AuthLoginNotFoundException quando o id não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarAuthLogin(99L))
                    .isInstanceOf(AuthLoginNotFoundException.class)
                    .hasMessageContaining("99");

            verify(entityMapper, never()).toDomain(any(AuthLoginEntity.class));
        }
    }

    // =========================================================================
    // deletarAuthLogin
    // =========================================================================
    @Nested
    @DisplayName("deletarAuthLogin")
    class DeletarAuthLogin {

        @Test
        @DisplayName("deve deletar quando o id existe")
        void deveDeletarComSucesso() {
            AuthLoginEntity entity = buildEntity();
            when(repository.findById(1L)).thenReturn(Optional.of(entity));

            service.deletarAuthLogin(1L);

            verify(repository).delete(entity);
        }

        @Test
        @DisplayName("deve lançar AuthLoginNotFoundException quando o id não existe")
        void deveLancarExcecaoQuandoIdNaoExiste() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.deletarAuthLogin(99L))
                    .isInstanceOf(AuthLoginNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).delete(any());
        }
    }
}