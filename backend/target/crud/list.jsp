<%@ page import="java.util.*, model.Product" %>
<html>
<head><title>Lista de Produtos</title></head>
<body>
  <h1>Lista de Produtos</h1>
  <a href="products?action=new">Novo Produto</a>
  <table border="1" cellpadding="6">
    <tr><th>ID</th><th>Nome</th><th>Preço</th><th>Descrição</th><th>Ações</th></tr>
    <%
      List<Product> produtos = (List<Product>) request.getAttribute("produtos");
      for (Product p : produtos) {
    %>
    <tr>
      <td><%=p.getCod_produto()%></td>
      <td><%=p.getNome_produto()%></td>
      <td><%=p.getPreco_produto()%></td>
      <td><%=p.getDescricao_produto()%></td>
      <td>
        <a href="products?action=edit&id=<%=p.getCod_produto()%>">Editar</a> |
        <a href="products?action=delete&id=<%=p.getCod_produto()%>">Excluir</a>
      </td>
    </tr>
    <% } %>
  </table>
</body>
</html>
