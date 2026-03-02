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

### Arquitetura Monolítica em Camadas

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

## 6. Infraestrutura, Escalabilidade e Sessão Distribuída

### 6.1. Containerização e Orquestração

O sistema é executado via Docker Compose, com três serviços principais:
- **app**: instâncias do backend Spring Boot (Java 17)
- **nginx**: proxy reverso e balanceador de carga
- **db**: banco de dados PostgreSQL

Exemplo de escala horizontal:
```bash
docker-compose up --build --scale app=3
```
O Nginx distribui as requisições entre múltiplas instâncias do app.

### 6.2. Persistência de Sessão Distribuída (Spring Session JDBC + PostgreSQL)

**Problema:**
Por padrão, sessões HTTP são armazenadas em memória local. Isso impede o compartilhamento de sessão entre instâncias (ex: login feito em uma instância não é reconhecido em outra).

**Solução:**
- Utilização do [Spring Session JDBC](https://docs.spring.io/spring-session/reference/jdbc.html) para persistir sessões no PostgreSQL.
- As tabelas `SPRING_SESSION` e `SPRING_SESSION_ATTRIBUTES` são criadas automaticamente ou via script SQL.
- Todas as instâncias do app compartilham a mesma tabela de sessão, permitindo login único e logout global.

**Configuração relevante:**
```properties
# application.properties
spring.session.store-type=jdbc
spring.session.jdbc.initialize-schema=always
spring.datasource.url=jdbc:postgresql://db:5432/gerenciamento_cursos
spring.datasource.username=postgres
spring.datasource.password=postgres
```

**Infraestrutura:**
- O serviço `app` depende do `db` e só inicia após o banco estar saudável (healthcheck).
- O script `schema-postgresql.sql` pode ser usado para criar as tabelas manualmente, caso necessário.

**Testando sessão distribuída:**
1. Suba múltiplas instâncias:
   ```bash
   docker-compose up --build --scale app=3
   ```
2. Faça login via navegador ou curl.
3. Acesse diferentes endpoints repetidamente (F5 ou múltiplas abas).
4. Observe que a sessão é mantida mesmo alternando entre instâncias (veja logs com o campo `instance`).

**Dicas de troubleshooting:**
- Se as tabelas de sessão não forem criadas automaticamente, execute manualmente:
  ```bash
  docker cp src/main/resources/schema-postgresql.sql caixa-verso-gerenciamento-de-cursos-db-1:/schema-postgresql.sql
  docker-compose exec db psql -U postgres -d gerenciamento_cursos -f /schema-postgresql.sql
  ```
- Verifique as tabelas no banco:
  ```bash
  docker-compose exec db psql -U postgres -d gerenciamento_cursos -c "\dt"
  ```

---

## 7. Execução

### Pré-requisitos
- **Docker** (e Docker Compose) – requisito principal para execução rápida
- *(opcional)* Java 17 e Maven caso deseje rodar localmente sem container
- IDE (IntelliJ IDEA, Eclipse, VS Code) para desenvolvimento

### Configuração do Banco de Dados

O projeto utiliza **H2 Database em memória** durante desenvolvimento; nenhuma instalação é necessária e o banco é inicializado automaticamente pela aplicação. Em ambiente containerizado, a mesma configuração é usada sem ajustes adicionais.

### Execução

> 📌 **Observações de infraestrutura (requisitos do professor):**
>
> * O Nginx atua como **reverse proxy** e **load balancer**, escutando na porta 80.
> * A API Spring Boot roda em container separado e **não expõe** sua porta 8080
>   ao host. Assim, `http://localhost:8080` não responde, mostrando que apenas o
>   proxy é acessível.
> * O Nginx adiciona *headers de segurança* em todas as respostas:
>   `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY` e
>   `X-XSS-Protection: 1; mode=block`.
> * A comunicação entre Nginx e API se dá pela rede Docker; a escala de réplicas
>   (`--scale app=N`) e health checks garantem controle de tráfego/robustez.
>
> Estes requisitos são testáveis via navegador ou ferramentas como Postman.

### Execução

1. **Clone o repositório:**
```bash
git clone https://github.com/seu-usuario/sistema-gerenciamento-cursos.git
cd sistema-gerenciamento-cursos
```

2. **Compile e rode com Docker Compose (recomendado):**
```bash
# ao puxar o repositório, basta executar na raiz
docker-compose up --build
```

Este comando compila a aplicação dentro de um contêiner Maven (multi‑stage build), cria as imagens e sobe ambos os serviços `app` e `nginx` já configurados.

> 🔧 Não há necessidade de ter Java ou Maven instalados na máquina — apenas Docker.

3. **Execução local opcional:**
Se preferir rodar sem containers para desenvolvimento rápido:
```bash
mvn clean install
mvn spring-boot:run
``` 

(estas etapas exigem Java 17 e Maven na máquina)

4. **Acesse a aplicação via proxy:**
- **Interface Web:** `http://localhost` ⭐
- **API REST:** `http://localhost/api`
- **Console H2:** `http://localhost/h2-console`
  - JDBC URL: `jdbc:h2:mem:gerenciamento_cursos`
  - Username: `sa`
  - Password: *(deixe em branco)*

> ⚠️ A porta 8080 da API não é exposta ao host; acessos diretos (por exemplo
> `http://localhost:8080`) devem falhar, deixando claro que apenas o proxy serve
> como ponto de entrada.

### 🔁 Proxy reverso com Nginx

O projeto inclui um exemplo de configuração para que um servidor **Nginx** funcione como *reverse proxy* na frente da aplicação Spring Boot.

> 🛡️ O Nginx também injeta cabeçalhos de segurança em todas as respostas:
> `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY` e
> `X-XSS-Protection: 1; mode=block`, verificáveis via navegador/Postman.

> ⚖️ O balanceamento round‑robin e health checks permitem escalar o serviço e
> garantir que containers doentes não recebam tráfego (documentado mais adiante).

> 🚦 **Rate limiting:** a camada de proxy aplica um limite de **5 requisições por
> segundo por IP** com burst de 10. Excedendo essa taxa o cliente recebe HTTP 429
> (Too Many Requests), demonstrando controle de tráfego.
>
> 🔐 **Autenticação básica:** os caminhos `/swagger` e `/actuator` estão protegidos
> por autenticação básica HTTP, com credencial `admin:secret` (arquivo
> `nginx.htpasswd`). Ao acessar sem credenciais você receberá 401, com credenciais
> corretamente poderá visualizar o recurso.
> 
> ❗ **Páginas de erro personalizadas:** o proxy serve páginas HTML estáticas para
> 404 (`custom_404.html`) e para erros 50x (`custom_50x.html`). Simplesmente
> solicite um recurso inexistente ou pare o container `app` para forçar o erro e
> ver a mensagem amigável.
> 
> Para gerar seu próprio usuário/senha use o utilitário `htpasswd`:
> ```bash
> htpasswd -c nginx.htpasswd usuario
> ```
> O arquivo deve ser montado no serviço Nginx via Docker Compose.
>
> **Teste:** gere requisições rápidas e observe o código 429 retornado. Por exemplo:
>
> ```bash
> for i in {1..20}; do curl -s -o /dev/null -w "%{http_code} \n" http://localhost; done
> ```
>
> Você verá vários `200` inicialmente, seguidos por `429` quando o burst/limite é
> ultrapassado. Com ferramentas Windows (PowerShell) o mesmo efeito pode ser obtido
> via loop `Invoke-WebRequest` e verificando `StatusCode`.
>
> 🚫 **Limite de payload:** o proxy também impõe `client_max_body_size 1m`. Se um
> cliente enviar mais de 1 MB em uma única requisição ele receberá HTTP 413 (Payload
> Too Large). Use `curl --data-binary @arquivo_grande` para testar.
🎁 **GZIP:** a compressão está ativada para tipos `application/json` e `text/plain`.
Respostas desses tipos virão com o cabeçalho `Content-Encoding: gzip`, reduzindo o
uso de banda. Teste com `curl -I` e veja o header.

📝 **Logs estruturados:** o Nginx grava as seguintes informações em cada linha do
access log:

- IP do cliente (`$remote_addr`)
- Método HTTP (`$request_method`)
- Código de status (`$status`)
- Endereço do upstream atendente (`$upstream_addr`)
- Tempo de resposta do upstream (`$upstream_response_time`)

Exemplo real de linha de log (após gerar requisições):

```
127.0.0.1 - [02/Mar/2026:10:15:00 +0000] "GET / HTTP/1.1" status:200 upstream:app_1:8080 utime:0.002
`

Para ver os logs no container execute (em sua máquina):

```bash
docker-compose exec nginx tail -f /var/log/nginx/access.log
```

Isso ajuda a correlacionar tráfego, status e performance dos backends.

🗂️ **Cache de GET:** respostas a requisições GET são armazenadas em cache por 10
segundos. O cabeçalho `X-Cache-Status` indica se a requisição foi um _HIT_ ou _MISS_.
Basta fazer duas chamadas rápidas ao mesmo endpoint para observar o comportamento:

```bash
curl -I http://localhost/api/cursos; sleep 1; curl -I http://localhost/api/cursos
```

A segunda resposta deve mostrar `X-Cache-Status: HIT` se bem-sucedida.

🧵 **Request ID / correlação:** O Nginx injeta um header `X-Request-ID` com um
identificador único (ou reaproveita o enviado pelo cliente) em cada requisição.
Essa mesma ID é passada ao backend e registrada nos logs da aplicação via MDC.
O padrão de log foi ajustado (`%X{requestId}`) para facilitar correlação.
Para ver no backend, consulte o console do Spring Boot após fazer requisições e
observe o valor entre colchetes no início de cada linha.

🔁 **Balanceamento com 2 instâncias:** para demonstrar o comportamento em um
cenário com duas réplicas basta iniciar os serviços com escala:

```bash
# compilar e subir com duas instâncias do app
docker-compose up --build --scale app=2
```

O Nginx enviará requisições alternadamente entre as duas instâncias; essa ação
prova o requisito de balanceamento opcional.
1. **Configuração:** copie o arquivo `nginx.conf` localizado na raiz do repositório para o diretório de configuração do Nginx (`/etc/nginx/nginx.conf` em instalações Linux) ou mantenha-o junto ao `docker-compose.yml`.

```nginx
# conteúdo de nginx.conf provido no repositório
# (o upstream aponta para a aplicação ou para o serviço Docker `app`)
events {}
http {
    upstream backend {
        server app:8080;    # ou localhost:8080 quando rodando localmente
    }

    server {
        listen 80;
        server_name localhost;

        location / {
            proxy_pass http://backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

2. **Docker (opcional):** o arquivo `docker-compose.yml` já traz um serviço `nginx` que monta essa configuração e expõe a porta 80.

```bash
# gerar o artefato Java e subir os contêineres
mvn clean package
docker-compose up --build
```

3. **Execução manual:** sem Docker, instale o Nginx em sua máquina e reinicie o serviço após copiar o `nginx.conf`:

```bash
sudo cp nginx.conf /etc/nginx/nginx.conf
sudo systemctl restart nginx
```

> O proxy permite receber requisições na porta 80, aplicar SSL/caching e repassá‑las à aplicação em 8080.

### 🔁 Balanceamento de carga

Com o Docker Compose é simples criar múltiplas réplicas do serviço e deixar o Nginx distribuir as requisições.

1. Inicie os contêineres com escala, por exemplo três instâncias do app:

```bash
docker-compose up --build --scale app=3
```

2. O Nginx (já configurado com `resolver 127.0.0.11`) fará DNS round‑robin entre os IPs retornados pelo serviço `app`
   e assim balanceará as requisições automaticamente.

> Se não usar Docker, crie manualmente múltiplas instâncias da aplicação e liste-as em `upstream backend`.

### 🩺 Health checks

Além do balanceador, o contêiner da aplicação agora possui uma **verificação de saúde Docker** que consulta o endpoint `/actuator/health`. O Spring Boot expõe esse endpoint graças ao **Spring Boot Actuator** (dependência adicionada ao `pom.xml`).

- O `docker-compose.yml` inclui:

```yaml
healthcheck:
  test: ["CMD-SHELL", "curl -f http://localhost:8080/actuator/health || exit 1"]
  interval: 30s
  timeout: 10s
  retries: 3
```

- A propriedade `management.endpoints.web.exposure.include=health,info` em `application.properties` garante que o serviço responda.

Se uma instância ficar com a saúde ruim, o Docker marca o contêiner como `unhealthy` e o Nginx (graças a `max_fails`/`fail_timeout`) deixará de encaminhar requisições para ele, permitindo que o orquestrador reinicie automaticamente a unidade.

### 🔐 Autenticação e Controle de Acesso

O sistema implementa **controle de acesso baseado em perfis** (RBAC) com Spring Security. Autenticação obrigatória para acessar as funcionalidades.

**Credenciais de Teste:**

| Perfil | Email | Senha |
|--------|-------|-------|
| **Administrador** | `admin@escola.com` | `admin123` |
| **Professor** | `joao.silva@escola.com` | `prof123` |
| **Aluno** | `aluno@escola.com` | `aluno123` |

**Matriz de Permissões:**

| Recurso | ADMIN | PROFESSOR | ALUNO |
|---------|-------|-----------|-------|
| **Dashboard** | ✅ Ver estatísticas | ✅ Ver estatísticas | ✅ Ver estatísticas |
| **Cursos** | ✅ CRUD completo | ✅ CRUD completo | 👁️ Apenas visualizar |
| **Alunos** | ✅ CRUD completo | ✅ CRUD completo | ❌ Sem acesso |
| **Professores** | ✅ CRUD completo | ❌ Sem acesso | ❌ Sem acesso |
| **Matrículas** | ✅ CRUD completo | ✅ CRUD completo | 👁️ Apenas visualizar |

**Segurança Implementada:**
- ✅ Backend: Endpoints protegidos por perfil (Spring Security)
- ✅ Frontend: Interface adapta-se ao perfil do usuário
- ✅ Senhas criptografadas com BCrypt
- ✅ Sessões autenticadas via Spring Security

> **⚠️ Nota:** Credenciais de teste para ambiente de desenvolvimento. Em produção, usar senhas fortes e desabilitar seed de dados.

### 🎲 Dados de Exemplo (Seed)

O sistema **cria automaticamente dados de exemplo** ao iniciar pela primeira vez, facilitando a demonstração:

**Dados criados:**
- ✅ **3 Professores:** João Silva, Maria Santos, Carlos Oliveira
- ✅ **9 Alunos:** Com nomes, emails, CPFs e telefones
- ✅ **5 Cursos:** Arquitetura de Software, Spring Boot, Banco de Dados, Metodologias Ágeis, DevOps
- ✅ **13 Matrículas:** Com status variados (ativa, concluída, cancelada) e diferentes progressos

**Vantagens:**
- Não precisa cadastrar dados manualmente
- Sistema pronto para demonstração imediata
- Testa todas as funcionalidades com dados reais

### Interface Web

O sistema possui uma **interface web completa** para gerenciamento visual:

**Funcionalidades:**
- 📊 **Dashboard:** Estatísticas em tempo real (total de cursos, alunos, matrículas)
- 📚 **Gerenciamento de Cursos:** Criar, editar, excluir e visualizar cursos
- 👥 **Gerenciamento de Alunos:** CRUD completo de alunos
- �‍🏫 **Gerenciamento de Professores:** CRUD completo de professores/usuários
- 📝 **Gerenciamento de Matrículas:** Matricular alunos, atualizar progresso, cancelar e reativar matrículas
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

## 🗃️ Sessão distribuída e escalabilidade (Didático)
> **Atenção:**
> Para que o Spring Session JDBC funcione corretamente, é fundamental garantir que o schema das tabelas de sessão seja criado automaticamente no banco H2. Isso é feito adicionando a linha abaixo ao `application.properties`:
>
> ```properties
> spring.datasource.schema=classpath:org/springframework/session/jdbc/schema-h2.sql
> ```
>
> Sem isso, a aplicação apresentará erro de "Table SPRING_SESSION not found" e ficará unhealthy em ambiente Docker.

Quando a aplicação é executada com múltiplas instâncias (ex: `docker-compose up --build --scale app=3`), cada container mantém sessões de usuário apenas em sua própria memória. Isso significa que, sem configuração extra, o login pode ser "perdido" ao alternar entre instâncias, pois cada uma não compartilha o estado de autenticação.

**Para resolver esse problema em ambientes escaláveis, é necessário usar um gerenciador de sessão distribuída**, como:
- Spring Session com JDBC (banco relacional)
- Spring Session com Redis (cache distribuído)
- Spring Session com Hazelcast (in-memory grid)

Neste projeto, foi adotado o **Spring Session JDBC** usando o próprio banco H2, permitindo que todas as instâncias compartilhem as sessões de login. Assim, o usuário permanece autenticado mesmo com balanceamento de carga.

> **Resumo didático:**
> - Monolitos simples: sessão em memória (não escala)
> - Sistemas escaláveis: sessão distribuída (JDBC, Redis, etc.)
> - Spring Session resolve o problema de forma transparente

Essa abordagem é fundamental para aplicações modernas que precisam de alta disponibilidade e escalabilidade horizontal.

Este projeto demonstra a aplicação prática de conceitos de arquitetura de software em um contexto realista. A escolha por uma arquitetura monolítica em camadas permite clareza na separação de responsabilidades, facilitando manutenção e evolução futura do sistema.

A implementação prioriza **baixo acoplamento**, **alta coesão** e **single responsibility principle**, fundamentais para um software sustentável e de qualidade.

---

**Licença:** MIT  
**Data de Entrega:** Março de 2026  
**Disciplina:** Introdução à Arquitetura de Software

---

## 📦 Evidências

Os comandos demonstrados ao longo do README servem como provas de funcionamento. Entre os principais estão:

```bash
# subir aplicação com proxy e balanceamento
docker-compose up --build --scale app=2

# testar rate limiting (retornar 429)
for i in {1..20}; do curl -s -o /dev/null -w "%{http_code} \n" http://localhost; done

# testar limite de payload (retornar 413)
head -c 1500000 /dev/urandom > big.bin
curl -i --data-binary @big.bin http://localhost

# testar cache e microcache
curl -I http://localhost/api/cursos; sleep 1; curl -I http://localhost/api/cursos

# visualizar cache header
curl -I http://localhost/api/cursos | grep -i X-Cache-Status

# verificar gzip
curl -I http://localhost/api/cursos -H 'Accept-Encoding: gzip'

# verificar X-Request-ID
curl -I http://localhost/api/cursos | grep -i X-Request-ID

# forçar 404 / 50x
curl -i http://localhost/naoencontre

docker-compose stop app; curl -i http://localhost

# autenticação básica
curl -I http://localhost/swagger           # sem credenciais
curl -I -u admin:secret http://localhost/swagger  # com credenciais

# checar health check
curl http://localhost/actuator/health
```

Você também pode inspecionar os logs do Nginx e da aplicação via:

```bash
docker-compose logs --follow nginx
# e
# (no backend) logs já aparecem no console com requestId
```

