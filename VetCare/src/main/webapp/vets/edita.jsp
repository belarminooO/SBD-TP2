<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ page import="veterinario.Veterinario" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>VetCare - Editar Veterinário</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/style.css"
    />
  </head>
  <body>
    <div class="header">
      <div class="container">
        <h1>VetCare Manager</h1>
        <nav>
          <a href="${pageContext.request.contextPath}/vets" class="nav-link"
            >Voltar</a
          >
        </nav>
      </div>
    </div>

    <div class="container">
      <div class="card">
        <% Veterinario v = (Veterinario) request.getAttribute("vet"); %>
        <h2><%= (v == null ? "Novo Veterinário" : "Editar Veterinário") %></h2>

        <form action="vets" method="post">
          <div class="form-group">
            <label>Nº Licença:</label>
            <input type="text" name="NLicenca" value="<%= (v != null ?
            v.getNLicenca() : "") %>" <%= (v != null ? "readonly" : "required")
            %>>
          </div>

          <div class="form-group">
            <label>Nome Completo:</label>
            <input type="text" name="Nome" value="<%= (v != null ? v.getNome() :
            "") %>" required>
          </div>

          <div style="margin-top: 20px">
            <button type="submit" class="btn btn-primary">Gravar</button>
            <a href="vets" class="btn btn-secondary">Cancelar</a>
          </div>
        </form>
      </div>
    </div>
  </body>
</html>
