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
<script>
function filterAnimals(nif) {
    const animalSelect = document.getElementsByName('Animal_IDAnimal')[0];
    const options = animalSelect.options;
    
    let firstMatch = -1;
    for (let i = 0; i < options.length; i++) {
        const option = options[i];
        if (option.value === "") {
            option.style.display = 'block';
            continue;
        }
        
        const tutorNif = option.getAttribute('data-tutor');
        if (tutorNif === nif) {
            option.style.display = 'block';
            if (firstMatch === -1) firstMatch = i;
        } else {
            option.style.display = 'none';
        }
    }
    
    // If current selection is hidden, reset to first available or empty
    if (options[animalSelect.selectedIndex].style.display === 'none') {
        animalSelect.selectedIndex = (firstMatch !== -1) ? firstMatch : 0;
    }
}
</script>
</head>
<body onload="filterAnimals(document.getElementsByName('Cliente_NIF')[0].value)">

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
            
            <% if ("weekend_holiday".equals(request.getParameter("error"))) { %>
                <div style="background-color: #fee2e2; border: 1px solid #ef4444; color: #b91c1c; padding: 10px; border-radius: 4px; margin-bottom: 20px;">
                    ⚠️ <strong>Erro:</strong> A clínica não funciona em fins de semana ou feriados.
                </div>
            <% } %>
            
            <form action="agendamentos" method="post">
                <div style="margin-bottom:15px;">
                    <label>Data e Hora:</label>
                    <input type="datetime-local" name="DataHoraInicio" required class="form-control">
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Motivo:</label>
                    <textarea name="Motivo" rows="3" style="width:100%" class="form-control" placeholder="Descreva o motivo..."></textarea>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Cliente:</label>
                    <select name="Cliente_NIF" required class="form-control" onchange="filterAnimals(this.value)">
                        <% 
                           String selNif = (String) request.getAttribute("selectedClienteNif");
                           List<Cliente> clientes = (List<Cliente>) request.getAttribute("listaClientes");
                           if(clientes!=null) for(Cliente c : clientes) { 
                               boolean sel = c.getNif().equals(selNif);
                        %>
                           <option value="<%= c.getNif() %>" <%= sel ? "selected" : "" %>><%= c.getNomeCompleto() %></option>
                        <% } %>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Animal:</label>
                    <select name="Animal_IDAnimal" class="form-control">
                        <option value="">(Selecione se aplicável)</option>
                        <% 
                           Integer selAnimal = (Integer) request.getAttribute("selectedAnimalId");
                           List<Animal> animais = (List<Animal>) request.getAttribute("listaAnimais");
                           if(animais!=null) for(Animal a : animais) { 
                               boolean sel = a.getIdAnimal().equals(selAnimal);
                        %>
                           <option value="<%= a.getIdAnimal() %>" data-tutor="<%= a.getClienteNif() %>" <%= sel ? "selected" : "" %>><%= a.getNome() %></option>
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
