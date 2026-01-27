package com.gerenciamento.cursos.controller;

import com.gerenciamento.cursos.dto.AlunoDTO;
import com.gerenciamento.cursos.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Alunos.
 */
@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final AlunoService alunoService;

    /**
     * Lista todos os alunos ativos.
     * GET /api/alunos
     */
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarTodos() {
        List<AlunoDTO> alunos = alunoService.listarTodos();
        return ResponseEntity.ok(alunos);
    }

    /**
     * Busca aluno por ID.
     * GET /api/alunos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> buscarPorId(@PathVariable Long id) {
        AlunoDTO aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(aluno);
    }

    /**
     * Cria um novo aluno.
     * POST /api/alunos
     */
    @PostMapping
    public ResponseEntity<AlunoDTO> criar(@Valid @RequestBody AlunoDTO alunoDTO) {
        AlunoDTO novoAluno = alunoService.criar(alunoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    /**
     * Atualiza um aluno existente.
     * PUT /api/alunos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> atualizar(@PathVariable Long id, 
                                               @Valid @RequestBody AlunoDTO alunoDTO) {
        AlunoDTO alunoAtualizado = alunoService.atualizar(id, alunoDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    /**
     * Deleta um aluno (soft delete).
     * DELETE /api/alunos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Busca alunos por nome.
     * GET /api/alunos/buscar?nome=XXX
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<AlunoDTO>> buscarPorNome(@RequestParam String nome) {
        List<AlunoDTO> alunos = alunoService.buscarPorNome(nome);
        return ResponseEntity.ok(alunos);
    }
}
