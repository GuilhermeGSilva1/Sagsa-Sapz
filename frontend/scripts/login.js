const API_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");
    
    if (!loginForm) return;

    loginForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const emailInput = document.getElementById("username") || document.getElementById("email");
        const passwordInput = document.getElementById("password");
        const alertDiv = document.getElementById("alert");

        if (alertDiv) alertDiv.style.display = "none";

        const email = emailInput ? emailInput.value.trim().toLowerCase() : "";
        const password = passwordInput ? passwordInput.value : "";

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ 
                    email: email, 
                    password: password 
                })
            });

            if (response.ok) {
                let token = response.headers.get("Authorization");

                if (!token) {
                    const body = await response.json().catch(() => ({}));
                    token = body.token || body.Authorization;
                }

                if (token) {
                    const tokenLimpo = token.replace(/^Bearer\s+/i, '');
                    localStorage.setItem("Authorization", tokenLimpo);
                    localStorage.setItem("user_email", email);
                    window.location.href = "index.html";
                } else {
                    exibirErro("O servidor não retornou o token de autenticação.");
                }
            } else {
                exibirErro("E-mail ou senha inválidos.");
            }
        } catch (err) {
            console.error("Erro na conexão:", err);
            exibirErro("Erro ao conectar com o servidor da API.");
        }
    });
});

function exibirErro(mensagem) {
    const alertDiv = document.getElementById("alert");
    if (alertDiv) {
        alertDiv.className = "alert alert-error";
        alertDiv.innerText = mensagem;
        alertDiv.style.display = "block";
    } else {
        alert(mensagem);
    }
}