package com.gerenciamento.cursos.repository;

import com.gerenciamento.cursos.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade Aluno.
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    /**
     * Busca aluno por email.
     */
    Optional<Aluno> findByEmail(String email);

    /**
     * Busca aluno por CPF.
     */
    Optional<Aluno> findByCpf(String cpf);

    /**
     * Verifica se existe aluno com determinado email.
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se existe aluno com determinado CPF.
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca todos os alunos ativos.
     */
    List<Aluno> findByAtivoTrue();

    /**
     * Busca alunos por nome (case insensitive).
     */
    List<Aluno> findByNomeContainingIgnoreCase(String nome);
}
