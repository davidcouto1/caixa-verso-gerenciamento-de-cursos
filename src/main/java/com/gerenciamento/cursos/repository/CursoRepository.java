package com.gerenciamento.cursos.repository;

import com.gerenciamento.cursos.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de persistência da entidade Curso.
 * Encapsula o acesso ao banco de dados.
 */
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    /**
     * Busca todos os cursos ativos.
     */
    List<Curso> findByAtivoTrue();

    /**
     * Busca cursos por nome (case insensitive).
     */
    List<Curso> findByNomeContainingIgnoreCase(String nome);

    /**
     * Busca cursos de um professor específico.
     */
    List<Curso> findByProfessorId(Long professorId);

    /**
     * Busca cursos com vagas disponíveis.
     */
    @Query("SELECT c FROM Curso c WHERE c.vagasDisponiveis > 0 AND c.ativo = true")
    List<Curso> findCursosComVagas();

    /**
     * Conta quantos cursos um professor possui.
     */
    long countByProfessorId(Long professorId);
}
