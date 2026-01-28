package com.gerenciamento.cursos.dto;

import com.gerenciamento.cursos.model.Curso;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object para Curso.
 * Usado para transferência de dados entre camadas e API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Integer cargaHoraria;
    private Integer vagas;
    private Integer vagasDisponiveis;
    private Boolean ativo;
    private Long professorId;
    private String professorNome;
    private LocalDateTime dataCriacao;
    private Integer totalMatriculas;

    /**
     * Converte entidade Curso para DTO.
     */
    public static CursoDTO fromEntity(Curso curso) {
        CursoDTO dto = new CursoDTO();
        dto.setId(curso.getId());
        dto.setNome(curso.getNome());
        dto.setDescricao(curso.getDescricao());
        dto.setCargaHoraria(curso.getCargaHoraria());
        dto.setVagas(curso.getVagas());
        dto.setVagasDisponiveis(curso.getVagasDisponiveis());
        dto.setAtivo(curso.getAtivo());
        dto.setProfessorId(curso.getProfessor() != null ? curso.getProfessor().getId() : null);
        dto.setProfessorNome(curso.getProfessor() != null ? curso.getProfessor().getNome() : null);
        dto.setDataCriacao(curso.getDataCriacao());
        dto.setTotalMatriculas(curso.getMatriculas() != null ? curso.getMatriculas().size() : 0);
        return dto;
    }

    /**
     * Converte DTO para entidade Curso.
     * Nota: O professor deve ser associado no Service após buscar o Usuario pelo professorId.
     */
    public Curso toEntity() {
        Curso curso = new Curso();
        curso.setId(this.id);
        curso.setNome(this.nome);
        curso.setDescricao(this.descricao);
        curso.setCargaHoraria(this.cargaHoraria);
        curso.setVagas(this.vagas);
        curso.setVagasDisponiveis(this.vagasDisponiveis);
        curso.setAtivo(this.ativo != null ? this.ativo : true);
        // Professor será associado no Service usando professorId
        return curso;
    }
}
