<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%-- Ficha de edição/criação de veterinário. Gere os
dados profissionais, utilizando a Cédula como identificador único. --%> <%@ page
import="veterinario.Veterinario" %>
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
          <a href="${pageContext.request.contextPath}/" class="nav-link"
            >Home</a
          >
          <a href="${pageContext.request.contextPath}/vets" class="nav-link"
            >Veterinários</a
          >
        </nav>
      </div>
    </div>

    <div class="container">
      <div class="card">
        <h2>Ficha do Veterinário</h2>
        <% Veterinario v = (Veterinario) request.getAttribute("vet"); %>
        <form action="vets" method="post">
          <div style="margin-bottom: 15px">
            <label>Nº Cédula (NLicenca):</label>
            <input type="text" name="NLicenca" required style="width:100%"
            value="<%= (v!=null) ? v.getNLicenca() : "" %>" <%= (v!=null) ?
            "readonly" : "" %>>
            <small
              >Se estiver a editar, o número de cédula não pode ser
              alterado.</small
            >
          </div>
          <div style="margin-bottom: 15px">
            <label>Nome Completo:</label>
            <input type="text" name="Nome" required style="width:100%"
            value="<%= (v!=null) ? v.getNome() : "" %>">
          </div>
          <button type="submit" class="btn btn-primary">Gravar</button>
          <a href="vets" class="btn btn-secondary">Cancelar</a>
        </form>
      </div>
    </div>
  </body>
</html>
