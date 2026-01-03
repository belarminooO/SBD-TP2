<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="cliente.*" %>
<%@ page import="auth.Utilizador" %>
<%@ page import="auth.Role" %>
<%
    Utilizador user = (Utilizador) session.getAttribute("utilizador");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    boolean isGerente = user.getRole() == Role.GERENTE;
    boolean isVeterinario = user.getRole() == Role.VETERINARIO;
    boolean podeEditar = user.podeEditarClientes();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Clientes</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <% if (isGerente) { %>
                    <a href="${pageContext.request.contextPath}/vets" class="nav-link">Veterinários</a>
                <% } %>
                <a href="${pageContext.request.contextPath}/clientes" class="nav-link">Clientes</a>
                <a href="${pageContext.request.contextPath}/animais" class="nav-link">Animais</a>
                <a href="${pageContext.request.contextPath}/agendamentos" class="nav-link">Agendamentos</a>
                <% if (isGerente) { %>
                    <a href="${pageContext.request.contextPath}/manager" class="nav-link">Gestão</a>
                <% } %>
                <a href="${pageContext.request. contextPath}/logout" class="nav-link" style="float: right;">Sair</a>
            </nav>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <div style="display: flex; justify-content:space-between; align-items:center; margin-bottom:20px;">
                <h2>Lista de Clientes</h2>
                <% if (podeEditar) { %>
                    <a href="clientes?p=edit" class="btn btn-primary">+ Novo Cliente</a>
                <% } %>
            </div>
            
            <% if (isVeterinario) { %>
                <div style="background:#ebf8ff; border:1px solid #4299e1; padding:10px; border-radius:5px; margin-bottom:15px;">
                    <small>ℹ️ Modo de consulta - não é possível editar dados de clientes. </small>
                </div>
            <% } %>

            <table>
                <thead>
                    <tr>
                        <th>NIF</th>
                        <th>Nome</th>
                        <th>Tipo</th>
                        <th>Contactos</th>
                        <th>Morada/Localidade</th>
                        <th>Preferência</th>
                        <% if (podeEditar) { %>
                            <th>Ações</th>
                        <% } %>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Cliente> lista = (List<Cliente>) request.getAttribute("listaClientes");
                    if (lista != null) {
                        for (Cliente c : lista) {
                    %>
                    <tr>
                        <td><%= c.getNif() %></td>
                        <td><%= c.getNomeCompleto() %></td>
                        <td><%= c. getTipoCliente() %></td>
                        <td><%= (c.getContactos() != null ?  c.getContactos() : "-") %></td>
                        <td>
                            <%= (c.getMorada() != null ? c.getMorada() : "") %>
                            <small style="display:block; color:#666;">
                                <%= (c.getConcelho() != null ? c.getConcelho() : "") %> <%= (c.getDistrito() != null ? ", " + c.getDistrito() : "") %>
                            </small>
                        </td>
                        <td><%= (c.getPreferenciasLinguisticas() != null ? c.getPreferenciasLinguisticas() : "-") %></td>
                        <% if (podeEditar) { %>
                            <td>
                                <a href="clientes?p=edit&nif=<%= c.getNif() %>" class="btn btn-sm btn-primary">Editar</a>
                            </td>
                        <% } %>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="<%= podeEditar ? 7 : 6 %>">Sem registos. </td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>