<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Gestão de escalas e horários dos veterinários.
    Permite atribuir turnos e serviços específicos a cada médico, prevenindo conflitos de agendamento.
--%>
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

    </div>
 
<script>
    function updateClinic(val) {
        window.location.href = 'manager?p=horarios&filterClinica=' + val;
    }

    function fillForm(idHorario, idServico, nLicenca, idClinica) {
        var currentFilter = document.getElementById('selClinica').value;
        
        // Se a clínica selecionada no filtro for DIFERENTE da clínica do horário a editar
        if(currentFilter != idClinica) {
            if(confirm("Este horário pertence a uma clínica que não está selecionada. Deseja mudar o filtro para editar?")) {
                 // Recarrega a página com o filtro certo e parâmetros para abrir a edição automaticamente
                 window.location.href = 'manager?p=horarios&filterClinica=' + idClinica + '&editH=' + idHorario + '&editS='+ idServico + '&editV=' + nLicenca;
            }
            return;
        }

        // Se o filtro já estiver certo, preenche o formulário diretamente
        var slotSelect = document.getElementById('selHorario');
        slotSelect.value = idHorario;
        
        // Fallback: Se por algum motivo o valor não for aceite (ex: filtro errado), avisa
        if(slotSelect.value != idHorario) {
             alert("Erro: Não foi possível encontrar o horário na lista. Tente mudar a clínica manualmente.");
             return;
        }

        document.getElementById('selServico').value = idServico;
        document.getElementById('selVet').value = nLicenca;
        
        document.getElementById('oldIDHorario').value = idHorario;
        document.getElementById('oldIDServico').value = idServico;
        
        document.getElementById('btnSubmit').innerText = "Atualizar Escala";
        document.getElementById('btnSubmit').classList.remove('btn-primary');
        document.getElementById('btnSubmit').classList.add('btn-warning');
        document.getElementById('btnCancel').style.display = 'inline-block';
        
        window.scrollTo({ top: 0, behavior: 'smooth' });
        document.querySelector('.card').style.borderColor = '#f59e0b';
    }
    
    function cancelEdit() {
        document.getElementById('formEscala').reset();
        document.getElementById('oldIDHorario').value = "";
        document.getElementById('oldIDServico').value = "";
        
        document.getElementById('btnSubmit').innerText = "Atribuir Nova";
        document.getElementById('btnSubmit').classList.remove('btn-warning');
        document.getElementById('btnSubmit').classList.add('btn-primary');
        document.getElementById('btnCancel').style.display = 'none';
        document.querySelector('.card').style.borderColor = '';
    }
    
    // Auto-open edit if params exist
    window.onload = function() {
        const urlParams = new URLSearchParams(window.location.search);
        if(urlParams.has('editH')) {
            fillForm(urlParams.get('editH'), urlParams.get('editS'), urlParams.get('editV'), urlParams.get('filterClinica'));
        }
    }
</script>

    <div class="container">
        
        <% String msg = request.getParameter("msg");
           if (msg != null && !msg.isEmpty()) { %>
           <div class="card" style="background-color: #fff3cd; border-color: #ffeeba; color: #856404; margin-bottom: 20px;">
               <strong>Aviso:</strong> <%= java.net.URLDecoder.decode(msg, "UTF-8") %>
           </div>
        <% } %>

        <div class="card">
            <h2>4.2 Atribuir Supervisão (Veterinários)</h2>
            <form action="manager" method="post" id="formEscala">
                <input type="hidden" name="action" value="atribuir">
                <input type="hidden" name="oldIDHorario" id="oldIDHorario">
                <input type="hidden" name="oldIDServico" id="oldIDServico">
                
                <div style="display:flex; gap:10px; align-items:flex-end;">
                    <div>
                        <label>Clínica:</label><br>
                        <select id="selClinica" onchange="updateClinic(this.value)" class="form-control" style="width: 200px;">
                            <% 
                            List<clinica.Clinica> allClinicas = (List<clinica.Clinica>) request.getAttribute("listaClinicas");
                            Integer selClinicaId = (Integer) request.getAttribute("selectedClinicaId");
                            
                            if(allClinicas != null) for(clinica.Clinica c : allClinicas) {
                                boolean isSel = (selClinicaId != null && c.getIdClinica() == selClinicaId);
                            %>
                                <option value="<%= c.getIdClinica() %>" <%= isSel ? "selected" : "" %>><%= c.getLocalidade() %></option>
                            <% } %>
                        </select>
                    </div>

                    <div>
                        <label>Horário (Slot):</label><br>
                        <select name="IDHorario" id="selHorario" required class="form-control" style="width: 250px;">
                             <% 
                                List<Horario> filteredHoras = (List<Horario>) request.getAttribute("listaHorarios");
                                if(filteredHoras != null && !filteredHoras.isEmpty()) {
                                    for(Horario h : filteredHoras) { 
                             %>
                                <option value="<%= h.getIdHorario() %>">
                                    <%= h.getDiaSemana() %>: <%= h.getHoraInicio().toString().substring(0,5) %> - <%= h.getHoraFim().toString().substring(0,5) %>
                                </option>
                             <%     }
                                } else { %>
                                <option value="">(Sem horários para esta clínica)</option>
                             <% } %>
                        </select>
                    </div>
                    <div>
                        <label>Serviço:</label><br>
                        <select name="IDServico" id="selServico" required class="form-control">
                             <% List<TipoServico> tipos = (List<TipoServico>) request.getAttribute("listaTipos");
                                if(tipos!=null) for(TipoServico t : tipos) { %>
                                <option value="<%= t.getIdServico() %>"><%= t.getNome() %></option>
                             <% } %>
                        </select>
                    </div>
                    <div>
                        <label>Veterinário:</label><br>
                        <select name="NLicenca" id="selVet" required class="form-control">
                             <% List<Veterinario> vets = (List<Veterinario>) request.getAttribute("listaVets");
                                if(vets!=null) for(Veterinario v : vets) { %>
                                <option value="<%= v.getNLicenca() %>"><%= v.getNome() %></option>
                             <% } %>
                        </select>
                    </div>
                    <button type="submit" id="btnSubmit" class="btn btn-primary" style="margin-bottom:0">Atribuir Nova</button>
                    <button type="button" id="btnCancel" class="btn btn-secondary" style="display:none; margin-bottom:0" onclick="cancelEdit()">Cancelar</button>
                </div>
            </form>
        </div>

        <div class="card">
            <h3>Horários Atribuídos</h3>
            <table style="width:100%">
                <thead>
                    <tr>
                        <th>Clínica</th>
                        <th>Dia/Hora</th>
                        <th>Serviço</th>
                        <th>Veterinário</th>
                        <th style="width:100px">Ações</th>
                    </tr>
                </thead>
                <tbody>
                 <% List<EscalonamentoDAO.Escala> escalas = (List<EscalonamentoDAO.Escala>) request.getAttribute("listaEscalas");
                    if(escalas!=null) for(EscalonamentoDAO.Escala e : escalas) { %>
                    <tr>
                        <td><strong><%= e.clinica %></strong></td>
                        <td><%= e.dia %> <%= e.hora %></td>
                        <td><%= e.servicoNome %></td>
                        <td><%= e.vetNome %></td>
                        <td>
                            <% 
                                // Fetch all hours to lookup clinic IDs for the edit button
                                // This is necessary because the table shows all assignments, not just the filtered ones.
                                List<Horario> allLookUp = agendamento.AgendamentoDAO.getAllHorarios();
                                int eClinicaId = 0;
                                if(allLookUp != null) {
                                    for(Horario h : allLookUp) {
                                        if(h.getIdHorario() == e.idHorario) {
                                            eClinicaId = h.getClinicaId();
                                            break;
                                        }
                                    }
                                }
                            %>
                            <button type="button" class="btn btn-warning" style="padding: 2px 8px; font-size: 0.8rem;" 
                                onclick="fillForm('<%= e.idHorario %>', '<%= e.idServico %>', '<%= e.nLicenca %>', '<%= eClinicaId %>')">
                                Editar
                            </button>
                        </td>
                    </tr>
                 <% } %>
                </tbody>
            </table>
        </div>

    </div>

</body>
</html>
