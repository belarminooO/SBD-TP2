<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Listagem de todos os clientes registados (Pessoas e Empresas).
    Permite visualizar contactos e aceder à edição de dados.
--%>
<%@ page import="java.util.List" %>
<%@ page import="cliente.*" %>
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
                <h2>Lista de Clientes</h2>
                <a href="clientes?p=edit" class="btn btn-primary">+ Novo Cliente</a>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>NIF</th>
                        <th>Nome</th>
                        <th>Tipo</th>
                        <th>Contactos</th>
                        <th>Morada/Localidade</th>
                        <th>Preferência</th>
                        <th>Ações</th>
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
                        <td><%= c.getTipoCliente() %></td>
                        <td><%= (c.getContactos() != null ? c.getContactos() : "-") %></td>
                        <td>
                            <%= (c.getMorada() != null ? c.getMorada() : "") %>
                            <small style="display:block; color:#666;">
                                <%= (c.getConcelho() != null ? c.getConcelho() : "") %> <%= (c.getDistrito() != null ? ", " + c.getDistrito() : "") %>
                            </small>
                        </td>
                        <td><%= (c.getPreferenciasLinguisticas() != null ? c.getPreferenciasLinguisticas() : "-") %></td>
                        <td>
                            <a href="clientes?p=edit&nif=<%= c.getNif() %>" class="btn btn-sm btn-primary">Editar</a>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr><td colspan="5">Sem registos.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
