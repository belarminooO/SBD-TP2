<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Painel de Gestão (Dashboard).
    Centraliza indicadores de negócio, alertas operacionais e ferramentas de exportação/importação de dados.
--%>
<%@ page import="java.util.*" %>
<%@ page import="animal.Animal" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Gestão</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager - Painel de Gestão</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/manager" class="nav-link">Dashboard</a>
                <a href="${pageContext.request.contextPath}/manager?p=horarios" class="nav-link">Gerir Escalas</a>
            </nav>
        </div>
    </div>

    <div class="container">

        <div class="card" style="background: #eef2ff; border-left: 5px solid #4f46e5;">
            <h3>Gestão de Escalonamento</h3>
            <p>Atribuição de veterinários aos horários de funcionamento e serviços da clínica.</p>
            <a href="${pageContext.request.contextPath}/manager?p=horarios" class="btn btn-primary">Ir para Gestão de Escalas</a>
        </div>

        <div class="card">
            <h3>Animais com Expectativa de Vida Excedida</h3>
            <ul>
            <% List<Animal> velhos = (List<Animal>) request.getAttribute("animaisVelhos");
               if(velhos!=null) for(Animal a : velhos) { %>
               <li><%= a.getNome() %> (<%= a.getCatalogoNomeComum() %>)</li>
            <% } %>
            </ul>
        </div>

        <div class="card">
            <h3>Tutores com Animais com Excesso de Peso</h3>
            <ul>
            <% Map<String, Integer> obesos = (Map<String, Integer>) request.getAttribute("tutoresObesos");
               if(obesos!=null) for(Map.Entry<String,Integer> e : obesos.entrySet()) { %>
               <li><%= e.getKey() %>: <%= e.getValue() %> animais</li>
            <% } %>
            </ul>
        </div>

        <div class="card">
            <h3>Top Cancelamentos (Último Trimestre)</h3>
            <ul>
            <% List<String> cancels = (List<String>) request.getAttribute("topCancelamentos");
               if(cancels!=null) for(String s : cancels) { %>
               <li><%= s %></li>
            <% } %>
            </ul>
        </div>

        <div class="card" style="border-left: 5px solid #6366f1;">
            <h3>Exportar Perfil de Animal (XML/JSON)</h3>
            <p style="font-size: 0.9rem; color: #666;">Seleção de animal para exportação da ficha e histórico clínico completo.</p>
            <form action="manager" method="get" style="display:flex; gap:10px; align-items:flex-end;">
                <input type="hidden" name="p" value="xml">
                <div style="flex:1;">
                    <label>Animal:</label>
                    <select name="id" required style="width:100%; padding: 5px; border-radius: 4px; border: 1px solid #ddd;">
                        <% List<Animal> animalsForExport = animal.AnimalDAO.getAll();
                           if(animalsForExport!=null) for(Animal a : animalsForExport) { %>
                           <option value="<%= a.getIdAnimal() %>"><%= a.getNome() %> (Tutor NIF: <%= a.getClienteNif() %>)</option>
                        <% } %>
                    </select>
                </div>
                <button type="submit" name="p" value="xml" class="btn btn-secondary">Exportar XML</button>
                <button type="submit" name="p" value="json" class="btn btn-secondary">Exportar JSON</button>
            </form>
        </div>

        <div class="card">
            <h3>Importar Perfil de Animal (XML/JSON)</h3>
            <p style="font-size: 0.9rem; color: #666;">Inserção de conteúdo para importação da ficha e do histórico clínico.</p>
            <form action="manager" method="get">
                <input type="hidden" name="p" value="import">
                <div style="display:flex; gap:10px;">
                    <div style="flex:1;">
                        <label>Conteúdo XML:</label>
                        <textarea name="xmlData" style="width:100%; height:80px;" placeholder="Conteúdo XML..."></textarea>
                    </div>
                    <div style="flex:1;">
                        <label>Conteúdo JSON:</label>
                        <textarea name="jsonData" style="width:100%; height:80px;" placeholder="Conteúdo JSON..."></textarea>
                    </div>
                </div>
                <button type="submit" class="btn btn-secondary" style="margin-top:10px;">Importar Dados</button>
            </form>
            <% String msg = request.getParameter("msg"); if(msg!=null){ %>
                <div style="margin-top:10px; padding:10px; background:#d4edda; color:#155724; border-radius:4px;"><%= msg %></div>
            <% } %>
        </div>

        <div class="card">
            <h3>Agenda Próxima Semana</h3>
            <ul>
            <% Map<String, Integer> agenda = (Map<String, Integer>) request.getAttribute("agendaSemana");
               if(agenda!=null) for(Map.Entry<String,Integer> e : agenda.entrySet()) { %>
               <li><%= e.getKey() %>: <%= e.getValue() %> agendamentos</li>
            <% } %>
            </ul>
        </div>

    </div>

</body>
</html>
