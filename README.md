# Sistema de Gerenciamento de Cursos

## 1. Descrição do Problema

Instituições de ensino necessitam de um sistema para gerenciar cursos, disciplinas e matrículas de alunos. Atualmente, muitos processos são manuais ou descentralizados, gerando inconsistências e dificuldades de acompanhamento.

**Problemas identificados:**
- Dificuldade em controlar vagas disponíveis em cursos
- Falta de visibilidade do progresso dos alunos
- Processos manuais de inscrição e gerenciamento
- Ausência de controle de permissões entre diferentes tipos de usuários

## 2. Objetivo do Sistema

Desenvolver um sistema web que permita:
- **Professores:** cadastrar e gerenciar cursos e disciplinas
- **Alunos:** visualizar cursos disponíveis e realizar matrículas
- **Administradores:** gerenciar usuários e gerar relatórios
- **Sistema:** controlar vagas, validar inscrições e calcular progresso automaticamente

**O projeto inclui:**
- 🔧 **Backend:** API REST com Spring Boot
- 🎨 **Frontend:** Interface web moderna em HTML/CSS/JavaScript
- 💾 **Banco de Dados:** H2 em memória (desenvolvimento/testes)

## 3. Estilo Arquitetural Adotado

### Arquitetura Monolítica em Camadas (Layered Architecture)

O sistema utiliza o padrão **MVC (Model-View-Controller)** organizado em camadas bem definidas.

```
┌─────────────────────────────────────┐
│     Camada de Apresentação          │
│         (Controllers)               │  ← REST API (JSON)
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Camada de Negócio              │
│         (Services)                  │  ← Regras de Negócio
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│      Camada de Acesso a Dados       │
│        (Repositories)               │  ← JPA/Hibernate
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        Banco de Dados               │
│      (H2 Database)                  │
└─────────────────────────────────────┘
```

### Componentes Principais:

**1. Controller Layer (Apresentação)**
- `CursoController`: endpoints para gerenciamento de cursos
- `AlunoController`: endpoints para gerenciamento de alunos
- `MatriculaController`: endpoints para inscrições e matrículas
- `UsuarioController`: endpoints para autenticação e gerenciamento de usuários

**2. Service Layer (Negócio)**
- `CursoService`: validações de criação, edição e vagas de cursos
- `AlunoService`: gestão de perfil e histórico do aluno
- `MatriculaService`: regras de inscrição, validação de pré-requisitos
- `UsuarioService`: autenticação e autorização

**3. Repository Layer (Dados)**
- `CursoRepository`: persistência de cursos
- `AlunoRepository`: persistência de alunos
- `MatriculaRepository`: persistência de matrículas
- `UsuarioRepository`: persistência de usuários

**4. Model Layer**
- Entities: `Curso`, `Aluno`, `Matricula`, `Usuario`
- DTOs: objetos de transferência para API

## 4. Justificativa das Decisões Arquiteturais

### Por que Arquitetura Monolítica em Camadas?

**Decisão:** Adotar arquitetura monolítica com separação clara de camadas (Controller, Service, Repository).

**Alternativas Consideradas:**
1. **Microserviços:** um serviço para cursos, outro para alunos, outro para matrículas
2. **Arquitetura em camadas sem separação rigorosa:** controllers acessando diretamente repositories
3. **Event-Driven Architecture:** comunicação entre componentes via eventos

**Motivos da escolha:**

✅ **Simplicidade de implementação e manutenção**
- Para o escopo acadêmico, a complexidade de microserviços seria desnecessária
- Um único processo facilita deploy e debugging
- Não há necessidade de escalabilidade independente de componentes

✅ **Clareza arquitetural**
- Separação de responsabilidades é visível e fácil de explicar
- Cada camada tem um papel bem definido
- Facilita a apresentação e compreensão do projeto

✅ **Baixo acoplamento e alta coesão**
- Controllers não conhecem detalhes de persistência
- Services contêm toda lógica de negócio isolada
- Repositories encapsulam acesso a dados

✅ **Manutenibilidade**
- Mudanças em uma camada não afetam as outras
- Facilita testes unitários e de integração
- Código mais organizado e legível

### Decisões Técnicas Específicas

#### 1. Spring Boot como Framework
**Decisão:** Utilizar Spring Boot para desenvolvimento da API REST.

**Justificativa:**
- Acelera o desenvolvimento sem esconder a arquitetura
- Injeção de dependências facilita baixo acoplamento
- Ecossistema maduro com JPA, Security, Validation
- Não compromete a clareza das camadas arquiteturais

**Impacto:** Redução de código boilerplate mantendo controle sobre a arquitetura.

#### 2. Banco de Dados H2
**Decisão:** Usar banco de dados H2 em memória para persistência.

**Justificativa:**
- Relacionamentos claros entre Curso, Aluno e Matrícula
- ACID garante consistência em operações críticas (ex: limite de vagas)
- Consultas complexas facilitadas por SQL
- Configuração zero - não requer instalação externa
- Console web integrado para visualização dos dados
- Ideal para desenvolvimento, testes e demonstrações acadêmicas

**Impacto:** Integridade referencial garantida, facilita desenvolvimento e testes, elimina dependências externas.

#### 3. API REST (JSON)
**Decisão:** Expor funcionalidades via API REST com respostas JSON.

**Justificativa:**
- Padrão amplamente adotado e compreendido
- Facilita integração futura com frontend (React, Angular, etc.)
- Stateless, facilitando escalabilidade horizontal se necessário

**Impacto:** Flexibilidade para diferentes clientes consumirem a API.

#### 4. DTOs (Data Transfer Objects)
**Decisão:** Separar entidades de domínio (Entities) de objetos de transferência (DTOs).

**Justificativa:**
- Evita expor estrutura interna do banco de dados
- Permite controlar exatamente quais dados trafegam na API
- Facilita versionamento da API sem quebrar clientes

**Impacto:** Mais classes, mas maior segurança e flexibilidade.

## 5. Regras de Negócio Implementadas

### Gerenciamento de Cursos
- Cursos possuem limite de vagas
- Apenas professores/admins podem criar cursos
- Cursos podem ter pré-requisitos

### Matrículas
- Aluno não pode se matricular em curso sem vagas
- Não pode haver matrícula duplicada no mesmo curso
- Validação de pré-requisitos antes da matrícula
- Cálculo automático de progresso do aluno

### Controle de Acesso
- Três perfis: ADMIN, PROFESSOR, ALUNO
- Permissões diferentes por perfil
- Autenticação obrigatória para operações sensíveis

## 6. Instruções para Execução

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Configuração do Banco de Dados

O projeto utiliza **H2 Database em memória**, que não requer instalação ou configuração adicional. O banco de dados é criado automaticamente ao iniciar a aplicação.

### Execução

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/sistema-gerenciamento-cursos.git
cd sistema-gerenciamento-cursos
```

2. **Compile o projeto:**
```bash
mvn clean install
```

3. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

4. **Acesse a aplicação:**
- **Interface Web:** `http://localhost:8080` ⭐
- **API REST:** `http://localhost:8080/api`
- **Console H2:** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:gerenciamento_cursos`
  - Username: `sa`
  - Password: *(deixe em branco)*

### Interface Web

O sistema possui uma **interface web completa** para gerenciamento visual:

**Funcionalidades:**
- 📊 **Dashboard:** Estatísticas em tempo real (total de cursos, alunos, matrículas)
- 📚 **Gerenciamento de Cursos:** Criar, editar, excluir e visualizar cursos
- 👥 **Gerenciamento de Alunos:** CRUD completo de alunos
- 📝 **Gerenciamento de Matrículas:** Matricular alunos, atualizar progresso, cancelar matrículas
- 🎯 **Filtros e Buscas:** Filtrar matrículas por aluno ou curso
- 📱 **Design Responsivo:** Funciona em desktop, tablet e mobile

**Estrutura Frontend:**
```
src/main/resources/static/
├── index.html          # Página principal
├── css/
│   └── style.css       # Estilos da aplicação
└── js/
    └── app.js          # Lógica e integração com API
```

**Acesse diretamente:** Após iniciar a aplicação, abra o navegador em `http://localhost:8080`

### Exemplos de Endpoints

#### Cursos
- `GET /api/cursos` - Listar todos os cursos
- `GET /api/cursos/{id}` - Buscar curso por ID
- `POST /api/cursos` - Criar novo curso
- `PUT /api/cursos/{id}` - Atualizar curso
- `DELETE /api/cursos/{id}` - Deletar curso

#### Alunos
- `GET /api/alunos` - Listar todos os alunos
- `GET /api/alunos/{id}` - Buscar aluno por ID
- `POST /api/alunos` - Criar novo aluno
- `GET /api/alunos/{id}/progresso` - Ver progresso do aluno

#### Matrículas
- `POST /api/matriculas` - Realizar matrícula
- `GET /api/matriculas/aluno/{alunoId}` - Listar matrículas de um aluno
- `DELETE /api/matriculas/{id}` - Cancelar matrícula

### Exemplo de Requisição (criar curso)

```bash
POST http://localhost:8080/api/cursos
Content-Type: application/json

{
  "nome": "Introdução à Arquitetura de Software",
  "descricao": "Curso sobre padrões e estilos arquiteturais",
  "cargaHoraria": 60,
  "vagas": 30,
  "professorId": 1
}
```

## 7. Testes

Execute os testes com:
```bash
mvn test
```

## 8. Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── gerenciamento/
│   │           └── cursos/
│   │               ├── GerenciamentoCursosApplication.java
│   │               ├── controller/
│   │               │   ├── CursoController.java
│   │               │   ├── AlunoController.java
│   │               │   ├── MatriculaController.java
│   │               │   └── UsuarioController.java
│   │               ├── service/
│   │               │   ├── CursoService.java
│   │               │   ├── AlunoService.java
│   │               │   ├── MatriculaService.java
│   │               │   └── UsuarioService.java
│   │               ├── repository/
│   │               │   ├── CursoRepository.java
│   │               │   ├── AlunoRepository.java
│   │               │   ├── MatriculaRepository.java
│   │               │   └── UsuarioRepository.java
│   │               ├── model/
│   │               │   ├── Curso.java
│   │               │   ├── Aluno.java
│   │               │   ├── Matricula.java
│   │               │   └── Usuario.java
│   │               ├── dto/
│   │               │   ├── CursoDTO.java
│   │               │   ├── AlunoDTO.java
│   │               │   └── MatriculaDTO.java
│   │               └── exception/
│   │                   └── BusinessException.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/
            └── gerenciamento/
                └── cursos/
                    └── service/
                        └── CursoServiceTest.java
```

## 9. Integrantes do Projeto

- **David Couto Bitencourt**

## 10. Conclusão

Este projeto demonstra a aplicação prática de conceitos de arquitetura de software em um contexto realista. A escolha por uma arquitetura monolítica em camadas permite clareza na separação de responsabilidades, facilitando manutenção e evolução futura do sistema.

A implementação prioriza **baixo acoplamento**, **alta coesão** e **single responsibility principle**, fundamentais para um software sustentável e de qualidade.

---

**Licença:** MIT  
**Data de Entrega:** Janeiro de 2026  
**Disciplina:** Introdução à Arquitetura de Software
