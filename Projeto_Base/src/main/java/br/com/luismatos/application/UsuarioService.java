package br.com.luismatos.application;

import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.domain.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder   passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository      = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Autenticação ──────────────────────────────────────────────────────────

    public Optional<Usuario> autenticar(String email, String senha) {
        return repository.buscarPorEmailESenha(email, passwordEncoder.encode(senha));
    }

    // ── Listagem ──────────────────────────────────────────────────────────────

    /** Retorna todos os usuários ordenados por e-mail. */
    public List<Usuario> listarTodos() {
        return repository.listarTodos();
    }

    // ── Criação ───────────────────────────────────────────────────────────────

    /**
     * Registra um novo usuário.
     * @throws IllegalArgumentException se o e-mail já estiver em uso.
     */
    public Usuario registrar(Usuario usuario) {
        if (repository.existeEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + usuario.getEmail());
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.salvar(usuario);
    }

    // ── Edição ────────────────────────────────────────────────────────────────

    /**
     * Atualiza os dados de um usuário existente.
     *
     * Regras de senha:
     *   - Se {@code usuario.getSenha()} estiver em branco → mantém a senha atual.
     *   - Caso contrário, a nova senha é criptografada antes de persistir.
     *
     * @throws IllegalArgumentException se o usuário não existir ou o e-mail já
     *                                  pertencer a outro usuário.
     */
    public Usuario atualizar(Usuario usuario) {
        Optional<Usuario> existente = repository.buscarPorId(usuario.getId());
        if (!existente.isPresent()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        // E-mail alterado: verifica unicidade em relação a OUTROS usuários
        if (repository.existeEmailParaOutroUsuario(usuario.getEmail(), usuario.getId())) {
            throw new IllegalArgumentException("E-mail já cadastrado por outro usuário.");
        }

        // Senha: só substitui se uma nova foi informada
        String novaSenha = usuario.getSenha();
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(novaSenha));
        } else {
            usuario.setSenha(existente.get().getSenha());
        }

        return repository.salvar(usuario);
    }

    // ── Exclusão ──────────────────────────────────────────────────────────────

    /**
     * Remove um usuário pelo ID.
     * @throws IllegalArgumentException se o usuário não for encontrado.
     */
    public void excluir(Long id) {
        Usuario usuario = repository.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        repository.deletar(usuario);
    }
}
