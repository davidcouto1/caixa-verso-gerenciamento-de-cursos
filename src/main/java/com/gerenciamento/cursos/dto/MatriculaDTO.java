package com.gerenciamento.cursos.dto;

import com.gerenciamento.cursos.model.Matricula;
import com.gerenciamento.cursos.model.Matricula.StatusMatricula;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object para Matrícula.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaDTO {

    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Long cursoId;
    private String cursoNome;
    private StatusMatricula status;
    private Double progresso;
    private LocalDateTime dataMatricula;
    private LocalDateTime dataConclusao;

    /**
     * Converte entidade Matrícula para DTO.
     */
    public static MatriculaDTO fromEntity(Matricula matricula) {
        MatriculaDTO dto = new MatriculaDTO();
        dto.setId(matricula.getId());
        dto.setAlunoId(matricula.getAluno().getId());
        dto.setAlunoNome(matricula.getAluno().getNome());
        dto.setCursoId(matricula.getCurso().getId());
        dto.setCursoNome(matricula.getCurso().getNome());
        dto.setStatus(matricula.getStatus());
        dto.setProgresso(matricula.getProgresso());
        dto.setDataMatricula(matricula.getDataMatricula());
        dto.setDataConclusao(matricula.getDataConclusao());
        return dto;
    }
}
