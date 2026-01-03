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

            <% String msg = (String) session.getAttribute("msg");
               if(msg != null) { session.removeAttribute("msg"); %>
                <div class="badge" style="background:#d4edda; color:#155724; padding:10px; margin-bottom:15px; display:block; width:100%; border:1px solid #c3e6cb;">
                   <%= msg %>
                </div>
            <% } %>

            <% String msgErr = (String) session.getAttribute("msgErr");
               if(msgErr != null) { session.removeAttribute("msgErr"); %>
                <div class="badge" style="background:#f8d7da; color:#721c24; padding:10px; margin-bottom:15px; display:block; width:100%; border:1px solid #f5c6cb;">
                   <%= msgErr %>
                </div>
            <% } %>

            <table>
                <thead>
                    <tr>
                         <th></th>
                        <th>Nome</th>
                        <th>Nome Comum</th>
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
                            String base64 = a.getFotografiaBase64();
                    %>
                     <tr>
                        <td style="width: 50px; text-align: center;">
                            <% if (base64 != null) { %>
                                <img src="<%= base64 %>" 
                                     style="width: 40px; height: 40px; object-fit: cover; border-radius: 50%; border: 1px solid #ddd;">
                            <% } else { %>
                                <div style="width: 40px; height: 40px; border-radius: 50%; background: #eee; display: flex; align-items: center; justify-content: center; font-size: 10px; color: #999;">N/A</div>
                            <% } %>
                        </td>
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
                    <tr><td colspan="6">Sem registos.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
