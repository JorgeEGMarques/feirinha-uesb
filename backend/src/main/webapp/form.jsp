<%@ page import="model.Product" %>
<%
  Product p = (Product) request.getAttribute("produto");
  boolean edit = p != null;
%>
<html>
<head><title><%= edit ? "Editar Produto" : "Novo Produto" %></title></head>
<body>
  <h1><%= edit ? "Editar Produto" : "Novo Produto" %></h1>
  <form action="products" method="post">
    <input type="hidden" name="id" value="<%= edit ? p.getCod_produto() : "" %>"/>
    Nome: <input type="text" name="nome" value="<%= edit ? p.getNome_produto() : "" %>" required><br/>
    Preço: <input type="text" name="preco" value="<%= edit ? p.getPreco_produto() : "" %>" required><br/>
    Descrição: <input type="text" name="descricao" value="<%= edit ? p.getDescricao_produto() : "" %>"><br/>
    <input type="submit" value="Salvar">
  </form>
  <br/>
  <a href="products">Voltar</a>
</body>
</html>
