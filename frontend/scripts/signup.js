const API_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    const signupForm = document.getElementById("signupForm");
    if (!signupForm) return;

    signupForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const nome = document.getElementById("nome").value.trim();
        const email = document.getElementById("email").value.trim().toLowerCase();
        const password = document.getElementById("password").value;
        const perfilId = parseInt(document.getElementById("perfilId").value, 10);
        const alertDiv = document.getElementById("alert");

        if (alertDiv) alertDiv.style.display = "none";

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
                if (alertDiv) {
                    alertDiv.className = "alert alert-success";
                    alertDiv.innerText = "Usuário cadastrado com sucesso! Redirecionando para login...";
                    alertDiv.style.display = "block";
                }
                setTimeout(() => {
                    window.location.href = "login.html";
                }, 1500);
            } else {
                const errorData = await response.json().catch(() => null);
                const msg =
                    errorData?.errors?.[0]?.message ||
                    errorData?.message ||
                    "Erro ao cadastrar usuário. Verifique se o e-mail já existe.";
                if (alertDiv) {
                    alertDiv.className = "alert alert-error";
                    alertDiv.innerText = msg;
                    alertDiv.style.display = "block";
                }
            }
        } catch (err) {
            console.error("Erro de conexão:", err);
            if (alertDiv) {
                alertDiv.className = "alert alert-error";
                alertDiv.innerText = "Erro de conexão com a API.";
                alertDiv.style.display = "block";
            }
        }
    });
});
