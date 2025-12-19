<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Home</title>
<link rel="stylesheet" href="css/style.css">
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <p>Sistema de Gestão Veterinária</p>
        </div>
    </div>

    <div class="container">
        <div class="card" style="text-align:center; padding: 40px;">
            <h2>Bem-vindo ao VetCare</h2>
            <p>Selecione um módulo para começar:</p>
            
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-top: 30px;">
                <a href="clientes" class="btn btn-primary" style="padding: 20px; font-size: 1.2rem;">👥 Clientes</a>
                <a href="animais" class="btn btn-primary" style="padding: 20px; font-size: 1.2rem;">🐾 Animais</a>
                <a href="vets" class="btn btn-primary" style="padding: 20px; font-size: 1.2rem;">🩺 Veterinários</a>
                <a href="agendamentos" class="btn btn-primary" style="padding: 20px; font-size: 1.2rem;">📅 Agendamentos</a>
                <a href="manager" class="btn btn-secondary" style="padding: 20px; font-size: 1.2rem;">📊 Gestão/Reports</a>
            </div>
        </div>
        
        <div class="card" style="margin-top:20px;">
            <h3>Dica de Instalação</h3>
            <p>Certifique-se de que o driver MySQL (`mysql-connector-j-x.x.x.jar`) está na pasta <code>WEB-INF/lib</code> para que a ligação à base de dados funcione.</p>
        </div>
    </div>

</body>
</html>
