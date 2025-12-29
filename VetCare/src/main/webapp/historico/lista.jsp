<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Histórico clínico do animal (Timeline).
    Exibe uma lista unificada de eventos médicos (consultas, cirurgias, vacinas) ordenados cronologicamente.
--%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>VetCare - Histórico Clínico</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/style.css"
    />
  </head>
  <body>
<%@ page import="java.util.*" %>
<%@ page import="animal.Animal" %>
<div class="header">
  <div class="container">
    <h1>VetCare Manager</h1>
    <nav>
      <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
      <a href="${pageContext.request.contextPath}/animais" class="nav-link">Animais</a>
    </nav>
  </div>
</div>

<div class="container">
  <%
    //  HYDRATION: Recupera o objeto Animal e a lista de eventos (Maps) do pedido HTTP.
    Animal a = (Animal) request.getAttribute("animal");
    List<Map<String, Object>> historia = (List<Map<String, Object>>) request.getAttribute("historia");
    if (a != null) {
  %>
  <div class="card" style="margin-bottom: 20px;">
    <h2>Ficha Clínica: <%= a.getNome() %></h2>
    <div style="display: flex; gap: 40px;">
        <div style="flex: 1;">
            <p><strong>Espécie:</strong> <%= a.getCatalogoNomeComum() %></p>
            <p><strong>Raça:</strong> <%= a.getRaca() %></p>
            <p><strong>Sexo:</strong> <%= a.getSexo() %></p>
        </div>
        <div style="flex: 1;">
            <p><strong>Idade:</strong> <%= a.getIdadeFormatada() %></p>
            <p><strong>Escalão Etário:</strong> <span class="badge"><%= a.getEscalaoEtario(a.getExpectativaVida()) %></span></p>
        </div>
    </div>
  </div>

  <div class="card">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h3>Histórico Completo</h3>
      <a href="historico?p=new&idAnimal=<%= a.getIdAnimal() %>" class="btn btn-primary">+ Novo Registo</a>
    </div>

    <table>
        <thead>
            <tr>
                <th>Data e Hora</th>
                <th>Tipo de Serviço</th>
                <th>Detalhes / Diagnóstico</th>
            </tr>
        </thead>
        <tbody>
            <% if (historia != null && !historia.isEmpty()) {
                for (Map<String, Object> record : historia) { %>
                <tr>
                    <td><%= record.get("DataHora") %></td>
                    <td><strong><%= record.get("Tipo") %></strong><br><small><%= record.get("TipoGenerico") %></small></td>
                    <td><%= record.get("Detalhes") %></td>
                </tr>
            <% } } else { %>
                <tr><td colspan="3">Nenhum registo histórico encontrado.</td></tr>
            <% } %>
        </tbody>
    </table>
  </div>
  <% } else { %>
    <div class="card"><p>Animal não encontrado.</p></div>
  <% } %>
</div>
  </body>
</html>
