package com.gerenciamento.cursos.service;

import com.gerenciamento.cursos.dto.CursoDTO;
import com.gerenciamento.cursos.exception.BusinessException;
import com.gerenciamento.cursos.exception.ResourceNotFoundException;
import com.gerenciamento.cursos.model.Curso;
import com.gerenciamento.cursos.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio de Cursos.
 * Implementa validações e regras de negócio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CursoService {

    private final CursoRepository cursoRepository;

    /**
     * Lista todos os cursos ativos.
     */
    public List<CursoDTO> listarTodos() {
        log.info("Listando todos os cursos ativos");
        return cursoRepository.findByAtivoTrue().stream()
                .map(CursoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca curso por ID.
     */
    public CursoDTO buscarPorId(Long id) {
        log.info("Buscando curso com ID: {}", id);
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
        return CursoDTO.fromEntity(curso);
    }

    /**
     * Cria um novo curso.
     */
    @Transactional
    public CursoDTO criar(CursoDTO cursoDTO) {
        log.info("Criando novo curso: {}", cursoDTO.getNome());
        
        validarDadosCurso(cursoDTO);
        
        Curso curso = cursoDTO.toEntity();
        curso = cursoRepository.save(curso);
        
        log.info("Curso criado com sucesso. ID: {}", curso.getId());
        return CursoDTO.fromEntity(curso);
    }

    /**
     * Atualiza um curso existente.
     */
    @Transactional
    public CursoDTO atualizar(Long id, CursoDTO cursoDTO) {
        log.info("Atualizando curso ID: {}", id);
        
        Curso cursoExistente = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
        
        validarDadosCurso(cursoDTO);
        
        // Atualiza apenas campos permitidos
        cursoExistente.setNome(cursoDTO.getNome());
        cursoExistente.setDescricao(cursoDTO.getDescricao());
        cursoExistente.setCargaHoraria(cursoDTO.getCargaHoraria());
        
        // Valida alteração de vagas
        if (cursoDTO.getVagas() != null && !cursoDTO.getVagas().equals(cursoExistente.getVagas())) {
            validarAlteracaoVagas(cursoExistente, cursoDTO.getVagas());
            cursoExistente.setVagas(cursoDTO.getVagas());
        }
        
        cursoExistente = cursoRepository.save(cursoExistente);
        log.info("Curso atualizado com sucesso");
        
        return CursoDTO.fromEntity(cursoExistente);
    }

    /**
     * Desativa um curso (soft delete).
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Desativando curso ID: {}", id);
        
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
        
        curso.setAtivo(false);
        cursoRepository.save(curso);
        
        log.info("Curso desativado com sucesso");
    }

    /**
     * Lista cursos com vagas disponíveis.
     */
    public List<CursoDTO> listarCursosComVagas() {
        log.info("Listando cursos com vagas disponíveis");
        return cursoRepository.findCursosComVagas().stream()
                .map(CursoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca cursos por nome.
     */
    public List<CursoDTO> buscarPorNome(String nome) {
        log.info("Buscando cursos com nome contendo: {}", nome);
        return cursoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(CursoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Valida dados do curso.
     */
    private void validarDadosCurso(CursoDTO cursoDTO) {
        if (cursoDTO.getNome() == null || cursoDTO.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do curso é obrigatório");
        }
        
        if (cursoDTO.getCargaHoraria() == null || cursoDTO.getCargaHoraria() <= 0) {
            throw new BusinessException("Carga horária deve ser maior que zero");
        }
        
        if (cursoDTO.getVagas() == null || cursoDTO.getVagas() <= 0) {
            throw new BusinessException("Número de vagas deve ser maior que zero");
        }
        
        if (cursoDTO.getProfessorId() == null) {
            throw new BusinessException("Professor responsável é obrigatório");
        }
    }

    /**
     * Valida alteração de vagas do curso.
     */
    private void validarAlteracaoVagas(Curso curso, Integer novasVagas) {
        int vagasOcupadas = curso.getVagas() - curso.getVagasDisponiveis();
        
        if (novasVagas < vagasOcupadas) {
            throw new BusinessException(
                String.format("Não é possível reduzir vagas para %d. Já existem %d alunos matriculados",
                    novasVagas, vagasOcupadas)
            );
        }
        
        // Ajusta vagas disponíveis proporcionalmente
        int diferenca = novasVagas - curso.getVagas();
        curso.setVagasDisponiveis(curso.getVagasDisponiveis() + diferenca);
    }
}
