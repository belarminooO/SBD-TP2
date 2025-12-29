<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Ficha de gestão de cliente (Pessoa ou Empresa).
    Para clientes empresariais, exige o preenchimento do Capital Social.
--%>
<%@ page import="cliente.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Editar Cliente</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script>
/** Alterna a visibilidade do campo Capital Social com base no tipo de cliente (Req 1.2) */
function toggleEmpresa(val) {
    const div = document.getElementById('divCapital');
    const input = div.querySelector('input');
    if (val === 'Empresa') {
        div.style.display = 'block';
        input.setAttribute('required', 'required');
    } else {
        div.style.display = 'none';
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
            <h2>Ficha de Cliente</h2>
            <%
            Cliente c = (Cliente) request.getAttribute("cliente");
            String nif = (c != null) ? c.getNif() : "";
            String nome = (c != null) ? c.getNomeCompleto() : "";
            %>

            <form action="clientes" method="post">
                <p><small style="color: red;">* Campos Obrigatórios</small></p>

                <div style="margin-bottom:15px;">
                    <label>Tipo de Cliente: <span style="color:red;">*</span></label>
                    <select name="TipoCliente" id="tipoSelector" onchange="toggleEmpresa(this.value)">
                        <option value="Pessoa" <%= (c instanceof ClientePessoa) ? "selected" : "" %>>Pessoa</option>
                        <option value="Empresa" <%= (c instanceof ClienteEmpresa) ? "selected" : "" %>>Empresa</option>
                    </select>
                </div>

                <div style="margin-bottom:15px;">
                    <label>NIF: <span style="color:red;">*</span></label>
                    <input type="text" name="NIF" value="<%= nif %>" required pattern="\d{9}" title="O NIF deve ter 9 dígitos" maxlength="9">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Nome Completo: <span style="color:red;">*</span></label>
                    <input type="text" name="NomeCompleto" value="<%= nome %>" required style="width:100%">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Contactos: <span style="color:red;">*</span></label>
                    <input type="text" name="Contactos" value="<%= (c!=null && c.getContactos()!=null) ? c.getContactos() : "" %>" required style="width:100%" placeholder="Telefone ou Email">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Morada: <span style="color:red;">*</span></label>
                    <input type="text" name="Morada" value="<%= (c!=null && c.getMorada()!=null) ? c.getMorada() : "" %>" required style="width:100%">
                </div>

                <div style="display: flex; gap: 10px; margin-bottom: 15px;">
                    <div style="flex: 1;">
                        <label>Distrito: <span style="color:red;">*</span></label>
                        <input type="text" name="Distrito" value="<%= (c!=null && c.getDistrito()!=null) ? c.getDistrito() : "" %>" required style="width:100%">
                    </div>
                    <div style="flex: 1;">
                        <label>Concelho (opcional):</label>
                        <input type="text" name="Concelho" value="<%= (c!=null && c.getConcelho()!=null) ? c.getConcelho() : "" %>" style="width:100%">
                    </div>
                    <div style="flex: 1;">
                        <label>Freguesia (opcional):</label>
                        <input type="text" name="Freguesia" value="<%= (c!=null && c.getFreguesia()!=null) ? c.getFreguesia() : "" %>" style="width:100%">
                    </div>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Preferências Linguísticas (opcional):</label>
                    <input type="text" name="PreferenciasLinguisticas" value="<%= (c!=null && c.getPreferenciasLinguisticas()!=null) ? c.getPreferenciasLinguisticas() : "" %>" style="width:100%">
                </div>

                <div id="divCapital" style="display:none; margin-bottom:15px;">
                     <label>Capital Social:</label>
                     <input type="number" name="CapitalSocial" step="0.01" value="<%= (c instanceof ClienteEmpresa && ((ClienteEmpresa)c).getCapitalSocial()!=null) ? ((ClienteEmpresa)c).getCapitalSocial() : "" %>">
                </div>

                <button type="submit" class="btn btn-primary">Gravar</button>
                <a href="clientes" class="btn btn-secondary">Cancelar</a>
            </form>
        </div>
    </div>

</body>
</html>
