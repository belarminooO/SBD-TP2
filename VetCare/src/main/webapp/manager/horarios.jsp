<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="manager.EscalonamentoDAO" %>
<%@ page import="clinica.*" %>
<%@ page import="veterinario.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Gestão de Horários</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/manager" class="nav-link">Dashboard</a>
                <a href="${pageContext.request.contextPath}/manager?p=horarios" class="nav-link">Escalas</a>
            </nav>
        </div>
    </div>

    <div class="container">
        
        <div class="card">
            <h2>4.2 Atribuir Supervisão (Veterinários)</h2>
            <form action="manager" method="post">
                <input type="hidden" name="action" value="atribuir">
                <div style="display:flex; gap:10px; align-items:flex-end;">
                    <div>
                        <label>Horário (Slot):</label><br>
                        <select name="IDHorario" required>
                             <% List<Horario> horas = (List<Horario>) request.getAttribute("listaHorarios");
                               if(horas!=null) for(Horario h : horas) { %>
                               <option value="<%= h.getIdHorario() %>"><%= h.toString() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div>
                        <label>Serviço:</label><br>
                        <select name="IDServico" required>
                             <% List<TipoServico> tipos = (List<TipoServico>) request.getAttribute("listaTipos");
                               if(tipos!=null) for(TipoServico t : tipos) { %>
                               <option value="<%= t.getIdServico() %>"><%= t.getNome() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div>
                        <label>Veterinário:</label><br>
                        <select name="NLicenca" required>
                             <% List<Veterinario> vets = (List<Veterinario>) request.getAttribute("listaVets");
                               if(vets!=null) for(Veterinario v : vets) { %>
                               <option value="<%= v.getNLicenca() %>"><%= v.getNome() %></option>
                            <% } %>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary">Atribuir</button>
                </div>
            </form>
        </div>
        
        <div class="card">
            <h3>Horários Atribuídos</h3>
            <table>
                <thead>
                    <tr>
                        <th>Dia/Hora</th>
                        <th>Serviço</th>
                        <th>Veterinário</th>
                    </tr>
                </thead>
                <tbody>
                 <% List<EscalonamentoDAO.Escala> escalas = (List<EscalonamentoDAO.Escala>) request.getAttribute("listaEscalas");
                    if(escalas!=null) for(EscalonamentoDAO.Escala e : escalas) { %>
                    <tr>
                        <td><%= e.dia %> <%= e.hora %></td>
                        <td><%= e.servicoNome %></td>
                        <td><%= e.vetNome %></td>
                    </tr>
                 <% } %>
                </tbody>
            </table>
        </div>

    </div>

</body>
</html>
