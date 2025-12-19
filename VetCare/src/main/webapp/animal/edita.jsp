<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="animal.*" %>
<%@ page import="cliente.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Editar Animal</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/animais" class="nav-link">Animais</a>
            </nav>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Ficha do Animal</h2>
            
            <form action="animais" method="post">
                <div style="margin-bottom:15px;">
                    <label>Nome:</label>
                    <input type="text" name="Nome" required style="width:100%">
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Espécie:</label>
                    <select name="Catalogo_NomeComum" required style="width:100%">
                        <% 
                        List<Catalogo> especies = (List<Catalogo>) request.getAttribute("listaEspecies");
                        if(especies!=null) for(Catalogo c : especies) { %>
                        <option value="<%= c.getNomeComum() %>"><%= c.getNomeComum() %></option>
                        <% } %>
                    </select>
                </div>

                 <div style="margin-bottom:15px;">
                    <label>Raça:</label>
                    <input type="text" name="Raca" required style="width:100%">
                </div>
                
                 <div style="margin-bottom:15px;">
                    <label>Sexo:</label>
                    <select name="Sexo">
                        <option value="M">Macho</option>
                        <option value="F">Fêmea</option>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Tutor:</label>
                    <select name="Cliente_NIF" required style="width:100%">
                        <% 
                        List<Cliente> clientes = (List<Cliente>) request.getAttribute("listaClientes");
                        if(clientes!=null) for(Cliente cli : clientes) { %>
                        <option value="<%= cli.getNif() %>"><%= cli.getNomeCompleto() %> (<%= cli.getNif() %>)</option>
                        <% } %>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Data Nascimento:</label>
                    <input type="date" name="DataNascimento" required>
                </div>
                
                 <div style="margin-bottom:15px;">
                    <label>Peso Atual (kg):</label>
                    <input type="number" step="0.01" name="PesoAtual" required>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Cores:</label>
                    <input type="text" name="Cores" required>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Fotografia (URL):</label>
                    <input type="text" name="Fotografia" placeholder="http://...">
                </div>

                <button type="submit" class="btn btn-primary">Gravar</button>
                <a href="animais" class="btn btn-secondary">Cancelar</a>
            </form>
        </div>
    </div>

</body>
</html>
