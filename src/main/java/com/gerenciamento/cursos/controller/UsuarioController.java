package com.gerenciamento.cursos.controller;

import com.gerenciamento.cursos.model.Usuario;
import com.gerenciamento.cursos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Usuários.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    /**
     * Lista todos os usuários.
     * GET /api/usuarios
     */
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Lista apenas professores.
     * GET /api/usuarios/professores
     */
    @GetMapping("/professores")
    public ResponseEntity<List<Usuario>> listarProfessores() {
        List<Usuario> professores = usuarioRepository.findByTipo(Usuario.TipoUsuario.PROFESSOR);
        return ResponseEntity.ok(professores);
    }

    /**
     * Busca usuário por ID.
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
