// Mostra/oculta mensagem de erro se vier da URL
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.get('error') !== null) {
    const errorMsg = document.getElementById('errorMessage');
    errorMsg.textContent = '❌ Email ou senha incorretos. Tente novamente.';
    errorMsg.classList.add('active');
}

if (urlParams.get('logout') !== null) {
    const errorMsg = document.getElementById('errorMessage');
    errorMsg.textContent = '✅ Logout realizado com sucesso!';
    errorMsg.style.backgroundColor = '#d1fae5';
    errorMsg.style.color = '#065f46';
    errorMsg.classList.add('active');
}
