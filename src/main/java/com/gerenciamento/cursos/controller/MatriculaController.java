package com.gerenciamento.cursos.controller;

import com.gerenciamento.cursos.dto.MatriculaDTO;
import com.gerenciamento.cursos.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST para gerenciamento de Matrículas.
 */
@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    /**
     * Realiza matrícula de um aluno em um curso.
     * POST /api/matriculas
     * Body: { "alunoId": 1, "cursoId": 1 }
     */
    @PostMapping
    public ResponseEntity<MatriculaDTO> matricular(@RequestBody Map<String, Long> request) {
        Long alunoId = request.get("alunoId");
        Long cursoId = request.get("cursoId");
        MatriculaDTO matricula = matriculaService.matricular(alunoId, cursoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
    }

    /**
     * Cancela uma matrícula.
     * DELETE /api/matriculas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        matriculaService.cancelarMatricula(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza o progresso de uma matrícula.
     * PATCH /api/matriculas/{id}/progresso
     * Body: { "progresso": 75.5 }
     */
    @PatchMapping("/{id}/progresso")
    public ResponseEntity<MatriculaDTO> atualizarProgresso(@PathVariable Long id, 
                                                            @RequestBody Map<String, Double> request) {
        Double progresso = request.get("progresso");
        MatriculaDTO matricula = matriculaService.atualizarProgresso(id, progresso);
        return ResponseEntity.ok(matricula);
    }

    /**
     * Lista matrículas de um aluno.
     * GET /api/matriculas/aluno/{alunoId}
     */
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<MatriculaDTO>> listarPorAluno(@PathVariable Long alunoId) {
        List<MatriculaDTO> matriculas = matriculaService.listarMatriculasPorAluno(alunoId);
        return ResponseEntity.ok(matriculas);
    }

    /**
     * Lista matrículas de um curso.
     * GET /api/matriculas/curso/{cursoId}
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<MatriculaDTO>> listarPorCurso(@PathVariable Long cursoId) {
        List<MatriculaDTO> matriculas = matriculaService.listarMatriculasPorCurso(cursoId);
        return ResponseEntity.ok(matriculas);
    }
}
