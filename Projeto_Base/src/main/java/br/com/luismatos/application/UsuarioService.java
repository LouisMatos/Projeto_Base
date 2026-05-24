package br.com.luismatos.application;

import br.com.luismatos.domain.model.Usuario;
import br.com.luismatos.domain.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repository;
    private final PasswordEncoder   passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository      = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Autenticação ──────────────────────────────────────────────────────────

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> resultado = repository.buscarPorEmailESenha(email, passwordEncoder.encode(senha));
        if (resultado.isPresent()) {
            log.info("Autenticação bem-sucedida: {}", email);
        } else {
            log.warn("Falha de autenticação para: {}", email);
        }
        return resultado;
    }

    // ── Busca ─────────────────────────────────────────────────────────────────

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.buscarPorEmail(email);
    }

    // ── Listagem ──────────────────────────────────────────────────────────────

    public List<Usuario> listarTodos() {
        return repository.listarTodos();
    }

    // ── Criação ───────────────────────────────────────────────────────────────

    public Usuario registrar(Usuario usuario) {
        if (repository.existeEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + usuario.getEmail());
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario salvo = repository.salvar(usuario);
        log.info("Usuário registrado: {} [{}]", salvo.getEmail(), salvo.getTipoUsuario());
        return salvo;
    }

    // ── Edição ────────────────────────────────────────────────────────────────

    public Usuario atualizar(Usuario usuario) {
        Optional<Usuario> existente = repository.buscarPorId(usuario.getId());
        if (!existente.isPresent()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (repository.existeEmailParaOutroUsuario(usuario.getEmail(), usuario.getId())) {
            throw new IllegalArgumentException("E-mail já cadastrado por outro usuário.");
        }
        usuario.setSenha(resolverSenha(usuario.getSenha(), existente.get().getSenha()));
        Usuario atualizado = repository.salvar(usuario);
        log.info("Usuário atualizado: {}", atualizado.getEmail());
        return atualizado;
    }

    // ── Exclusão ──────────────────────────────────────────────────────────────

    public void excluir(Long id) {
        Usuario usuario = repository.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        repository.deletar(usuario);
        log.info("Usuário excluído: {} (id={})", usuario.getEmail(), id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolverSenha(String novaSenha, String senhaAtual) {
        if (novaSenha != null && !novaSenha.trim().isEmpty()) {
            return passwordEncoder.encode(novaSenha);
        }
        return senhaAtual;
    }
}
