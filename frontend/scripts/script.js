const API_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    verificarAutenticacao();
    carregarSapzs();
});

function verificarAutenticacao() {
    const token = localStorage.getItem("Authorization");
    const email = localStorage.getItem("user_email");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    document.getElementById("userDisplay").innerText = `${email}`;
}

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

async function carregarSapzs() {
    const token = localStorage.getItem("Authorization");
    const tbody = document.getElementById("sapzTableBody");

    try {
        const response = await fetch(`${API_URL}/situacoes-aprendizagem`, {
            headers: { 
                "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token}` 
            }
        });

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
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center; color:red;">Erro ao carregar dados.</td></tr>';
    }
}

document.getElementById("sapzForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("Authorization");

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
                "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token}`
            },
            body: JSON.stringify(novaSapz)
        });

        if (response.ok || response.status === 201) {
            alert("SAPZ salva com sucesso!");
            document.getElementById("sapzForm").reset();
            carregarSapzs();
        } else {
            const errorMsg = await response.text();
            alert("Erro ao salvar SAPZ: " + errorMsg);
        }
    } catch (err) {
        alert("Erro de conexão com o servidor.");
    }
});

async function deletarSapz(id) {
    if (!confirm("Deseja realmente excluir esta SAPZ?")) return;
    const token = localStorage.getItem("Authorization");

    try {
        const response = await fetch(`${API_URL}/situacoes-aprendizagem/${id}`, {
            method: "DELETE",
            headers: { 
                "Authorization": token.startsWith("Bearer ") ? token : `Bearer ${token}` 
            }
        });

        if (response.ok || response.status === 204) {
            carregarSapzs();
        }
    } catch (err) {
        alert("Erro ao excluir.");
    }
}
