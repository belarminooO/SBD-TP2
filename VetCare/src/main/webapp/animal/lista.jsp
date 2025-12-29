<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Listagem geral de animais registados.
    Permite pesquisar por tutor e aceder às fichas de edição, genealogia e histórico.
--%>
<%@ page import="java.util.List" %>
<%@ page import="animal.Animal" %>
<%@ page import="cliente.Cliente" %>
<%@ page import="cliente.ClienteDAO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Animais</title>
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
                <h2>Lista de Animais</h2>
                <div style="display:flex; gap:10px;">
                    <form action="animais" method="get" style="display:flex;">
                        <input type="text" name="search" list="tutoresList" placeholder="Pesquisar por Tutor..." style="padding:5px;">
                        <datalist id="tutoresList">
                            <%
                            List<Cliente> allClients = ClienteDAO.getAll();
                            if(allClients!=null) for(Cliente c : allClients) { %>
                                <option value="<%= c.getNomeCompleto() %>">
                            <% } %>
                        </datalist>
                        <button type="submit" class="btn btn-sm btn-secondary" style="margin-left:5px;">Pesquisar</button>
                    </form>
                    <a href="animais?p=edit" class="btn btn-primary">+ Novo Animal</a>
                </div>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Espécie</th>
                        <th>Idade / Escalão</th>
                        <th>Tutor (NIF)</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Animal> lista = (List<Animal>) request.getAttribute("listaAnimais");
                    if (lista != null) {
                        for (Animal a : lista) {
                    %>
                    <tr>
                        <td><%= a.getNome() %></td>
                        <td><%= a.getCatalogoNomeComum() %></td>
                        <td>
                            <%= a.getIdadeFormatada() %><br>
                            <span class="badge" style="background: #e9ecef; color: #495057;"><%= a.getEscalaoEtario(a.getExpectativaVida()) %></span>
                        </td>
                        <td><%= a.getClienteNif() %></td>
                        <td>
                            <a href="animais?p=edit&id=<%= a.getIdAnimal() %>" class="btn btn-sm">Editar</a>
                            <a href="animais?p=genealogia&id=<%= a.getIdAnimal() %>" class="btn btn-sm btn-secondary">Genealogia</a>
                            <a href="historico?idAnimal=<%= a.getIdAnimal() %>" class="btn btn-sm btn-secondary">Histórico</a>
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
