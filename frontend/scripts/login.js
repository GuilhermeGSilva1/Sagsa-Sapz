const API_URL = "http://localhost:8080";

document.getElementById("loginForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const alertDiv = document.getElementById("alert");

    alertDiv.style.display = "none";

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const tokenHeader = response.headers.get("Authorization");
            let token = tokenHeader;
            try {
                const body = await response.json();
                if (body.token) token = body.token;
            } catch (err) {}

            localStorage.setItem("Authorization", token);
            localStorage.setItem("user_email", email);
            window.location.href = "index.html";
        } else {
            alertDiv.innerText = "E-mail ou senha inválidos.";
            alertDiv.style.display = "block";
        }
    } catch (err) {
        alertDiv.innerText = "Erro ao conectar com o servidor da API.";
        alertDiv.style.display = "block";
    }
});

// Exemplo no login.js após sucesso na requisição
const response = await fetch('http://localhost:8080/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
});

if (response.ok) {
    // Obter o token do cabeçalho de resposta ou do corpo JSON
    const token = response.headers.get('Authorization'); 
    
    if (token) {
        localStorage.setItem('Authorization', token);
        window.location.href = 'index.html';
    }
}
