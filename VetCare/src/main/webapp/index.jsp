<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="auth.Utilizador" %>
<%@ page import="auth.Role" %>
<%
    Utilizador user = (Utilizador) session.getAttribute("utilizador");
    if (user == null) {
        response. sendRedirect("login");
        return;
    }
    
    boolean isGerente = user.getRole() == Role.GERENTE;
    boolean isVeterinario = user.getRole() == Role.VETERINARIO;
    boolean isCliente = user.getRole() == Role.CLIENTE;
%>
<! DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>VetCare - Home</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        /* Estilos do topo/header do segundo código */
        .top-bar {
            background: linear-gradient(135deg, #2c5282 0%, #4299e1 100%);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }
        
        .top-bar .brand {
            display: flex;
            align-items: center;
            color: white;
            font-size: 24px;
            font-weight: 700;
        }
        
        . top-bar .brand span {
            margin-right: 10px;
            font-size: 30px;
        }
        
        .user-menu {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .user-info-box {
            display: flex;
            align-items: center;
            background: rgba(255, 255, 255, 0.15);
            padding: 8px 16px;
            border-radius: 25px;
            color: white;
        }
        
        .user-info-box .avatar {
            width: 35px;
            height: 35px;
            background: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 10px;
            font-size: 18px;
            color: black;
        }
        
        .user-info-box .user-details {
            line-height: 1.3;
        }
        
        .user-info-box .username {
            font-weight: 600;
            font-size: 14px;
        }
        
        .user-info-box .role-text {
            font-size: 11px;
            opacity: 0.9;
        }
        
        .btn-logout {
            display: flex;
            align-items: center;
            gap: 8px;
            background: linear-gradient(135deg, #e53e3e 0%, #c53030 100%);
            color: white;
            padding: 10px 20px;
            border-radius:  25px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.3s ease;
            border: none;
            cursor: pointer;
        }
        
        .btn-logout:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(229, 62, 62, 0.4);
        }
        
        /* Estilos do container do primeiro código */
        .container {
            max-width: 1200px;
            margin: 20px auto;
            padding: 0 20px;
        }
        
        .card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            margin-bottom: 20px;
        }
        
        /* Estilos dos model cards do terceiro código */
        .modules-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 25px;
            padding: 40px;
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .module-card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            text-align: center;
            text-decoration: none;
            color: #2d3748;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
            transition: all 0.3s ease;
            border: 2px solid transparent;
        }
        
        . module-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.12);
            border-color: #4299e1;
        }
        
        .module-card .icon {
            font-size: 45px;
            margin-bottom: 15px;
        }
        
        .module-card h3 {
            font-size:  18px;
            margin-bottom: 8px;
            color: #2d3748;
        }
        
        . module-card p {
            font-size: 13px;
            color: #718096;
        }
        
        /* Estilos dos banners informativos do primeiro código */
        .info-box {
            margin-top: 20px;
            padding: 15px 20px;
            border-radius: 5px;
        }
        
        .info-veterinario {
            background: #ebf8ff;
            border: 1px solid #4299e1;
        }
        
        . info-cliente {
            background:  #fffbeb;
            border: 1px solid #f59e0b;
        }
        
        /* Estilos para a seção de boas-vindas */
        .welcome-section {
            text-align: center;
            padding: 50px 20px;
            background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
            margin-bottom: 20px;
        }
        
        .welcome-section h1 {
            font-size: 32px;
            color: #2d3748;
            margin-bottom: 10px;
        }
        
        .welcome-section p {
            color: #718096;
            font-size: 16px;
        }
    </style>
</head>
<body style="margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f7fafc;">
    
    <!-- Top Bar do segundo código -->
    <div class="top-bar">
        <div class="brand">
            <span>🐾</span> VetCare Manager
        </div>
        
        <div class="user-menu">
            <div class="user-info-box">
                <div class="avatar">
                    <% if (isGerente) { %>👔<% } 
                       else if (isVeterinario) { %>👨‍⚕️<% } 
                       else { %>👤<% } %>
                </div>
                <div class="user-details">
                    <div class="username"><%= user.getUsername() %></div>
                    <div class="role-text"><%= user.getRole().name() %></div>
                </div>
            </div>
            
            <a href="logout" class="btn-logout">
                🚪 Sair
            </a>
        </div>
    </div>

    <!-- Welcome Section -->
    <div class="welcome-section">
        <h1>Bem-vindo, <%= user.getUsername() %>!</h1>
        <p>Selecione um módulo para começar a trabalhar</p>
    </div>

    <!-- Container principal do primeiro código -->
    <div class="container">
       <!-- Modules Grid -->
        <div class="modules-grid">
            
            <%-- GERENTE E VETERINÁRIO - Clientes --%>
            <% if (isGerente || isVeterinario) { %>
            <a href="clientes" class="module-card">
                <div class="icon">👥</div>
                <h3>Clientes</h3>
                <p>Gerir fichas de clientes</p>
            </a>
            <% } %>
            
            <%-- GERENTE E VETERINÁRIO - Animais --%>
            <% if (isGerente || isVeterinario) { %>
            <a href="animais" class="module-card">
                <div class="icon">🐕</div>
                <h3>Animais</h3>
                <p>Gerir fichas de animais</p>
            </a>
            <% } %>
            
            <%-- CLIENTE - Os Meus Animais --%>
            <% if (isCliente && user. getNifCliente() != null) { %>
            <a href="animais?clienteNif=<%= user.getNifCliente() %>" class="module-card">
                <div class="icon">🐾</div>
                <h3>Os Meus Animais</h3>
                <p>Ver os seus animais</p>
            </a>
            <% } %>
            
            <%-- GERENTE - Veterinários --%>
            <% if (isGerente) { %>
            <a href="vets" class="module-card">
                <div class="icon">👨‍⚕️</div>
                <h3>Veterinários</h3>
                <p>Gerir equipa veterinária</p>
            </a>
            <% } %>
            
            <%-- GERENTE E VETERINÁRIO - Agendamentos --%>
            <% if (isGerente || isVeterinario) { %>
            <a href="agendamentos" class="module-card">
                <div class="icon">📅</div>
                <h3>Agendamentos</h3>
                <p>Gerir consultas e marcações</p>
            </a>
            <% } %>
            
            <%-- CLIENTE - Os Meus Agendamentos --%>
            <% if (isCliente && user. getNifCliente() != null) { %>
            <a href="agendamentos?clienteNif=<%= user.getNifCliente() %>" class="module-card">
                <div class="icon">📆</div>
                <h3>Os Meus Agendamentos</h3>
                <p>Ver as suas marcações</p>
            </a>
            <% } %>
            
            <%-- GERENTE E VETERINÁRIO - Histórico Médico --%>
            <% if (isGerente || isVeterinario) { %>
            <a href="historico" class="module-card">
                <div class="icon">📑</div>
                <h3>Histórico Médico</h3>
                <p>Consultar registos clínicos</p>
            </a>
            <% } %>
            
            <%-- GERENTE - Gestão/Relatórios --%>
            <% if (isGerente) { %>
            <a href="manager" class="module-card">
                <div class="icon">📊</div>
                <h3>Gestão / Relatórios</h3>
                <p>Estatísticas e relatórios</p>
            </a>
            <% } %>
            
        </div>

        <!-- Info boxes do primeiro código -->
        <% if (isVeterinario) { %>
        <div class="card info-box info-veterinario">
            <h3>ℹ️ Perfil Veterinário</h3>
            <p>Pode consultar todos os clientes e animais, gerir agendamentos e históricos médicos. A edição de dados de clientes está restrita.</p>
        </div>
        <% } %>
        
        <% if (isCliente) { %>
        <div class="card info-box info-cliente">
            <h3>ℹ️ Perfil Cliente</h3>
            <p>Como cliente, tem acesso apenas aos seus animais e aos seus agendamentos. </p>
        </div>
        <% } %>
    </div>
</body>
</html>