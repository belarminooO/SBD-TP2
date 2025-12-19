<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="clinica.TipoServico" %>
<%@ page import="cliente.Cliente" %>
<%@ page import="animal.Animal" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Novo Agendamento</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/agendamentos" class="nav-link">Agendamentos</a>
            </nav>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Marcar Consulta/Serviço</h2>
            
            <form action="agendamentos" method="post">
                <div style="margin-bottom:15px;">
                    <label>Data e Hora:</label>
                    <input type="datetime-local" name="DataHoraInicio" required>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Motivo:</label>
                    <textarea name="Motivo" rows="3" style="width:100%"></textarea>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Cliente:</label>
                    <select name="Cliente_NIF" required>
                        <% List<Cliente> clientes = (List<Cliente>) request.getAttribute("listaClientes");
                           if(clientes!=null) for(Cliente c : clientes) { %>
                           <option value="<%= c.getNif() %>"><%= c.getNomeCompleto() %></option>
                        <% } %>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Animal:</label>
                    <select name="Animal_IDAnimal">
                        <option value="">(Selecione se aplicável)</option>
                        <% List<Animal> animais = (List<Animal>) request.getAttribute("listaAnimais");
                           if(animais!=null) for(Animal a : animais) { %>
                           <option value="<%= a.getIdAnimal() %>"><%= a.getNome() %></option>
                        <% } %>
                    </select>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Serviço:</label>
                    <select name="TipoServico" required>
                        <% List<TipoServico> tipos = (List<TipoServico>) request.getAttribute("listaTipos");
                           if(tipos!=null) for(TipoServico t : tipos) { %>
                           <option value="<%= t.getIdServico() %>"><%= t.getNome() %></option>
                        <% } %>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary">Agendar</button>
            </form>
        </div>
    </div>

</body>
</html>
