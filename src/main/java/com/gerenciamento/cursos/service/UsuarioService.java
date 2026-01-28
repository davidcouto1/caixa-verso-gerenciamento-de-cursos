package com.gerenciamento.cursos.service;

import com.gerenciamento.cursos.exception.BusinessException;
import com.gerenciamento.cursos.exception.ResourceNotFoundException;
import com.gerenciamento.cursos.model.Usuario;
import com.gerenciamento.cursos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service responsável pela lógica de negócio de Usuários.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lista todos os usuários.
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Lista apenas professores.
     */
    public List<Usuario> listarProfessores() {
        return usuarioRepository.findByTipo(Usuario.TipoUsuario.PROFESSOR);
    }

    /**
     * Busca usuário por ID.
     */
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));
    }

    /**
     * Cria um novo usuário (professor ou admin).
     */
    @Transactional
    public Usuario criar(Usuario usuario) {
        log.info("Criando novo usuário: {} ({})", usuario.getNome(), usuario.getTipo());

        // Valida se email já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new BusinessException("Email já cadastrado no sistema");
        }

        // Criptografa a senha
        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        } else {
            // Senha padrão se não informada
            usuario.setSenha(passwordEncoder.encode("senha123"));
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso. ID: {}", usuario.getId());
        
        return usuario;
    }

    /**
     * Atualiza um usuário existente.
     */
    @Transactional
    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        // Valida email único
        usuarioRepository.findByEmail(usuarioAtualizado.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new BusinessException("Email já cadastrado para outro usuário");
                    }
                });

        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setTipo(usuarioAtualizado.getTipo());
        usuarioExistente.setAtivo(usuarioAtualizado.getAtivo());

        // Atualiza senha apenas se foi informada
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isEmpty()) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
        }

        usuarioExistente = usuarioRepository.save(usuarioExistente);
        log.info("Usuário atualizado com sucesso");

        return usuarioExistente;
    }

    /**
     * Desativa um usuário.
     */
    @Transactional
    public void desativar(Long id) {
        log.info("Desativando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", id));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso");
    }
}
