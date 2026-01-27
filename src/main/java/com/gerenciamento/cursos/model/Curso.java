package com.gerenciamento.cursos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa um Curso no sistema.
 * Contém informações como nome, descrição, carga horária e vagas disponíveis.
 */
@Entity
@Table(name = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do curso é obrigatório")
    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @NotNull(message = "Carga horária é obrigatória")
    @Min(value = 1, message = "Carga horária deve ser maior que zero")
    @Column(nullable = false)
    private Integer cargaHoraria;

    @NotNull(message = "Número de vagas é obrigatório")
    @Min(value = 1, message = "Deve haver pelo menos 1 vaga")
    @Column(nullable = false)
    private Integer vagas;

    @Column(nullable = false)
    private Integer vagasDisponiveis;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @NotNull(message = "Professor responsável é obrigatório")
    @Column(nullable = false)
    private Long professorId;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Matricula> matriculas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (vagasDisponiveis == null) {
            vagasDisponiveis = vagas;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    /**
     * Verifica se há vagas disponíveis no curso.
     */
    public boolean temVagasDisponiveis() {
        return vagasDisponiveis > 0;
    }

    /**
     * Decrementa o número de vagas disponíveis.
     */
    public void decrementarVaga() {
        if (vagasDisponiveis > 0) {
            vagasDisponiveis--;
        }
    }

    /**
     * Incrementa o número de vagas disponíveis.
     */
    public void incrementarVaga() {
        if (vagasDisponiveis < vagas) {
            vagasDisponiveis++;
        }
    }
}
