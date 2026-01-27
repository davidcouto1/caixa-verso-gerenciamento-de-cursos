package com.gerenciamento.cursos.controller;

import com.gerenciamento.cursos.dto.CursoDTO;
import com.gerenciamento.cursos.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gerenciamento de Cursos.
 * Camada de apresentação - responsável por receber requisições HTTP.
 */
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    /**
     * Lista todos os cursos ativos.
     * GET /api/cursos
     */
    @GetMapping
    public ResponseEntity<List<CursoDTO>> listarTodos() {
        List<CursoDTO> cursos = cursoService.listarTodos();
        return ResponseEntity.ok(cursos);
    }

    /**
     * Busca curso por ID.
     * GET /api/cursos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CursoDTO> buscarPorId(@PathVariable Long id) {
        CursoDTO curso = cursoService.buscarPorId(id);
        return ResponseEntity.ok(curso);
    }

    /**
     * Cria um novo curso.
     * POST /api/cursos
     */
    @PostMapping
    public ResponseEntity<CursoDTO> criar(@Valid @RequestBody CursoDTO cursoDTO) {
        CursoDTO novoCurso = cursoService.criar(cursoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCurso);
    }

    /**
     * Atualiza um curso existente.
     * PUT /api/cursos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CursoDTO> atualizar(@PathVariable Long id, 
                                               @Valid @RequestBody CursoDTO cursoDTO) {
        CursoDTO cursoAtualizado = cursoService.atualizar(id, cursoDTO);
        return ResponseEntity.ok(cursoAtualizado);
    }

    /**
     * Deleta um curso (soft delete).
     * DELETE /api/cursos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cursoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista cursos com vagas disponíveis.
     * GET /api/cursos/disponiveis
     */
    @GetMapping("/disponiveis")
    public ResponseEntity<List<CursoDTO>> listarCursosComVagas() {
        List<CursoDTO> cursos = cursoService.listarCursosComVagas();
        return ResponseEntity.ok(cursos);
    }

    /**
     * Busca cursos por nome.
     * GET /api/cursos/buscar?nome=XXX
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<CursoDTO>> buscarPorNome(@RequestParam String nome) {
        List<CursoDTO> cursos = cursoService.buscarPorNome(nome);
        return ResponseEntity.ok(cursos);
    }
}
