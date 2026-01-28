const API_URL = 'http://localhost:8080/api';

// Caches para dados
let professoresCache = [];
let alunosCache = [];
let cursosCache = [];
let alunosOptions = [];
let cursosOptions = [];
let currentUser = null; // Informações do usuário logado

// Verificação de autenticação e carregamento de usuário
async function checkAuthentication() {
    try {
        const response = await fetch(`${API_URL}/auth/me`);
        if (!response.ok) {
            window.location.href = '/login.html';
            return;
        }
        
        currentUser = await response.json();
        console.log('Usuário logado:', currentUser);
        
        // Configura menus baseado no perfil
        setupMenusByRole();
        
    } catch (error) {
        console.error('Erro ao verificar autenticação:', error);
        window.location.href = '/login.html';
    }
}

// Configura visibilidade dos menus baseado no perfil
function setupMenusByRole() {
    const menuItems = {
        'dashboard': ['ADMIN', 'PROFESSOR', 'ALUNO'],
        'cursos': ['ADMIN', 'PROFESSOR', 'ALUNO'],
        'alunos': ['ADMIN', 'PROFESSOR'],
        'professores': ['ADMIN'],
        'matriculas': ['ADMIN', 'PROFESSOR', 'ALUNO']
    };
    
    const userRole = currentUser.tipo;
    
    // Oculta menus que o usuário não tem acesso
    document.querySelectorAll('.nav-menu a').forEach(link => {
        const onclick = link.getAttribute('onclick');
        if (onclick) {
            const match = onclick.match(/showSection\('(\w+)'\)/);
            if (match) {
                const section = match[1];
                if (menuItems[section] && !menuItems[section].includes(userRole)) {
                    link.style.display = 'none';
                }
            }
        }
    });
    
    // Adiciona nome do usuário no menu
    const navMenu = document.querySelector('.nav-menu');
    const userInfo = document.createElement('li');
    userInfo.style.color = '#6b7280';
    userInfo.style.fontSize = '0.875rem';
    userInfo.innerHTML = `<span style="color: #4f46e5; font-weight: 500;">${currentUser.nome}</span> (${getRoleLabel(userRole)})`;
    navMenu.insertBefore(userInfo, navMenu.lastElementChild);
    
    // Mostra botões de criação baseado nas permissões
    if (hasPermission('ADMIN', 'PROFESSOR')) {
        document.getElementById('btnNovoCurso').style.display = 'inline-block';
        document.getElementById('btnNovoAluno').style.display = 'inline-block';
        document.getElementById('btnNovaMatricula').style.display = 'inline-block';
    }
    
    if (hasPermission('ADMIN')) {
        document.getElementById('btnNovoProfessor').style.display = 'inline-block';
    }
}

function getRoleLabel(role) {
    const labels = {
        'ADMIN': 'Administrador',
        'PROFESSOR': 'Professor',
        'ALUNO': 'Aluno'
    };
    return labels[role] || role;
}

// Verifica se usuário tem permissão
function hasPermission(...roles) {
    return currentUser && roles.includes(currentUser.tipo);
}

// Função de logout
async function logout() {
    if (!confirm('Deseja realmente sair do sistema?')) return;
    
    try {
        await fetch(`${API_URL}/auth/logout`, { method: 'POST' });
        window.location.href = '/login.html';
    } catch (error) {
        console.error('Erro ao fazer logout:', error);
        window.location.href = '/login.html';
    }
}

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication();
    loadDashboard();
    loadCursos();
    loadAlunos();
    loadProfessoresTabela();
    setupForms();
    setupFilters();
    loadProfessores();
});

// Navegação
function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => {
        section.classList.remove('active');
    });
    document.querySelectorAll('.nav-menu a').forEach(link => {
        link.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');
    
    // Ativar o link do menu correspondente
    const activeLink = document.querySelector(`.nav-menu a[onclick*="${sectionId}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }

    // Carregar dados da seção
    if (sectionId === 'cursos') loadCursos();
    if (sectionId === 'alunos') loadAlunos();
    if (sectionId === 'professores') loadProfessoresTabela();
    if (sectionId === 'matriculas') loadMatriculas();
    if (sectionId === 'dashboard') loadDashboard();
}

// Dashboard
async function loadDashboard() {
    try {
        const [cursos, alunos, professores, cursosComVagas] = await Promise.all([
            fetch(`${API_URL}/cursos`).then(r => r.json()),
            fetch(`${API_URL}/alunos`).then(r => r.json()),
            fetch(`${API_URL}/usuarios/professores`).then(r => r.json()),
            fetch(`${API_URL}/cursos/disponiveis`).then(r => r.json())
        ]);

        document.getElementById('totalCursos').textContent = cursos.length;
        document.getElementById('totalAlunos').textContent = alunos.length;
        document.getElementById('totalProfessores').textContent = professores.length;
        document.getElementById('cursosComVagas').textContent = cursosComVagas.length;

        // Calcular total de matrículas
        let totalMatriculas = 0;
        cursos.forEach(curso => {
            totalMatriculas += curso.totalMatriculas || 0;
        });
        document.getElementById('totalMatriculas').textContent = totalMatriculas;
    } catch (error) {
        console.error('Erro ao carregar dashboard:', error);
        showMessage('Erro ao carregar dashboard', 'error');
    }
}

// PROFESSORES
async function loadProfessores() {
    try {
        const response = await fetch(`${API_URL}/usuarios/professores`);
        const professores = await response.json();
        professoresCache = professores;
    } catch (error) {
        professoresCache = [];
    }
}

async function loadProfessoresTabela() {
    try {
        const response = await fetch(`${API_URL}/usuarios/professores`);
        const professores = await response.json();
        professoresCache = professores;
        const tbody = document.getElementById('professoresTableBody');
        
        if (professores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="loading">Nenhum professor cadastrado</td></tr>';
            return;
        }

        tbody.innerHTML = professores.map(professor => `
            <tr>
                <td>${professor.id}</td>
                <td><strong>${professor.nome}</strong></td>
                <td>${professor.email}</td>
                <td><span class="badge ${professor.ativo ? 'badge-success' : 'badge-danger'}">${professor.ativo ? 'Ativo' : 'Inativo'}</span></td>
                <td>
                    ${hasPermission('ADMIN') ? `
                    <div class="action-buttons">
                        <button class="btn btn-warning" onclick="editProfessor(${professor.id})">Editar</button>
                        <button class="btn btn-danger" onclick="deleteProfessor(${professor.id})">Excluir</button>
                    </div>
                    ` : '<span style="color: #6b7280;">Sem permissão</span>'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar professores:', error);
        showMessage('Erro ao carregar professores', 'error');
    }
}

function showProfessorModal(professor = null) {
    const modal = document.getElementById('professorModal');
    const form = document.getElementById('professorForm');
    const title = document.getElementById('professorModalTitle');
    
    form.reset();
    
    if (professor) {
        title.textContent = 'Editar Professor';
        document.getElementById('professorId').value = professor.id;
        document.getElementById('professorNome').value = professor.nome;
        document.getElementById('professorEmail').value = professor.email;
        document.getElementById('professorSenha').required = false;
        document.getElementById('professorSenha').placeholder = 'Deixe em branco para manter a senha atual';
    } else {
        title.textContent = 'Novo Professor';
        document.getElementById('professorId').value = '';
        document.getElementById('professorSenha').required = true;
        document.getElementById('professorSenha').placeholder = '';
    }
    
    modal.classList.add('active');
}

async function editProfessor(id) {
    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`);
        const professor = await response.json();
        showProfessorModal(professor);
    } catch (error) {
        console.error('Erro ao buscar professor:', error);
        showMessage('Erro ao buscar professor', 'error');
    }
}

async function deleteProfessor(id) {
    if (!confirm('Deseja realmente excluir este professor?')) return;
    
    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showMessage('Professor excluído com sucesso!', 'success');
            loadProfessoresTabela();
            loadProfessores();
        } else {
            throw new Error('Erro ao excluir professor');
        }
    } catch (error) {
        console.error('Erro ao excluir professor:', error);
        showMessage('Erro ao excluir professor', 'error');
    }
}

function populateProfessorSelect() {
    const select = document.getElementById('cursoProfessorId');
    select.innerHTML = '<option value="">Selecione um professor...</option>';
    
    professoresCache.forEach(prof => {
        const option = document.createElement('option');
        option.value = prof.id;
        option.textContent = prof.nome;
        select.appendChild(option);
    });
}

function populateAlunoSelect() {
    const select = document.getElementById('matriculaAlunoId');
    select.innerHTML = '<option value="">Selecione um aluno...</option>';
    
    alunosCache.forEach(aluno => {
        const option = document.createElement('option');
        option.value = aluno.id;
        option.textContent = `${aluno.nome} - ${aluno.email}`;
        select.appendChild(option);
    });
}

function populateCursoSelect() {
    const select = document.getElementById('matriculaCursoId');
    select.innerHTML = '<option value="">Selecione um curso...</option>';
    
    cursosCache
        .filter(curso => curso.ativo && curso.vagasDisponiveis > 0)
        .forEach(curso => {
            const option = document.createElement('option');
            option.value = curso.id;
            option.textContent = `${curso.nome} (${curso.vagasDisponiveis} vagas)`;
            select.appendChild(option);
        });
}

function filterSelect(selectId, searchText, type) {
    const select = document.getElementById(selectId);
    const options = type === 'aluno' ? alunosOptions : cursosOptions;
    const search = searchText.toLowerCase();
    
    select.innerHTML = '<option value="">Selecione...</option>';
    
    const filtered = options.filter(opt => opt.searchText.includes(search));
    
    filtered.forEach(opt => {
        const option = document.createElement('option');
        option.value = opt.value;
        option.textContent = opt.text;
        option.dataset.search = opt.searchText;
        select.appendChild(option);
    });
}

// CURSOS
async function loadCursos() {
    try {
        const response = await fetch(`${API_URL}/cursos`);
        const cursos = await response.json();
        cursosCache = cursos;
        const tbody = document.getElementById('cursosTableBody');
        
        if (cursos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="loading">Nenhum curso cadastrado</td></tr>';
            return;
        }

        tbody.innerHTML = cursos.map(curso => `
            <tr>
                <td>${curso.id}</td>
                <td><strong>${curso.nome}</strong></td>
                <td>${curso.cargaHoraria}h</td>
                <td>${curso.vagas}</td>
                <td>${curso.vagasDisponiveis}</td>
                <td><span class="badge ${curso.ativo ? 'badge-success' : 'badge-danger'}">${curso.ativo ? 'Ativo' : 'Inativo'}</span></td>
                <td>
                    <div class="action-buttons">
                        ${hasPermission('ADMIN', 'PROFESSOR') ? `
                            <button class="btn btn-warning" onclick="editCurso(${curso.id})">Editar</button>
                            <button class="btn btn-danger" onclick="deleteCurso(${curso.id})">Excluir</button>
                        ` : '<span style="color: #6b7280; font-size: 0.875rem;">Visualização</span>'}
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar cursos:', error);
        showMessage('Erro ao carregar cursos', 'error');
    }
}

function showCursoModal(curso = null) {
    const modal = document.getElementById('cursoModal');
    const form = document.getElementById('cursoForm');
    const title = document.getElementById('cursoModalTitle');
    
    form.reset();
    populateProfessorSelect();
    
    if (curso) {
        title.textContent = 'Editar Curso';
        document.getElementById('cursoId').value = curso.id;
        document.getElementById('cursoNome').value = curso.nome;
        document.getElementById('cursoDescricao').value = curso.descricao || '';
        document.getElementById('cursoCargaHoraria').value = curso.cargaHoraria;
        document.getElementById('cursoVagas').value = curso.vagas;
        document.getElementById('cursoProfessorId').value = curso.professorId;
    } else {
        title.textContent = 'Novo Curso';
        document.getElementById('cursoId').value = '';
    }
    
    modal.classList.add('active');
}

async function editCurso(id) {
    try {
        const response = await fetch(`${API_URL}/cursos/${id}`);
        const curso = await response.json();
        showCursoModal(curso);
    } catch (error) {
        console.error('Erro ao buscar curso:', error);
        showMessage('Erro ao buscar curso', 'error');
    }
}

async function deleteCurso(id) {
    if (!confirm('Deseja realmente excluir este curso?')) return;
    
    try {
        const response = await fetch(`${API_URL}/cursos/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showMessage('Curso excluído com sucesso!', 'success');
            loadCursos();
            loadDashboard();
        } else {
            throw new Error('Erro ao excluir curso');
        }
    } catch (error) {
        console.error('Erro ao excluir curso:', error);
        showMessage('Erro ao excluir curso', 'error');
    }
}

// ALUNOS
async function loadAlunos() {
    try {
        const response = await fetch(`${API_URL}/alunos`);
        const alunos = await response.json();
        alunosCache = alunos;
        const tbody = document.getElementById('alunosTableBody');
        
        if (alunos.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="loading">Nenhum aluno cadastrado</td></tr>';
            return;
        }

        tbody.innerHTML = alunos.map(aluno => `
            <tr>
                <td>${aluno.id}</td>
                <td><strong>${aluno.nome}</strong></td>
                <td>${aluno.email}</td>
                <td>${formatCPF(aluno.cpf)}</td>
                <td>${aluno.telefone || '-'}</td>
                <td><span class="badge ${aluno.ativo ? 'badge-success' : 'badge-danger'}">${aluno.ativo ? 'Ativo' : 'Inativo'}</span></td>
                <td>
                    ${hasPermission('ADMIN', 'PROFESSOR') ? `
                    <div class="action-buttons">
                        <button class="btn btn-warning" onclick="editAluno(${aluno.id})">Editar</button>
                        <button class="btn btn-danger" onclick="deleteAluno(${aluno.id})">Excluir</button>
                    </div>
                    ` : '<span style="color: #6b7280;">Visualização</span>'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar alunos:', error);
        showMessage('Erro ao carregar alunos', 'error');
    }
}

function showAlunoModal(aluno = null) {
    const modal = document.getElementById('alunoModal');
    const form = document.getElementById('alunoForm');
    const title = document.getElementById('alunoModalTitle');
    
    form.reset();
    
    if (aluno) {
        title.textContent = 'Editar Aluno';
        document.getElementById('alunoId').value = aluno.id;
        document.getElementById('alunoNome').value = aluno.nome;
        document.getElementById('alunoEmail').value = aluno.email;
        document.getElementById('alunoCpf').value = aluno.cpf;
        document.getElementById('alunoTelefone').value = aluno.telefone || '';
    } else {
        title.textContent = 'Novo Aluno';
        document.getElementById('alunoId').value = '';
    }
    
    modal.classList.add('active');
}

async function editAluno(id) {
    try {
        const response = await fetch(`${API_URL}/alunos/${id}`);
        const aluno = await response.json();
        showAlunoModal(aluno);
    } catch (error) {
        console.error('Erro ao buscar aluno:', error);
        showMessage('Erro ao buscar aluno', 'error');
    }
}

async function deleteAluno(id) {
    if (!confirm('Deseja realmente excluir este aluno?')) return;
    
    try {
        const response = await fetch(`${API_URL}/alunos/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showMessage('Aluno excluído com sucesso!', 'success');
            loadAlunos();
            loadDashboard();
        } else {
            throw new Error('Erro ao excluir aluno');
        }
    } catch (error) {
        console.error('Erro ao excluir aluno:', error);
        showMessage('Erro ao excluir aluno', 'error');
    }
}

// MATRÍCULAS
async function loadMatriculas() {
    const filtroTipo = document.getElementById('filtroTipo').value;
    const filtroId = document.getElementById('filtroId').value;
    
    let url = `${API_URL}/matriculas`;
    
    if (filtroTipo === 'aluno' && filtroId) {
        url += `/aluno/${filtroId}`;
    } else if (filtroTipo === 'curso' && filtroId) {
        url += `/curso/${filtroId}`;
    }
    
    try {
        const response = await fetch(url);
        const matriculas = await response.json();
        const tbody = document.getElementById('matriculasTableBody');
        
        if (matriculas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="loading">Nenhuma matrícula encontrada</td></tr>';
            return;
        }

        tbody.innerHTML = matriculas.map(matricula => `
            <tr>
                <td>${matricula.id}</td>
                <td>${matricula.alunoNome || 'ID: ' + matricula.alunoId}</td>
                <td>${matricula.cursoNome || 'ID: ' + matricula.cursoId}</td>
                <td><span class="badge badge-${getStatusBadge(matricula.status)}">${matricula.status}</span></td>
                <td>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${matricula.progresso}%"></div>
                    </div>
                    <small>${matricula.progresso}%</small>
                </td>
                <td>${formatDate(matricula.dataMatricula)}</td>
                <td>
                    ${hasPermission('ADMIN', 'PROFESSOR') ? `
                    <div class="action-buttons">
                        ${matricula.status === 'ATIVA' ? `
                            <button class="btn btn-success" onclick="showProgressoModal(${matricula.id}, ${matricula.progresso})">Progresso</button>
                            <button class="btn btn-danger" onclick="cancelarMatricula(${matricula.id})">Cancelar</button>
                        ` : ''}
                        ${matricula.status === 'CANCELADA' ? `
                            <button class="btn btn-primary" onclick="reativarMatricula(${matricula.id})" title="Reativar esta matrícula">✨ Reativar</button>
                        ` : ''}
                    </div>
                    ` : '<span style="color: #6b7280;">Visualização</span>'}
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar matrículas:', error);
        showMessage('Erro ao carregar matrículas', 'error');
    }
}

function showMatriculaModal() {
    const modal = document.getElementById('matriculaModal');
    const form = document.getElementById('matriculaForm');
    form.reset();
    
    // Popular selects
    populateAlunoSelect();
    populateCursoSelect();
    
    modal.classList.add('active');
}

function showProgressoModal(matriculaId, progressoAtual) {
    const modal = document.getElementById('progressoModal');
    document.getElementById('progressoMatriculaId').value = matriculaId;
    document.getElementById('progressoValor').value = progressoAtual;
    document.getElementById('progressoSlider').value = progressoAtual;
    modal.classList.add('active');
}

async function cancelarMatricula(id) {
    if (!confirm('Deseja realmente cancelar esta matrícula?')) return;
    
    try {
        const response = await fetch(`${API_URL}/matriculas/${id}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            showMessage('Matrícula cancelada com sucesso!', 'success');
            loadMatriculas();
            loadDashboard();
        } else {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao cancelar matrícula');
        }
    } catch (error) {
        console.error('Erro ao cancelar matrícula:', error);
        showMessage('Erro ao cancelar matrícula', 'error');
    }
}

async function reativarMatricula(id) {
    if (!confirm('Deseja realmente reativar esta matrícula?')) return;
    
    try {
        const response = await fetch(`${API_URL}/matriculas/${id}/reativar`, {
            method: 'PATCH'
        });
        
        if (response.ok) {
            showMessage('Matrícula reativada com sucesso! ✨', 'success');
            loadMatriculas();
            loadDashboard();
        } else {
            const error = await response.json();
            throw new Error(error.message || 'Erro ao reativar matrícula');
        }
    } catch (error) {
        console.error('Erro ao reativar matrícula:', error);
        showMessage(error.message, 'error');
    }
}

// FORMS
function setupForms() {
    // Professor Form
    document.getElementById('professorForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const id = document.getElementById('professorId').value;
        const data = {
            nome: document.getElementById('professorNome').value,
            email: document.getElementById('professorEmail').value,
            senha: document.getElementById('professorSenha').value,
            tipo: 'PROFESSOR',
            ativo: true
        };
        
        try {
            const url = id ? `${API_URL}/usuarios/${id}` : `${API_URL}/usuarios`;
            const method = id ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                showMessage(`Professor ${id ? 'atualizado' : 'criado'} com sucesso!`, 'success');
                closeModal('professorModal');
                loadProfessoresTabela();
                loadProfessores();
            } else {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao salvar professor');
            }
        } catch (error) {
            console.error('Erro ao salvar professor:', error);
            showMessage(error.message, 'error');
        }
    });
    
    // Curso Form
    document.getElementById('cursoForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const id = document.getElementById('cursoId').value;
        const data = {
            nome: document.getElementById('cursoNome').value,
            descricao: document.getElementById('cursoDescricao').value,
            cargaHoraria: parseInt(document.getElementById('cursoCargaHoraria').value),
            vagas: parseInt(document.getElementById('cursoVagas').value),
            professorId: parseInt(document.getElementById('cursoProfessorId').value)
        };
        
        try {
            const url = id ? `${API_URL}/cursos/${id}` : `${API_URL}/cursos`;
            const method = id ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                showMessage(`Curso ${id ? 'atualizado' : 'criado'} com sucesso!`, 'success');
                closeModal('cursoModal');
                loadCursos();
                loadDashboard();
            } else {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao salvar curso');
            }
        } catch (error) {
            console.error('Erro ao salvar curso:', error);
            showMessage(error.message, 'error');
        }
    });
    
    updateFiltroMatriculas();
    
    // Aluno Form
    document.getElementById('alunoForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const id = document.getElementById('alunoId').value;
        const data = {
            nome: document.getElementById('alunoNome').value,
            email: document.getElementById('alunoEmail').value,
            cpf: document.getElementById('alunoCpf').value.replace(/\D/g, ''),
            telefone: document.getElementById('alunoTelefone').value
        };
        
        // Adiciona senha apenas ao criar novo aluno
        if (!id) {
            data.senha = document.getElementById('alunoSenha').value;
        }
        
        try {
            const url = id ? `${API_URL}/alunos/${id}` : `${API_URL}/alunos`;
            const method = id ? 'PUT' : 'POST';
            
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            
            if (response.ok) {
                showMessage(`Aluno ${id ? 'atualizado' : 'criado'} com sucesso!`, 'success');
                closeModal('alunoModal');
                loadAlunos();
                loadDashboard();
            } else {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao salvar aluno');
            }
        } catch (error) {
            console.error('Erro ao salvar aluno:', error);
            showMessage(error.message, 'error');
        }
    });
    
    // Matrícula Form
    document.getElementById('matriculaForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const alunoId = parseInt(document.getElementById('matriculaAlunoId').value);
        const cursoId = parseInt(document.getElementById('matriculaCursoId').value);
        
        try {
            const response = await fetch(`${API_URL}/matriculas`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ alunoId, cursoId })
            });
            
            if (response.ok) {
                showMessage('Matrícula realizada com sucesso!', 'success');
                closeModal('matriculaModal');
                loadMatriculas();
                loadDashboard();
            } else {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao realizar matrícula');
            }
        } catch (error) {
            console.error('Erro ao realizar matrícula:', error);
            showMessage(error.message, 'error');
        }
    });
    
    // Progresso Form
    document.getElementById('progressoForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const matriculaId = document.getElementById('progressoMatriculaId').value;
        const progresso = parseFloat(document.getElementById('progressoValor').value);
        
        try {
            const response = await fetch(`${API_URL}/matriculas/${matriculaId}/progresso`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ progresso })
            });
            
            if (response.ok) {
                showMessage('Progresso atualizado com sucesso!', 'success');
                closeModal('progressoModal');
                loadMatriculas();
            } else {
                const error = await response.json();
                throw new Error(error.message || 'Erro ao atualizar progresso');
            }
        } catch (error) {
            console.error('Erro ao atualizar progresso:', error);
            showMessage(error.message, 'error');
        }
    });
    
    // Sync slider com input
    document.getElementById('progressoValor').addEventListener('input', (e) => {
        document.getElementById('progressoSlider').value = e.target.value;
    });
    
    document.getElementById('progressoSlider').addEventListener('input', (e) => {
        document.getElementById('progressoValor').value = e.target.value;
    });
}

async function updateFiltroMatriculas() {
    const filtroTipo = document.getElementById('filtroTipo').value;
    const filtroId = document.getElementById('filtroId');
    
    if (filtroTipo === 'todas') {
        filtroId.style.display = 'none';
        filtroId.value = '';
        loadMatriculas();
    } else {
        filtroId.style.display = 'block';
        filtroId.innerHTML = '<option value="">Selecione...</option>';
        
        if (filtroTipo === 'aluno') {
            alunosCache.forEach(aluno => {
                const option = document.createElement('option');
                option.value = aluno.id;
                option.textContent = `${aluno.nome} (${aluno.email})`;
                filtroId.appendChild(option);
            });
        } else if (filtroTipo === 'curso') {
            cursosCache.forEach(curso => {
                const option = document.createElement('option');
                option.value = curso.id;
                option.textContent = curso.nome;
                filtroId.appendChild(option);
            });
        }
    }
}

// FILTERS
function setupFilters() {
    document.getElementById('filtroTipo').addEventListener('change', (e) => {
        const filtroId = document.getElementById('filtroId');
        if (e.target.value === 'todas') {
            filtroId.style.display = 'none';
            filtroId.value = '';
        } else {
            filtroId.style.display = 'block';
        }
    });
}

// MODAL
function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

// Click fora do modal fecha
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
}

// HELPERS
function formatCPF(cpf) {
    if (!cpf) return '-';
    return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
}

function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
}

function getStatusBadge(status) {
    const badges = {
        'ATIVA': 'success',
        'CONCLUIDA': 'info',
        'CANCELADA': 'danger',
        'TRANCADA': 'warning'
    };
    return badges[status] || 'info';
}

function showMessage(message, type) {
    // Criar toast notification
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background: ${type === 'success' ? '#10b981' : '#ef4444'};
        color: white;
        border-radius: 0.5rem;
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
        z-index: 9999;
        animation: slideIn 0.3s;
    `;
    toast.textContent = message;
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}
