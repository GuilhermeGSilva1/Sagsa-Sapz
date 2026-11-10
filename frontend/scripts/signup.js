const API_URL = "http://localhost:8080";

document.getElementById("signupForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const nome = document.getElementById("nome").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const perfilId = parseInt(document.getElementById("perfilId").value);
    const alertDiv = document.getElementById("alert");

    alertDiv.style.display = "none";

    try {
        const response = await fetch(`${API_URL}/user`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                nome,
                email,
                password,
                perfilId
            })
        });

        if (response.ok || response.status === 201) {
            alertDiv.className = "alert alert-success";
            alertDiv.innerText = "Usuário cadastrado com sucesso! Redirecionando para login...";
            alertDiv.style.display = "block";
            setTimeout(() => window.location.href = "login.html", 1500);
        } else {
            alertDiv.className = "alert alert-error";
            alertDiv.innerText = "Erro ao cadastrar usuário. Verifique se o e-mail já existe.";
            alertDiv.style.display = "block";
        }
    } catch (err) {
        alertDiv.className = "alert alert-error";
        alertDiv.innerText = "Erro de conexão com a API.";
        alertDiv.style.display = "block";
    }
});
