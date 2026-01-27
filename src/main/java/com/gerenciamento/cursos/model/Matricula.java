package com.gerenciamento.cursos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa a Matrícula de um Aluno em um Curso.
 * Relaciona alunos e cursos, controlando status e progresso.
 */
@Entity
@Table(name = "matriculas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"aluno_id", "curso_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Aluno é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @NotNull(message = "Curso é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMatricula status = StatusMatricula.ATIVA;

    @Min(value = 0, message = "Progresso mínimo é 0%")
    @Max(value = 100, message = "Progresso máximo é 100%")
    @Column(nullable = false)
    private Double progresso = 0.0;

    @Column(name = "data_matricula", nullable = false, updatable = false)
    private LocalDateTime dataMatricula;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataMatricula = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        if (progresso >= 100.0 && status == StatusMatricula.ATIVA) {
            status = StatusMatricula.CONCLUIDA;
            dataConclusao = LocalDateTime.now();
        }
    }

    /**
     * Atualiza o progresso da matrícula.
     */
    public void atualizarProgresso(Double novoProgresso) {
        if (novoProgresso < 0) {
            this.progresso = 0.0;
        } else if (novoProgresso > 100) {
            this.progresso = 100.0;
        } else {
            this.progresso = novoProgresso;
        }
    }

    /**
     * Cancela a matrícula.
     */
    public void cancelar() {
        this.status = StatusMatricula.CANCELADA;
    }

    /**
     * Verifica se a matrícula está ativa.
     */
    public boolean isAtiva() {
        return status == StatusMatricula.ATIVA;
    }

    /**
     * Enum para representar o status da matrícula.
     */
    public enum StatusMatricula {
        ATIVA,
        CONCLUIDA,
        CANCELADA,
        TRANCADA
    }
}
