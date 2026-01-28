// Script para gerenciar login
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const btn = document.getElementById('loginBtn');
    const btnText = document.getElementById('btnText');
    const errorMsg = document.getElementById('errorMessage');
    
    // Desabilita botão e mostra loading
    btn.disabled = true;
    btnText.innerHTML = 'Entrando<span class="loading"></span>';
    errorMsg.classList.remove('active');
    
    const formData = new FormData(e.target);
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            body: formData
        });
        
        if (response.ok) {
            // Login bem-sucedido
            window.location.href = '/index.html';
        } else {
            // Credenciais inválidas
            errorMsg.textContent = '❌ Email ou senha incorretos. Tente novamente.';
            errorMsg.classList.add('active');
            btn.disabled = false;
            btnText.textContent = 'Entrar';
        }
    } catch (error) {
        console.error('Erro ao fazer login:', error);
        errorMsg.textContent = '❌ Erro ao conectar com o servidor. Tente novamente.';
        errorMsg.classList.add('active');
        btn.disabled = false;
        btnText.textContent = 'Entrar';
    }
});

// Verifica se já está logado
fetch('/api/auth/me')
    .then(response => {
        if (response.ok) {
            // Já está logado, redireciona
            window.location.href = '/index.html';
        }
    })
    .catch(() => {
        // Não está logado, permanece na página
    });
