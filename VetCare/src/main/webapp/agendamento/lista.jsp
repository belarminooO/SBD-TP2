<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="agendamento.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Agendamentos</title>
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
                <h2>Agendamentos</h2>
                <a href="agendamentos?p=new" class="btn btn-primary">+ Novo Agendamento</a>
            </div>
            
            <table>
                <thead>
                    <tr>
                        <th>Data/Hora</th>
                        <th>Motivo</th>
                        <th>Animal (ID)</th>
                        <th>Cliente</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    List<Agendamento> lista = (List<Agendamento>) request.getAttribute("listaAgendamentos");
                    if (lista != null) {
                        for (Agendamento a : lista) {
                    %>
                    <tr>
                        <td><%= a.getDataHoraInicio() %></td>
                        <td><%= a.getMotivo() %></td>
                        <td><%= (a.getAnimalId() != null ? a.getAnimalId() : "-") %></td>
                        <td><%= a.getClienteNif() %></td>
                        <td><span class="badge"><%= (a.getStatus()!=null?a.getStatus():"Agendado") %></span></td>
                        <td>
                            <% if (!"Cancelado".equals(a.getStatus()) && !"Rejeitado".equals(a.getStatus())) { %>
                                <a href="agendamentos?p=cancel&id=<%= a.getIdAgendamento() %>" class="btn btn-sm btn-danger" onclick="return confirm('Confirmar cancelamento?')">Cancelar</a>
                                <a href="agendamentos?p=reject&id=<%= a.getIdAgendamento() %>" class="btn btn-sm btn-secondary" onclick="return confirm('Confirmar rejeição?')">Rejeitar</a>
                                <a href="agendamentos?p=resched&id=<%= a.getIdAgendamento() %>" class="btn btn-sm">Reagendar</a>
                            <% } %>
                        </td>
                    </tr>
                    <% 
                        }
                    } else {
                    %>
                    <tr><td colspan="5">Sem agendamentos.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
