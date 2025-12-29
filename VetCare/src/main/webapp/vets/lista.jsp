<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Listagem de veterinários do corpo clínico.
    Permite visualizar e editar os dados cadastrais dos profissionais.
--%>
<%@ page import="java.util.List" %>
<%@ page import="veterinario.Veterinario" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Veterinários</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/vets" class="nav-link">Veterinários</a>
                <a href="${pageContext.request.contextPath}/clientes" class="nav-link">Clientes</a>
                <a href="${pageContext.request.contextPath}/animais" class="nav-link">Animais</a>
                <a href="${pageContext.request.contextPath}/agendamentos" class="nav-link">Agendamentos</a>
                <a href="${pageContext.request.contextPath}/manager" class="nav-link">Gestão</a>
            </nav>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                <h2>Veterinários Registados</h2>
                <a href="vets?p=edit" class="btn btn-primary">+ Novo Veterinário</a>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Cédula (NLicenca)</th>
                        <th>Nome Profissional</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Veterinario> lista = (List<Veterinario>) request.getAttribute("listaVets");
                    if (lista != null) {
                        for (Veterinario v : lista) {
                    %>
                    <tr>
                        <td><%= v.getNLicenca() %></td>
                        <td><%= v.getNome() %></td>
                        <td>
                            <a href="vets?p=edit&id=<%= v.getNLicenca() %>" class="btn btn-sm">Editar</a>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="3">Sem registos.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
