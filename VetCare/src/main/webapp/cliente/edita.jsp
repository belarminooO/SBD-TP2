<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="cliente.*" %>
<%@ page import="auth.Utilizador" %>
<%@ page import="auth.Role" %>
<%
    Utilizador user = (Utilizador) session.getAttribute("utilizador");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    boolean isGerente = user.getRole() == Role.GERENTE;
    boolean podeEditar = user.podeEditarClientes();
    
    // Cliente só pode ver os seus próprios dados
    Cliente c = (Cliente) request.getAttribute("cliente");
    if (user.isCliente() && c != null && ! c.getNif().equals(user.getNifCliente())) {
        response. sendError(403, "Não tem permissão para ver os dados deste cliente.");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - <%= podeEditar ? "Editar" : "Ver" %> Cliente</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style. css">
<script>
function toggleEmpresa(val) {
    const div = document.getElementById('divCapital');
    const input = div.querySelector('input');
    if (val === 'Empresa') {
        div.style.display = 'block';
        input.setAttribute('required', 'required');
    } else {
        div. style.display = 'none';
        input.removeAttribute('required');
    }
}
</script>
</head>
<body onload="toggleEmpresa(document.getElementById('tipoSelector').value)">

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
            <h2><%= podeEditar ? "Ficha de Cliente" : "Dados do Cliente" %></h2>
            
            <% if (! podeEditar) { %>
                <div style="background:#ebf8ff; border:1px solid #4299e1; padding:10px; border-radius:5px; margin-bottom:15px;">
                    <small>ℹ️ Modo de consulta - não é possível editar dados. </small>
                </div>
            <% } %>
            
            <%
            String nif = (c != null) ? c.getNif() : "";
            String nome = (c != null) ? c.getNomeCompleto() : "";
            %>

            <form action="clientes" method="post">
                <p><small style="color:  red;">* Campos Obrigatórios</small></p>

                <div style="margin-bottom:15px;">
                    <label>Tipo de Cliente:  <span style="color: red;">*</span></label>
                    <select name="TipoCliente" id="tipoSelector" onchange="toggleEmpresa(this.value)" <%= ! podeEditar ?  "disabled" : "" %>>
                        <option value="Pessoa" <%= (c instanceof ClientePessoa) ? "selected" : "" %>>Pessoa</option>
                        <option value="Empresa" <%= (c instanceof ClienteEmpresa) ? "selected" : "" %>>Empresa</option>
                    </select>
                </div>

                <div style="margin-bottom:15px;">
                    <label>NIF: <span style="color: red;">*</span></label>
                    <input type="text" name="NIF" value="<%= nif %>" required pattern="\d{9}" title="O NIF deve ter 9 dígitos" maxlength="9" <%= !podeEditar ? "readonly" : "" %>>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Nome Completo: <span style="color:red;">*</span></label>
                    <input type="text" name="NomeCompleto" value="<%= nome %>" required style="width:100%" <%= !podeEditar ? "readonly" : "" %>>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Contactos: <span style="color: red;">*</span></label>
                    <input type="text" name="Contactos" value="<%= (c!=null && c.getContactos()!=null) ? c.getContactos() : "" %>" required style="width:100%" placeholder="Telefone ou Email" <%= !podeEditar ? "readonly" : "" %>>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Morada:  <span style="color:red;">*</span></label>
                    <input type="text" name="Morada" value="<%= (c!=null && c. getMorada()!=null) ? c.getMorada() : "" %>" required style="width:100%" <%= !podeEditar ? "readonly" : "" %>>
                </div>

                <div style="display:  flex; gap: 10px; margin-bottom:  15px;">
                    <div style="flex: 1;">
                        <label>Distrito: <span style="color:red;">*</span></label>
                        <input type="text" name="Distrito" value="<%= (c!=null && c. getDistrito()!=null) ? c.getDistrito() : "" %>" required style="width:100%" <%= !podeEditar ?  "readonly" : "" %>>
                    </div>
                    <div style="flex: 1;">
                        <label>Concelho (opcional):</label>
                        <input type="text" name="Concelho" value="<%= (c!=null && c.getConcelho()!=null) ? c.getConcelho() : "" %>" style="width:100%" <%= !podeEditar ? "readonly" : "" %>>
                    </div>
                    <div style="flex: 1;">
                        <label>Freguesia (opcional):</label>
                        <input type="text" name="Freguesia" value="<%= (c!=null && c.getFreguesia()!=null) ? c.getFreguesia() : "" %>" style="width:100%" <%= !podeEditar ? "readonly" : "" %>>
                    </div>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Preferências Linguísticas (opcional):</label>
                    <input type="text" name="PreferenciasLinguisticas" value="<%= (c!=null && c.getPreferenciasLinguisticas()!=null) ? c.getPreferenciasLinguisticas() : "" %>" style="width:100%" <%= ! podeEditar ? "readonly" : "" %>>
                </div>

                <div id="divCapital" style="display: none; margin-bottom:15px;">
                     <label>Capital Social: </label>
                     <input type="number" name="CapitalSocial" step="0.01" value="<%= (c instanceof ClienteEmpresa && ((ClienteEmpresa)c).getCapitalSocial()!=null) ? ((ClienteEmpresa)c).getCapitalSocial() : "" %>" <%= !podeEditar ? "readonly" : "" %>>
                </div>

                <% if (podeEditar) { %>
                    <button type="submit" class="btn btn-primary">Gravar</button>
                <% } %>
                <a href="clientes" class="btn btn-secondary">Voltar</a>
            </form>
        </div>
    </div>

</body>
</html>