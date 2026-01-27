package com.gerenciamento.cursos.repository;

import com.gerenciamento.cursos.model.Usuario;
import com.gerenciamento.cursos.model.Usuario.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe usuário com determinado email.
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários por tipo.
     */
    List<Usuario> findByTipo(TipoUsuario tipo);

    /**
     * Busca todos os usuários ativos.
     */
    List<Usuario> findByAtivoTrue();

    /**
     * Busca usuários ativos por tipo.
     */
    List<Usuario> findByTipoAndAtivoTrue(TipoUsuario tipo);
}
