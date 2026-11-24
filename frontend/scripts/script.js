const API_URL = "http://localhost:8080";
let sapzsCarregadas = [];
let unidadesCurricularesCarregadas = [];

function escapeHtml(texto) {
    if (texto === null || texto === undefined) return "";
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function getAuthHeader() {
    const token = localStorage.getItem("Authorization");
    if (!token || token === "null" || token === "undefined") return null;
    return token.startsWith("Bearer ") ? token : `Bearer ${token}`;
}

function redirecionarParaLogin() {
    localStorage.removeItem("Authorization");
    localStorage.removeItem("user_email");
    window.location.href = "login.html";
}

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

function logout() {
    localStorage.clear();
    window.location.href = "login.html";
}

document.addEventListener("DOMContentLoaded", () => {
    if (!verificarAutenticacao()) return;

    carregarUnidadesCurriculares();
    carregarSapzs();

    const sapzForm = document.getElementById("sapzForm");
    if (sapzForm) {
        sapzForm.addEventListener("submit", salvarSapz);
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "Escape") fecharModalDescricao();
    });
});

// Carrega Unidades Curriculares para o campo digitável + datalist
async function carregarUnidadesCurriculares() {
    const authHeader = getAuthHeader();
    const datalist = document.getElementById("unidadesCurricularesList");
    const input = document.getElementById("unidadeCurricularInput");
    const hiddenId = document.getElementById("unidadeCurricularId");
    if (!datalist || !input || !authHeader) return;

    try {
        const response = await fetch(`${API_URL}/unidades-curriculares`, {
            headers: { "Authorization": authHeader }
        });

        if (response.ok) {
            unidadesCurricularesCarregadas = await response.json();
            datalist.innerHTML = unidadesCurricularesCarregadas.map(uc =>
                `<option value="${escapeHtml(uc.nome)} (${uc.cargaHoraria}h)" data-id="${uc.id}"></option>`
            ).join("");

            if (unidadesCurricularesCarregadas.length > 0 && !input.value) {
                const primeira = unidadesCurricularesCarregadas[0];
                input.value = `${primeira.nome} (${primeira.cargaHoraria}h)`;
                if (hiddenId) hiddenId.value = primeira.id;
            }
        }
    } catch (err) {
        console.error("Erro ao carregar Unidades Curriculares:", err);
    }

    input.addEventListener("input", () => {
        const match = unidadesCurricularesCarregadas.find(uc =>
            `${uc.nome} (${uc.cargaHoraria}h)`.toLowerCase() === input.value.trim().toLowerCase() ||
            uc.nome.toLowerCase() === input.value.trim().toLowerCase()
        );
        if (hiddenId) {
            hiddenId.value = match ? match.id : "";
        }
    });
}

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
            sapzsCarregadas = await response.json();
            renderizarTabela();
        }
    } catch (err) {
        console.error("Erro ao carregar SAPZs:", err);
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:red;">Erro ao carregar dados.</td></tr>';
    }
}

function renderizarTabela() {
    const tbody = document.getElementById("sapzTableBody");
    if (!tbody) return;

    const termoBusca = (document.getElementById("buscaTitulo")?.value || "").trim().toLowerCase();
    const estrategiaFiltro = document.getElementById("filtroEstrategia")?.value || "";

    const filtradas = sapzsCarregadas.filter(s => {
        const matchEstrategia = !estrategiaFiltro || s.tipoEstrategia === estrategiaFiltro;
        const matchTexto = !termoBusca ||
            (s.titulo && s.titulo.toLowerCase().includes(termoBusca)) ||
            (s.contextualizacao && s.contextualizacao.toLowerCase().includes(termoBusca));
        return matchEstrategia && matchTexto;
    });

    tbody.innerHTML = "";

    if (filtradas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Nenhuma SAPZ encontrada.</td></tr>';
        return;
    }

    filtradas.forEach(s => {
        const descricaoResumo = s.contextualizacao || "Sem descrição";
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${s.id}</td>
            <td><strong>${escapeHtml(s.titulo)}</strong></td>
            <td><div class="desc-preview" title="${escapeHtml(descricaoResumo)}">${escapeHtml(descricaoResumo)}</div></td>
            <td><span class="badge">${escapeHtml(s.tipoEstrategia)}</span></td>
            <td>${s.cargaHorariaSa}h</td>
            <td>
                <div class="acoes-cell">
                    <button class="btn-ver" onclick="abrirModalDescricao(${s.id})">Ver Descrição</button>
                    <button class="btn-editar" onclick="prepararEdicao(${s.id})">Editar</button>
                    <button class="btn-excluir" onclick="deletarSapz(${s.id})">Excluir</button>
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function prepararEdicao(id) {
    const sapz = sapzsCarregadas.find(item => item.id === id);
    if (!sapz) return;

    document.getElementById("sapzId").value = sapz.id;
    document.getElementById("titulo").value = sapz.titulo || "";
    document.getElementById("tipoEstrategia").value = sapz.tipoEstrategia || "SITUACAO_PROBLEMA";

    const ucObj = unidadesCurricularesCarregadas.find(uc => uc.id === sapz.unidadeCurricular?.id) || sapz.unidadeCurricular;
    if (ucObj) {
        document.getElementById("unidadeCurricularId").value = ucObj.id;
        document.getElementById("unidadeCurricularInput").value = ucObj.cargaHoraria
            ? `${ucObj.nome} (${ucObj.cargaHoraria}h)`
            : (ucObj.nome || "");
    }

    document.getElementById("cargaHorariaSa").value = sapz.cargaHorariaSa ?? 20;
    document.getElementById("contextualizacao").value = sapz.contextualizacao || "";
    document.getElementById("desafio").value = sapz.desafio || "";
    document.getElementById("resultadosEsperados").value = sapz.resultadosEsperados || "";

    document.getElementById("formTitulo").innerText = `Editando SAPZ #${sapz.id}`;
    document.getElementById("btnSubmitSapz").innerText = "Atualizar SAPZ";
    document.getElementById("btnCancelarEdicao").style.display = "inline-block";

    window.scrollTo({ top: 0, behavior: "smooth" });
}

function cancelarEdicao() {
    document.getElementById("sapzForm").reset();
    document.getElementById("sapzId").value = "";
    document.getElementById("formTitulo").innerText = "Nova Situação de Aprendizagem (SAPZ)";
    document.getElementById("btnSubmitSapz").innerText = "Salvar SAPZ";
    document.getElementById("btnCancelarEdicao").style.display = "none";

    if (unidadesCurricularesCarregadas.length > 0) {
        const primeira = unidadesCurricularesCarregadas[0];
        document.getElementById("unidadeCurricularInput").value = `${primeira.nome} (${primeira.cargaHoraria}h)`;
        document.getElementById("unidadeCurricularId").value = primeira.id;
    }
}

function abrirModalDescricao(id) {
    const sapz = sapzsCarregadas.find(item => item.id === id);
    if (!sapz) return;

    document.getElementById("modalTitulo").innerText = `#${sapz.id} - ${sapz.titulo}`;

    const ucNome = sapz.unidadeCurricular?.nome || `ID ${sapz.unidadeCurricular?.id ?? "-"}`;
    const instrutorNome = sapz.instrutor?.nome || sapz.instrutor?.email || "-";

    document.getElementById("modalMeta").innerHTML = `
        <span><strong>Estratégia:</strong> ${escapeHtml(sapz.tipoEstrategia)}</span>
        <span><strong>Carga Horária:</strong> ${sapz.cargaHorariaSa}h</span>
        <span><strong>Unidade Curricular:</strong> ${escapeHtml(ucNome)}</span>
        <span><strong>Instrutor:</strong> ${escapeHtml(instrutorNome)}</span>
    `;

    document.getElementById("modalContextualizacao").innerText = sapz.contextualizacao || "Não informada.";
    document.getElementById("modalDesafio").innerText = sapz.desafio || "Não informado.";
    document.getElementById("modalResultados").innerText = sapz.resultadosEsperados || "Não informados.";

    document.getElementById("modalDescricao").classList.add("ativo");
}

function fecharModalDescricao() {
    const modal = document.getElementById("modalDescricao");
    if (modal) modal.classList.remove("ativo");
}

function fecharModalSeClicarFora(event) {
    if (event.target.id === "modalDescricao") {
        fecharModalDescricao();
    }
}

// Criar ou Atualizar SAPZ
async function salvarSapz(e) {
    e.preventDefault();
    const authHeader = getAuthHeader();
    if (!authHeader) return redirecionarParaLogin();

    const sapzId = document.getElementById("sapzId").value;
    const cargaHorariaSa = parseInt(document.getElementById("cargaHorariaSa").value, 10);

    if (isNaN(cargaHorariaSa) || cargaHorariaSa < 1 || cargaHorariaSa > 408) {
        alert("A carga horária permitida para uma SAPZ é de 1 a 408 horas.");
        return;
    }

    const ucInputValor = (document.getElementById("unidadeCurricularInput")?.value || "").trim().toLowerCase();
    const ucEncontrada = unidadesCurricularesCarregadas.find(uc =>
        `${uc.nome} (${uc.cargaHoraria}h)`.toLowerCase() === ucInputValor ||
        uc.nome.toLowerCase() === ucInputValor ||
        String(uc.id) === ucInputValor
    );
    const unidadeCurricularId = ucEncontrada
        ? ucEncontrada.id
        : parseInt(document.getElementById("unidadeCurricularId").value, 10);

    if (!unidadeCurricularId || isNaN(unidadeCurricularId)) {
        alert("Selecione ou digite uma Unidade Curricular válida.");
        return;
    }

    const payload = {
        titulo: document.getElementById("titulo").value,
        tipoEstrategia: document.getElementById("tipoEstrategia").value,
        unidadeCurricular: { id: unidadeCurricularId },
        cargaHorariaSa: cargaHorariaSa,
        contextualizacao: document.getElementById("contextualizacao").value,
        desafio: document.getElementById("desafio").value,
        resultadosEsperados: document.getElementById("resultadosEsperados").value,
        compartilhada: false
    };

    const isEdicao = Boolean(sapzId);
    const url = isEdicao
        ? `${API_URL}/situacoes-aprendizagem/${sapzId}`
        : `${API_URL}/situacoes-aprendizagem`;
    const method = isEdicao ? "PUT" : "POST";

    try {
        const response = await fetch(url, {
            method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": authHeader
            },
            body: JSON.stringify(payload)
        });

        if (response.status === 401 || response.status === 403) {
            redirecionarParaLogin();
            return;
        }

        if (response.ok || response.status === 201) {
            alert(isEdicao ? "SAPZ atualizada com sucesso!" : "SAPZ salva com sucesso!");
            cancelarEdicao();
            carregarSapzs();
        } else if (response.status >= 500) {
            alert("Ops! Ocorreu uma falha interna no servidor ao salvar a SAPZ.");
        } else {
            const errorData = await response.json().catch(() => null);
            const mensagem = errorData?.errors?.[0]?.message || errorData?.message || "Não foi possível salvar a SAPZ. Verifique os campos.";
            alert(mensagem);
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
            if (document.getElementById("sapzId").value === String(id)) {
                cancelarEdicao();
            }
            carregarSapzs();
        }
    } catch (err) {
        console.error("Erro ao excluir:", err);
        alert("Erro ao excluir.");
    }
}