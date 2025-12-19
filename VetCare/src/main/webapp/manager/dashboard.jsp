<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            </nav>
        </div>
    </div>

    <div class="container">
        
        <div class="card">
            <h3>4.5 Animais com Expetativa de Vida Excedida</h3>
            <ul>
            <% List<Animal> velhos = (List<Animal>) request.getAttribute("animaisVelhos");
               if(velhos!=null) for(Animal a : velhos) { %>
               <li><%= a.getNome() %> (<%= a.getCatalogoNomeComum() %>)</li>
            <% } %>
            </ul>
        </div>
        
        <div class="card">
            <h3>4.6 Tutores com Animais com Excesso de Peso</h3>
            <ul>
            <% Map<String, Integer> obesos = (Map<String, Integer>) request.getAttribute("tutoresObesos");
               if(obesos!=null) for(Map.Entry<String,Integer> e : obesos.entrySet()) { %>
               <li><%= e.getKey() %>: <%= e.getValue() %> animais</li>
            <% } %>
            </ul>
        </div>

        <div class="card">
            <h3>4.7 Top Cancelamentos (Último Trimestre)</h3>
            <ul>
            <% List<String> cancels = (List<String>) request.getAttribute("topCancelamentos");
               if(cancels!=null) for(String s : cancels) { %>
               <li><%= s %></li>
            <% } %>
            </ul>
        </div>
        
        <div class="card">
            <h3>4.8 Agenda Próxima Semana</h3>
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
