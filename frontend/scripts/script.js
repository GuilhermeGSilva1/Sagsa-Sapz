const API_URL = "http://localhost:8080";

// Auxiliar para recuperar o token formatado
function getAuthHeader() {
    const token = localStorage.getItem("Authorization");
    if (!token || token === "null" || token === "undefined") return null;
    return token.startsWith("Bearer ") ? token : `Bearer ${token}`;
}

// Redireciona para o login limpando dados inválidos
function redirecionarParaLogin() {
    localStorage.removeItem("Authorization");
    localStorage.removeItem("user_email");
    window.location.href = "login.html";
}

// Validação de autenticação local
function verificarAutenticacao() {
    const authHeader = getAuthHeader();
    const email = localStorage.getItem("user_email");

    if (!authHeader) {
        redirecionarParaLogin();
        return false;
    }

    const userDisplay = document.getElementById("userDisplay");
    if (userDisplay && email) {
        userDisplay.innerText = email;
    }

    return true;
}

// Logout manual
function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

// Inicialização da página
document.addEventListener("DOMContentLoaded", () => {
    if (!verificarAutenticacao()) return;

    carregarSapzs();

    const sapzForm = document.getElementById("sapzForm");
    if (sapzForm) {
        sapzForm.addEventListener("submit", salvarSapz);
    }
});

// Listar SAPZs da API
async function carregarSapzs() {
    const authHeader = getAuthHeader();
    const tbody = document.getElementById("sapzTableBody");
    if (!tbody || !authHeader) return;

    try {
        const response = await fetch(`${API_URL}/situacoes-aprendizagem`, {
            headers: { "Authorization": authHeader }
        });

        if (response.status === 401 || response.status === 403) {
            redirecionarParaLogin();
            return;
        }

        if (response.ok) {
            const sapzs = await response.json();
            tbody.innerHTML = "";

            if (sapzs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;">Nenhuma SAPZ cadastrada ainda.</td></tr>';
                return;
            }

            sapzs.forEach(s => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td>${s.id}</td>
                    <td><strong>${s.titulo}</strong></td>
                    <td><span class="badge">${s.tipoEstrategia}</span></td>
                    <td>${s.cargaHorariaSa}h</td>
                    <td><button onclick="deletarSapz(${s.id})" style="background:#dc3545; color:white; border:none; padding:4px 8px; border-radius:4px; cursor:pointer;">Excluir</button></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error("Erro ao carregar SAPZs:", err);
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:red;">Erro ao carregar dados.</td></tr>';
    }
}

// Salvar SAPZ
async function salvarSapz(e) {
    e.preventDefault();
    const authHeader = getAuthHeader();
    if (!authHeader) return redirecionarParaLogin();

    const novaSapz = {
        titulo: document.getElementById("titulo").value,
        tipoEstrategia: document.getElementById("tipoEstrategia").value,
        unidadeCurricular: { id: parseInt(document.getElementById("unidadeCurricularId").value) },
        cargaHorariaSa: parseInt(document.getElementById("cargaHorariaSa").value),
        contextualizacao: document.getElementById("contextualizacao").value,
        desafio: document.getElementById("desafio").value,
        resultadosEsperados: document.getElementById("resultadosEsperados").value,
        compartilhada: false
    };

    try {
        const response = await fetch(`${API_URL}/situacoes-aprendizagem`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": authHeader
            },
            body: JSON.stringify(novaSapz)
        });

        if (response.status === 401 || response.status === 403) {
            redirecionarParaLogin();
            return;
        }

        if (response.ok || response.status === 201) {
            alert("SAPZ salva com sucesso!");
            document.getElementById("sapzForm").reset();
            carregarSapzs();
        } else {
            const errorMsg = await response.text();
            alert("Erro ao salvar SAPZ: " + errorMsg);
        }
    } catch (err) {
        console.error("Erro de conexão:", err);
        alert("Erro de conexão com o servidor.");
    }
}

// Excluir SAPZ
async function deletarSapz(id) {
    if (!confirm("Deseja realmente excluir esta SAPZ?")) return;
    const authHeader = getAuthHeader();
    if (!authHeader) return redirecionarParaLogin();

    try {
        const response = await fetch(`${API_URL}/situacoes-aprendizagem/${id}`, {
            method: "DELETE",
            headers: { "Authorization": authHeader }
        });

        if (response.status === 401 || response.status === 403) {
            redirecionarParaLogin();
            return;
        }

        if (response.ok || response.status === 204) {
            carregarSapzs();
        }
    } catch (err) {
        console.error("Erro ao excluir:", err);
        alert("Erro ao excluir.");
    }
}