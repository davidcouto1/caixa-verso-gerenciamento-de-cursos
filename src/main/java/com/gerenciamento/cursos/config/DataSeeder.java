package com.gerenciamento.cursos.config;

import com.gerenciamento.cursos.model.Aluno;
import com.gerenciamento.cursos.model.Curso;
import com.gerenciamento.cursos.model.Usuario;
import com.gerenciamento.cursos.repository.AlunoRepository;
import com.gerenciamento.cursos.repository.CursoRepository;
import com.gerenciamento.cursos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Classe para popular o banco de dados com dados iniciais para testes.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Populando banco de dados com dados iniciais...");
            
            // Criar Professores
            Usuario prof1 = new Usuario();
            prof1.setNome("Prof. João Silva");
            prof1.setEmail("joao.silva@escola.com");
            prof1.setSenha("senha123");
            prof1.setTipo(Usuario.TipoUsuario.PROFESSOR);
            prof1.setAtivo(true);
            
            Usuario prof2 = new Usuario();
            prof2.setNome("Profa. Maria Santos");
            prof2.setEmail("maria.santos@escola.com");
            prof2.setSenha("senha123");
            prof2.setTipo(Usuario.TipoUsuario.PROFESSOR);
            prof2.setAtivo(true);
            
            Usuario prof3 = new Usuario();
            prof3.setNome("Prof. Carlos Oliveira");
            prof3.setEmail("carlos.oliveira@escola.com");
            prof3.setSenha("senha123");
            prof3.setTipo(Usuario.TipoUsuario.PROFESSOR);
            prof3.setAtivo(true);
            
            usuarioRepository.save(prof1);
            usuarioRepository.save(prof2);
            usuarioRepository.save(prof3);
            
            log.info("Professores criados: {}", usuarioRepository.count());
            
            // Criar Alunos de exemplo
            String[] nomesAlunos = {
                "Ana Paula Costa", "Bruno Henrique Lima", "Carla Fernanda Souza",
                "Daniel Rodrigues", "Eduarda Alves", "Felipe Martins",
                "Gabriela Santos", "Hugo Pereira", "Isabela Rocha"
            };
            
            for (int i = 0; i < nomesAlunos.length; i++) {
                Aluno aluno = new Aluno();
                aluno.setNome(nomesAlunos[i]);
                aluno.setEmail(nomesAlunos[i].toLowerCase().replace(" ", ".") + "@aluno.com");
                aluno.setCpf(String.format("%011d", 10000000000L + i));
                aluno.setTelefone(String.format("(11) 9%04d-%04d", i, i * 10));
                aluno.setAtivo(true);
                alunoRepository.save(aluno);
            }
            
            log.info("Alunos criados: {}", alunoRepository.count());
            
            // Criar Cursos de exemplo
            Curso curso1 = new Curso();
            curso1.setNome("Introdução à Arquitetura de Software");
            curso1.setDescricao("Curso sobre padrões e estilos arquiteturais");
            curso1.setCargaHoraria(60);
            curso1.setVagas(30);
            curso1.setVagasDisponiveis(30);
            curso1.setProfessorId(prof1.getId());
            curso1.setAtivo(true);
            
            Curso curso2 = new Curso();
            curso2.setNome("Desenvolvimento Web com Spring Boot");
            curso2.setDescricao("Aprenda a criar aplicações web com Spring Boot");
            curso2.setCargaHoraria(80);
            curso2.setVagas(25);
            curso2.setVagasDisponiveis(25);
            curso2.setProfessorId(prof2.getId());
            curso2.setAtivo(true);
            
            Curso curso3 = new Curso();
            curso3.setNome("Banco de Dados Avançado");
            curso3.setDescricao("SQL, NoSQL e otimização de queries");
            curso3.setCargaHoraria(50);
            curso3.setVagas(20);
            curso3.setVagasDisponiveis(20);
            curso3.setProfessorId(prof3.getId());
            curso3.setAtivo(true);
            
            Curso curso4 = new Curso();
            curso4.setNome("Metodologias Ágeis");
            curso4.setDescricao("Scrum, Kanban e práticas ágeis");
            curso4.setCargaHoraria(40);
            curso4.setVagas(35);
            curso4.setVagasDisponiveis(35);
            curso4.setProfessorId(prof1.getId());
            curso4.setAtivo(true);
            
            Curso curso5 = new Curso();
            curso5.setNome("DevOps e Cloud Computing");
            curso5.setDescricao("Docker, Kubernetes e AWS");
            curso5.setCargaHoraria(70);
            curso5.setVagas(15);
            curso5.setVagasDisponiveis(15);
            curso5.setProfessorId(prof2.getId());
            curso5.setAtivo(true);
            
            cursoRepository.save(curso1);
            cursoRepository.save(curso2);
            cursoRepository.save(curso3);
            cursoRepository.save(curso4);
            cursoRepository.save(curso5);
            
            log.info("Cursos criados: {}", cursoRepository.count());
            log.info("Dados iniciais carregados com sucesso!");
        } else {
            log.info("Banco de dados já possui dados. Pulando seed...");
        }
    }
}
