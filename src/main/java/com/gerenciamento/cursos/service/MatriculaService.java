package com.gerenciamento.cursos.service;

import com.gerenciamento.cursos.dto.MatriculaDTO;
import com.gerenciamento.cursos.exception.BusinessException;
import com.gerenciamento.cursos.exception.ResourceNotFoundException;
import com.gerenciamento.cursos.model.Aluno;
import com.gerenciamento.cursos.model.Curso;
import com.gerenciamento.cursos.model.Matricula;
import com.gerenciamento.cursos.repository.AlunoRepository;
import com.gerenciamento.cursos.repository.CursoRepository;
import com.gerenciamento.cursos.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio de Matrículas.
 * Implementa regras como validação de vagas e pré-requisitos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    /**
     * Lista todas as matrículas.
     */
    public List<MatriculaDTO> listarTodas() {
        log.info("Listando todas as matrículas");
        return matriculaRepository.findAll().stream()
                .map(MatriculaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Realiza matrícula de um aluno em um curso.
     */
    @Transactional
    public MatriculaDTO matricular(Long alunoId, Long cursoId) {
        log.info("Matriculando aluno {} no curso {}", alunoId, cursoId);
        
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", alunoId));
        
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", cursoId));
        
        validarMatricula(aluno, curso);
        
        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setCurso(curso);
        matricula.setStatus(Matricula.StatusMatricula.ATIVA);
        matricula.setProgresso(0.0);
        
        // Decrementa vaga disponível
        curso.decrementarVaga();
        cursoRepository.save(curso);
        
        matricula = matriculaRepository.save(matricula);
        
        log.info("Matrícula realizada com sucesso. ID: {}", matricula.getId());
        return MatriculaDTO.fromEntity(matricula);
    }

    /**
     * Cancela uma matrícula.
     */
    @Transactional
    public void cancelarMatricula(Long matriculaId) {
        log.info("Cancelando matrícula ID: {}", matriculaId);
        
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula", matriculaId));
        
        if (!matricula.isAtiva()) {
            throw new BusinessException("Matrícula já foi cancelada ou concluída");
        }
        
        matricula.cancelar();
        
        // Incrementa vaga disponível
        Curso curso = matricula.getCurso();
        curso.incrementarVaga();
        cursoRepository.save(curso);
        
        matriculaRepository.save(matricula);
        
        log.info("Matrícula cancelada com sucesso");
    }

    /**
     * Atualiza o progresso de uma matrícula.
     */
    @Transactional
    public MatriculaDTO atualizarProgresso(Long matriculaId, Double progresso) {
        log.info("Atualizando progresso da matrícula {} para {}%", matriculaId, progresso);
        
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new ResourceNotFoundException("Matrícula", matriculaId));
        
        if (!matricula.isAtiva()) {
            throw new BusinessException("Não é possível atualizar progresso de matrícula inativa");
        }
        
        if (progresso < 0 || progresso > 100) {
            throw new BusinessException("Progresso deve estar entre 0 e 100");
        }
        
        matricula.atualizarProgresso(progresso);
        matricula = matriculaRepository.save(matricula);
        
        log.info("Progresso atualizado com sucesso");
        return MatriculaDTO.fromEntity(matricula);
    }

    /**
     * Lista todas as matrículas de um aluno.
     */
    public List<MatriculaDTO> listarMatriculasPorAluno(Long alunoId) {
        log.info("Listando matrículas do aluno: {}", alunoId);
        return matriculaRepository.findByAlunoId(alunoId).stream()
                .map(MatriculaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas as matrículas de um curso.
     */
    public List<MatriculaDTO> listarMatriculasPorCurso(Long cursoId) {
        log.info("Listando matrículas do curso: {}", cursoId);
        return matriculaRepository.findByCursoId(cursoId).stream()
                .map(MatriculaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Valida se a matrícula pode ser realizada.
     */
    private void validarMatricula(Aluno aluno, Curso curso) {
        // Verifica se aluno está ativo
        if (!aluno.getAtivo()) {
            throw new BusinessException("Aluno inativo não pode se matricular");
        }
        
        // Verifica se curso está ativo
        if (!curso.getAtivo()) {
            throw new BusinessException("Curso inativo não aceita novas matrículas");
        }
        
        // Verifica se há vagas disponíveis
        if (!curso.temVagasDisponiveis()) {
            throw new BusinessException("Curso não possui vagas disponíveis");
        }
        
        // Verifica se aluno já está matriculado no curso
        if (matriculaRepository.existsByAlunoIdAndCursoId(aluno.getId(), curso.getId())) {
            throw new BusinessException("Aluno já está matriculado neste curso");
        }
    }
}
