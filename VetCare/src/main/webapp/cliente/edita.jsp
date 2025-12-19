<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="cliente.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Editar Cliente</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script>
function toggleEmpresa(val) {
    document.getElementById('divCapital').style.display = (val === 'Empresa') ? 'block' : 'none';
}
</script>
</head>
<body>

    <div class="header">
        <div class="container">
            <h1>VetCare Manager</h1>
            <nav>
                <a href="${pageContext.request.contextPath}/" class="nav-link">Home</a>
                <a href="${pageContext.request.contextPath}/clientes" class="nav-link">Clientes</a>
            </nav>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Ficha de Cliente</h2>
            <%
            Cliente c = (Cliente) request.getAttribute("cliente");
            String nif = (c != null) ? c.getNif() : "";
            String nome = (c != null) ? c.getNomeCompleto() : "";
            // ... map other fields
            %>
            
            <form action="clientes" method="post">
                <div style="margin-bottom:15px;">
                    <label>Tipo de Cliente:</label>
                    <select name="TipoCliente" onchange="toggleEmpresa(this.value)">
                        <option value="Pessoa" <%= (c instanceof ClientePessoa) ? "selected" : "" %>>Pessoa</option>
                        <option value="Empresa" <%= (c instanceof ClienteEmpresa) ? "selected" : "" %>>Empresa</option>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>NIF:</label>
                    <input type="text" name="NIF" value="<%= nif %>" required pattern="\d{9}">
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Nome Completo:</label>
                    <input type="text" name="NomeCompleto" value="<%= nome %>" required style="width:100%">
                </div>
                
                <div id="divCapital" style="display:none; margin-bottom:15px;">
                     <label>Capital Social:</label>
                     <input type="number" name="CapitalSocial" step="0.01">
                </div>
                
                <button type="submit" class="btn btn-primary">Gravar</button>
                <a href="clientes" class="btn btn-secondary">Cancelar</a>
            </form>
        </div>
    </div>

</body>
</html>
