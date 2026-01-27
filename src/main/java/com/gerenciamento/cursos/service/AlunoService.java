package com.gerenciamento.cursos.service;

import com.gerenciamento.cursos.dto.AlunoDTO;
import com.gerenciamento.cursos.exception.BusinessException;
import com.gerenciamento.cursos.exception.ResourceNotFoundException;
import com.gerenciamento.cursos.model.Aluno;
import com.gerenciamento.cursos.repository.AlunoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio de Alunos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlunoService {

    private final AlunoRepository alunoRepository;

    /**
     * Lista todos os alunos ativos.
     */
    public List<AlunoDTO> listarTodos() {
        log.info("Listando todos os alunos ativos");
        return alunoRepository.findByAtivoTrue().stream()
                .map(AlunoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca aluno por ID.
     */
    public AlunoDTO buscarPorId(Long id) {
        log.info("Buscando aluno com ID: {}", id);
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        return AlunoDTO.fromEntity(aluno);
    }

    /**
     * Cria um novo aluno.
     */
    @Transactional
    public AlunoDTO criar(AlunoDTO alunoDTO) {
        log.info("Criando novo aluno: {}", alunoDTO.getNome());
        
        validarDadosAluno(alunoDTO);
        validarEmailUnico(alunoDTO.getEmail(), null);
        validarCpfUnico(alunoDTO.getCpf(), null);
        
        Aluno aluno = alunoDTO.toEntity();
        aluno = alunoRepository.save(aluno);
        
        log.info("Aluno criado com sucesso. ID: {}", aluno.getId());
        return AlunoDTO.fromEntity(aluno);
    }

    /**
     * Atualiza um aluno existente.
     */
    @Transactional
    public AlunoDTO atualizar(Long id, AlunoDTO alunoDTO) {
        log.info("Atualizando aluno ID: {}", id);
        
        Aluno alunoExistente = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        
        validarDadosAluno(alunoDTO);
        validarEmailUnico(alunoDTO.getEmail(), id);
        validarCpfUnico(alunoDTO.getCpf(), id);
        
        alunoExistente.setNome(alunoDTO.getNome());
        alunoExistente.setEmail(alunoDTO.getEmail());
        alunoExistente.setTelefone(alunoDTO.getTelefone());
        
        alunoExistente = alunoRepository.save(alunoExistente);
        log.info("Aluno atualizado com sucesso");
        
        return AlunoDTO.fromEntity(alunoExistente);
    }

    /**
     * Desativa um aluno (soft delete).
     */
    @Transactional
    public void deletar(Long id) {
        log.info("Desativando aluno ID: {}", id);
        
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
        
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
        
        log.info("Aluno desativado com sucesso");
    }

    /**
     * Busca alunos por nome.
     */
    public List<AlunoDTO> buscarPorNome(String nome) {
        log.info("Buscando alunos com nome contendo: {}", nome);
        return alunoRepository.findByNomeContainingIgnoreCase(nome).stream()
                .map(AlunoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Valida dados do aluno.
     */
    private void validarDadosAluno(AlunoDTO alunoDTO) {
        if (alunoDTO.getNome() == null || alunoDTO.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do aluno é obrigatório");
        }
        
        if (alunoDTO.getEmail() == null || !alunoDTO.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessException("Email inválido");
        }
        
        if (alunoDTO.getCpf() == null || !alunoDTO.getCpf().matches("\\d{11}")) {
            throw new BusinessException("CPF inválido. Deve conter 11 dígitos");
        }
    }

    /**
     * Valida se email já está em uso por outro aluno.
     */
    private void validarEmailUnico(String email, Long alunoId) {
        alunoRepository.findByEmail(email).ifPresent(aluno -> {
            if (!aluno.getId().equals(alunoId)) {
                throw new BusinessException("Email já cadastrado");
            }
        });
    }

    /**
     * Valida se CPF já está em uso por outro aluno.
     */
    private void validarCpfUnico(String cpf, Long alunoId) {
        alunoRepository.findByCpf(cpf).ifPresent(aluno -> {
            if (!aluno.getId().equals(alunoId)) {
                throw new BusinessException("CPF já cadastrado");
            }
        });
    }
}
