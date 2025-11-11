const API_URL = "http://localhost:8080/crud/api/products";

const form = document.getElementById("product-form");
const cancelBtn = document.getElementById("cancel-btn");
const title = document.getElementById("form-title");
const tableBody = document.getElementById("product-table-body");

let editingId = null;

// 🟢 LISTAR PRODUTOS
async function loadProducts() {
  tableBody.innerHTML = "<tr><td colspan='5'>Carregando...</td></tr>";
  const response = await fetch(API_URL);
  if (!response.ok) {
    tableBody.innerHTML = "<tr><td colspan='5'>Erro ao carregar produtos.</td></tr>";
    return;
  }
  const products = await response.json();

  tableBody.innerHTML = "";
  products.forEach((p) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${p.code}</td>
      <td>${p.name}</td>
      <td>${p.price.toFixed(2)}</td>
      <td>${p.description || ""}</td>
      <td class="actions">
        <button onclick="editProduct(${p.code})">Editar</button>
        <button class="delete" onclick="deleteProduct(${p.code})">Excluir</button>
      </td>`;
    tableBody.appendChild(tr);
  });
}

// 🟡 CRIAR / ATUALIZAR PRODUTO
form.addEventListener("submit", async (e) => {
  e.preventDefault();
  const product = {
    name: document.getElementById("name").value,
    price: parseFloat(document.getElementById("price").value),
    description: document.getElementById("description").value,
  };

  const method = editingId ? "PUT" : "POST";
  const url = editingId ? `${API_URL}/${editingId}` : API_URL;

  const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(product),
  });

  if (response.ok) {
    resetForm();
    loadProducts();
  } else {
    alert("Erro ao salvar produto");
  }
});

// 🧩 EDITAR PRODUTO
async function editProduct(id) {
  const response = await fetch(`${API_URL}/${id}`);
  if (!response.ok) return alert("Produto não encontrado");

  const p = await response.json();
  editingId = id;
  title.textContent = "Editar Produto";
  cancelBtn.classList.remove("hidden");

  document.getElementById("name").value = p.name;
  document.getElementById("price").value = p.price;
  document.getElementById("description").value = p.description;
}

// ❌ EXCLUIR PRODUTO
async function deleteProduct(id) {
  if (!confirm("Tem certeza que deseja excluir este produto?")) return;

  const response = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  if (response.ok) {
    loadProducts();
  } else {
    alert("Erro ao excluir produto");
  }
}

// 🔄 CANCELAR E LIMPAR FORMULÁRIO
cancelBtn.addEventListener("click", resetForm);

function resetForm() {
  editingId = null;
  title.textContent = "Cadastrar Produto";
  form.reset();
  cancelBtn.classList.add("hidden");
}

// 🔁 CARREGAR PRODUTOS AO ABRIR A PÁGINA
loadProducts();
