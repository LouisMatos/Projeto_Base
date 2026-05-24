package br.com.luismatos.application;

import br.com.luismatos.domain.model.TipoUsuario;
import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@email.com");
        usuario.setSenha("senha123");
        usuario.setTipoUsuario(TipoUsuario.COMUM);
    }

    // ── registrar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve registrar usuário com senha codificada")
    void deveRegistrarUsuario() {
        when(repository.existeEmail(usuario.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("HASH_DA_SENHA");
        when(repository.salvar(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = service.registrar(usuario);

        assertThat(resultado).isNotNull();
        verify(passwordEncoder).encode("senha123");
        verify(repository).salvar(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        when(repository.existeEmail(usuario.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(usuario))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("E-mail já cadastrado");

        verify(repository, never()).salvar(any());
    }

    // ── autenticar ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar usuário quando credenciais estão corretas")
    void deveAutenticarUsuarioComSucesso() {
        when(passwordEncoder.encode(anyString())).thenReturn("HASH");
        when(repository.buscarPorEmailESenha("teste@email.com", "HASH"))
            .thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = service.autenticar("teste@email.com", "senha123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("teste@email.com");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando credenciais são inválidas")
    void deveRetornarVazioParaCredenciaisInvalidas() {
        when(passwordEncoder.encode(anyString())).thenReturn("HASH_ERRADO");
        when(repository.buscarPorEmailESenha(anyString(), anyString()))
            .thenReturn(Optional.empty());

        Optional<Usuario> resultado = service.autenticar("teste@email.com", "senha_errada");

        assertThat(resultado).isEmpty();
    }

    // ── excluir ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve excluir usuário existente")
    void deveExcluirUsuario() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(usuario));

        service.excluir(1L);

        verify(repository).deletar(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir usuário inexistente")
    void deveLancarExcecaoAoExcluirNaoEncontrado() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.excluir(99L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("não encontrado");

        verify(repository, never()).deletar(any());
    }

    // ── atualizar ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar usuário mantendo senha atual quando campo vazio")
    void deveManterSenhaAtualQuandoNovaSenhaVazia() {
        Usuario atualizado = new Usuario();
        atualizado.setId(1L);
        atualizado.setEmail("novo@email.com");
        atualizado.setSenha("");  // sem nova senha
        atualizado.setTipoUsuario(TipoUsuario.ADMINISTRADOR);

        Usuario existente = new Usuario();
        existente.setId(1L);
        existente.setSenha("HASH_ATUAL");

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.existeEmailParaOutroUsuario("novo@email.com", 1L)).thenReturn(false);
        when(repository.salvar(any())).thenReturn(atualizado);

        service.atualizar(atualizado);

        // Senha deve permanecer como a do banco, sem chamar o encoder
        verify(passwordEncoder, never()).encode(anyString());
        assertThat(atualizado.getSenha()).isEqualTo("HASH_ATUAL");
    }

    @Test
    @DisplayName("Deve codificar nova senha ao atualizar quando informada")
    void deveCodeificarNovaSenhaAoAtualizar() {
        Usuario atualizado = new Usuario();
        atualizado.setId(1L);
        atualizado.setEmail("teste@email.com");
        atualizado.setSenha("novaSenha");

        when(repository.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(repository.existeEmailParaOutroUsuario(anyString(), any())).thenReturn(false);
        when(passwordEncoder.encode("novaSenha")).thenReturn("NOVO_HASH");
        when(repository.salvar(any())).thenReturn(atualizado);

        service.atualizar(atualizado);

        verify(passwordEncoder).encode("novaSenha");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com e-mail já usado por outro usuário")
    void deveLancarExcecaoEmailColisaoNaEdicao() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        when(repository.existeEmailParaOutroUsuario(usuario.getEmail(), 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(usuario))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("E-mail já cadastrado por outro usuário");
    }

    // ── listarTodos ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar lista de todos os usuários")
    void deveListarTodosOsUsuarios() {
        when(repository.listarTodos()).thenReturn(List.of(usuario));

        List<Usuario> resultado = service.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEmail()).isEqualTo("teste@email.com");
    }
}
