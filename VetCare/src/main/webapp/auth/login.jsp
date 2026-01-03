<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<! DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VetCare - Login</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 20px;
        }
        
        .login-container {
            width: 100%;
            max-width: 420px;
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            overflow: hidden;
        }
        
        .login-header {
            background: linear-gradient(135deg, #2c5282 0%, #4299e1 100%);
            padding: 40px 30px;
            text-align:  center;
            color: white;
        }
        
        .login-header . logo {
            font-size:  50px;
            margin-bottom:  10px;
        }
        
        .login-header h1 {
            font-size: 28px;
            font-weight: 700;
            margin-bottom: 5px;
        }
        
        .login-header p {
            font-size: 14px;
            opacity: 0.9;
        }
        
        .login-form {
            padding: 40px 30px;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight:  600;
            color: #2d3748;
            font-size: 14px;
        }
        
        . form-group input {
            width: 100%;
            padding:  14px 16px;
            border: 2px solid #e2e8f0;
            border-radius:  10px;
            font-size: 16px;
            transition: all 0.3s ease;
            background: #f7fafc;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #4299e1;
            background: white;
            box-shadow: 0 0 0 4px rgba(66, 153, 225, 0.15);
        }
        
        .form-group input::placeholder {
            color: #a0aec0;
        }
        
        .btn-login {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition:  all 0.3s ease;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(72, 187, 120, 0.3);
        }
        
        .btn-login:active {
            transform: translateY(0);
        }
        
        . error-message {
            background: linear-gradient(135deg, #fed7d7 0%, #feb2b2 100%);
            color: #c53030;
            padding: 14px 16px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 500;
            border-left: 4px solid #c53030;
        }
        
        .divider {
            display: flex;
            align-items: center;
            margin: 30px 0 20px 0;
            color: #a0aec0;
            font-size: 12px;
        }
        
        .divider::before,
        .divider::after {
            content: '';
            flex:  1;
            height: 1px;
            background: #e2e8f0;
        }
        
        .divider span {
            padding: 0 15px;
        }
        
        .role-info {
            background: #f7fafc;
            border-radius: 10px;
            padding: 20px;
        }
        
        .role-info h3 {
            font-size: 13px;
            color: #718096;
            text-transform: uppercase;
            letter-spacing: 1px;
            margin-bottom:  15px;
        }
        
        .role-item {
            display: flex;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e2e8f0;
        }
        
        .role-item:last-child {
            border-bottom: none;
        }
        
        .role-icon {
            width: 36px;
            height: 36px;
            border-radius:  8px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 12px;
            font-size:  16px;
        }
        
        .role-gerente . role-icon { background: #c6f6d5; }
        . role-veterinario .role-icon { background: #bee3f8; }
        . role-cliente .role-icon { background: #feebc8; }
        
        .role-item strong {
            color: #2d3748;
            font-size: 14px;
        }
        
        .role-item span {
            color: #718096;
            font-size:  12px;
            margin-left: 5px;
        }
        
        .footer-text {
            text-align: center;
            padding: 20px;
            background: #f7fafc;
            color: #a0aec0;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-header">
            <div class="logo">🐾</div>
            <h1>VetCare</h1>
            <p>Sistema de Gestão Veterinária</p>
        </div>
        
        <div class="login-form">
            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    ⚠️ <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <form method="post" action="${pageContext.request.contextPath}/login">
                <div class="form-group">
                    <label for="username">👤 Nome de Utilizador</label>
                    <input type="text" id="username" name="username" required 
                           placeholder="Introduza o seu username"
                           autocomplete="username">
                </div>
                
                <div class="form-group">
                    <label for="password">🔒 Password</label>
                    <input type="password" id="password" name="password" required
                           placeholder="Introduza a sua password"
                           autocomplete="current-password">
                </div>
                
                <button type="submit" class="btn-login">Entrar</button>
            </form>
            
            <div class="divider">
                <span>PERFIS DE ACESSO</span>
            </div>
            
            <div class="role-info">
                <div class="role-item role-gerente">
                    <div class="role-icon">👔</div>
                    <div>
                        <strong>Gerente</strong>
                        <span>- Acesso total ao sistema</span>
                    </div>
                </div>
                <div class="role-item role-veterinario">
                    <div class="role-icon">👨‍⚕️</div>
                    <div>
                        <strong>Veterinário</strong>
                        <span>- Consulta e agendamentos</span>
                    </div>
                </div>
                <div class="role-item role-cliente">
                    <div class="role-icon">👤</div>
                    <div>
                        <strong>Cliente</strong>
                        <span>- Os seus dados e animais</span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="footer-text">
            © 2026 VetCare Manager - Todos os direitos reservados
        </div>
    </div>
</body>
</html>