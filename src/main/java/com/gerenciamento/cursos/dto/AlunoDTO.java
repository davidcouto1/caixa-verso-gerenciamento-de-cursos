package com.gerenciamento.cursos.dto;

import com.gerenciamento.cursos.model.Aluno;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object para Aluno.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String senha; // Senha para criar usuário no sistema
    private Boolean ativo;
    private LocalDateTime dataCadastro;
    private Integer totalCursos;
    private Double progressoMedio;

    /**
     * Converte entidade Aluno para DTO.
     */
    public static AlunoDTO fromEntity(Aluno aluno) {
        AlunoDTO dto = new AlunoDTO();
        dto.setId(aluno.getId());
        dto.setNome(aluno.getNome());
        dto.setEmail(aluno.getEmail());
        dto.setCpf(aluno.getCpf());
        dto.setTelefone(aluno.getTelefone());
        dto.setAtivo(aluno.getAtivo());
        dto.setDataCadastro(aluno.getDataCadastro());
        dto.setTotalCursos(aluno.getTotalCursosMatriculados());
        dto.setProgressoMedio(aluno.calcularProgressoMedio());
        return dto;
    }

    /**
     * Converte DTO para entidade Aluno.
     */
    public Aluno toEntity() {
        Aluno aluno = new Aluno();
        aluno.setId(this.id);
        aluno.setNome(this.nome);
        aluno.setEmail(this.email);
        aluno.setCpf(this.cpf);
        aluno.setTelefone(this.telefone);
        aluno.setAtivo(this.ativo != null ? this.ativo : true);
        return aluno;
    }
}
