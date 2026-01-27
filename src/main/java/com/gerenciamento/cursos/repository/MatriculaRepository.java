package com.gerenciamento.cursos.repository;

import com.gerenciamento.cursos.model.Matricula;
import com.gerenciamento.cursos.model.Matricula.StatusMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência da entidade Matrícula.
 */
@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    /**
     * Busca todas as matrículas de um aluno.
     */
    List<Matricula> findByAlunoId(Long alunoId);

    /**
     * Busca todas as matrículas de um curso.
     */
    List<Matricula> findByCursoId(Long cursoId);

    /**
     * Busca matrículas ativas de um aluno.
     */
    List<Matricula> findByAlunoIdAndStatus(Long alunoId, StatusMatricula status);

    /**
     * Verifica se aluno já está matriculado em determinado curso.
     */
    boolean existsByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    /**
     * Busca matrícula específica de aluno em curso.
     */
    Optional<Matricula> findByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    /**
     * Conta quantas matrículas ativas existem em um curso.
     */
    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.curso.id = :cursoId AND m.status = 'ATIVA'")
    long countMatriculasAtivasByCursoId(Long cursoId);

    /**
     * Conta quantos cursos um aluno concluiu.
     */
    long countByAlunoIdAndStatus(Long alunoId, StatusMatricula status);
}
