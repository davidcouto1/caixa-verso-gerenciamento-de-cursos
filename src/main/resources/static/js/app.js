const API_URL = 'http://localhost:8080/api';

// Caches para dados
let professoresCache = [];
let alunosCache = [];
let cursosCache = [];
let alunosOptions = [];
let cursosOptions = [];

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    loadDashboard();
    loadCursos();
    loadAlunos();
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
    event.target.classList.add('active');

    // Carregar dados da seção
    if (sectionId === 'cursos') loadCursos();
    if (sectionId === 'alunos') loadAlunos();
    if (sectionId === 'matriculas') loadMatriculas();
    if (sectionId === 'dashboard') loadDashboard();
}

// Dashboard
async function loadDashboard() {
    try {
        const [cursos, alunos, cursosComVagas] = await Promise.all([
            fetch(`${API_URL}/cursos`).then(r => r.json()),
            fetch(`${API_URL}/alunos`).then(r => r.json()),
            fetch(`${API_URL}/cursos/disponiveis`).then(r => r.json())
        ]);

        document.getElementById('totalCursos').textContent = cursos.length;
        document.getElementById('totalAlunos').textContent = alunos.length;
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
        console.error('Erro ao carregar professores:', error);
        // Se não houver professores, criar alguns de exemplo
        professoresCache = [
            { id: 1, nome: 'Professor 1', tipo: 'PROFESSOR' },
            { id: 2, nome: 'Professor 2', tipo: 'PROFESSOR' },
            { id: 3, nome: 'Professor 3', tipo: 'PROFESSOR' }
        ];
    }
}

function populateProfessorSelect() {
    const select = document.getElementById('cursoProfessorId');
    select.innerHTML = '<option value="">Selecione um professor...</option>';
    
    professoresCache.forEach(prof => {
        const option = document.createElement('option');
        option.value = prof.id;
        option.textContent = `${prof.nome} (ID: ${prof.id})`;
        select.appendChild(option);
    });
}

function populateAlunoSelect() {
    const select = document.getElementById('matriculaAlunoId');
    select.innerHTML = '<option value="">Selecione um aluno...</option>';
    
    alunosOptions = alunosCache.map(aluno => ({
        value: aluno.id,
        text: `${aluno.nome} - ${aluno.email}`,
        searchText: `${aluno.nome} ${aluno.email} ${aluno.id}`.toLowerCase()
    }));
    
    alunosOptions.forEach(opt => {
    populateProfessorSelect();
        const option = document.createElement('option');
        option.value = opt.value;
        option.textContent = opt.text;
        option.dataset.search = opt.searchText;
        select.appendChild(option);
    });
}

function populateCursoSelect() {
    const select = document.getElementById('matriculaCursoId');
    select.innerHTML = '<option value="">Selecione um curso...</option>';
    
    cursosOptions = cursosCache
        .filter(curso => curso.ativo && curso.vagasDisponiveis > 0)
        .map(curso => ({
            value: curso.id,
            text: `${curso.nome} (${curso.vagasDisponiveis} vagas)`,
            searchText: `${curso.nome} ${curso.id}`.toLowerCase()
        }));
    
    cursosOptions.forEach(opt => {
        const option = document.createElement('option');
        option.value = opt.value;
        option.textContent = opt.text;
        option.dataset.search = opt.searchText;
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
                        <button class="btn btn-warning" onclick="editCurso(${curso.id})">Editar</button>
                        <button class="btn btn-danger" onclick="deleteCurso(${curso.id})">Excluir</button>
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
        alunosCache = alunos;
        const alunos = await response.json();
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
                    <div class="action-buttons">
                        <button class="btn btn-warning" onclick="editAluno(${aluno.id})">Editar</button>
                        <button class="btn btn-danger" onclick="deleteAluno(${aluno.id})">Excluir</button>
                    </div>
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

    // Popular selects
    populateAlunoSelect();
    populateCursoSelect();
    
    // Limpar campos de busca
    document.getElementById('matriculaAlunoSearch').value = '';
    document.getElementById('matriculaCursoSearch').value = '';
    
    
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
                    <div class="action-buttons">
                        ${matricula.status === 'ATIVA' ? `
                            <button class="btn btn-success" onclick="showProgressoModal(${matricula.id}, ${matricula.progresso})">Progresso</button>
                            <button class="btn btn-danger" onclick="cancelarMatricula(${matricula.id})">Cancelar</button>
                        ` : ''}
                    </div>
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
        showMessage(error.message, 'error');
    }
}

// FORMS
function setupForms() {
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
    }     cpf: document.getElementById('alunoCpf').value.replace(/\D/g, ''),
            telefone: document.getElementById('alunoTelefone').value
        };
        
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
            const response = await fetch(`${API_URL}/matriculas?alunoId=${alunoId}&cursoId=${cursoId}`, {
                method: 'POST'
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
            const response = await fetch(`${API_URL}/matriculas/${matriculaId}/progresso?progresso=${progresso}`, {
                method: 'PATCH'
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
