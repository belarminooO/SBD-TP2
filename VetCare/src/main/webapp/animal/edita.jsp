<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Ficha de edição/criação de animal.
    Suporta a inserção de novos registos ou a atualização de existentes.
    Carrega listas de espécies e clientes para seleção.
--%>
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

            <%
            Animal a = (Animal) request.getAttribute("animal");
            %>
            <form action="animais" method="post">
                <%--  CHAVE PRIMÁRIA: Se estiver vazia, o Servlet interpreta como um 'INSERT' --%>
                <input type="hidden" name="IDAnimal" value="<%= (a!=null && a.getIdAnimal()!=null) ? a.getIdAnimal() : "" %>">

                <p><small style="color: red;">* Campos Obrigatórios</small></p>

                <div style="margin-bottom:15px;">
                    <label>Nome: <span style="color:red;">*</span></label>
                    <input type="text" name="Nome" required style="width:100%" value="<%= (a!=null && a.getNome()!=null) ? a.getNome() : "" %>">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Espécie: <span style="color:red;">*</span></label>
                    <select name="Catalogo_NomeComum" required style="width:100%">
                        <%
                        List<Catalogo> especies = (List<Catalogo>) request.getAttribute("listaEspecies");
                        if(especies!=null) for(Catalogo c : especies) {
                            boolean sel = (a!=null && c.getNomeComum().equals(a.getCatalogoNomeComum()));
                        %>
                        <option value="<%= c.getNomeComum() %>" <%= sel ? "selected" : "" %>><%= c.getNomeComum() %></option>
                        <% } %>
                    </select>
                </div>

                 <div style="margin-bottom:15px;">
                    <label>Raça: <span style="color:red;">*</span></label>
                    <input type="text" name="Raca" required style="width:100%" value="<%= (a!=null && a.getRaca()!=null) ? a.getRaca() : "" %>">
                </div>

                 <div style="margin-bottom:15px;">
                    <label>Sexo: <span style="color:red;">*</span></label>
                    <select name="Sexo">
                        <option value="M" <%= (a!=null && "M".equals(a.getSexo())) ? "selected" : "" %>>Macho</option>
                        <option value="F" <%= (a!=null && "F".equals(a.getSexo())) ? "selected" : "" %>>Fêmea</option>
                    </select>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Tutor: <span style="color:red;">*</span></label>
                    <select name="Cliente_NIF" required style="width:100%">
                        <%
                        List<Cliente> clientes = (List<Cliente>) request.getAttribute("listaClientes");
                        if(clientes!=null) for(Cliente cli : clientes) {
                            boolean sel = (a!=null && cli.getNif().equals(a.getClienteNif()));
                        %>
                        <option value="<%= cli.getNif() %>" <%= sel ? "selected" : "" %>><%= cli.getNomeCompleto() %> (<%= cli.getNif() %>)</option>
                        <% } %>
                    </select>
                </div>

                <div style="display: flex; gap: 10px; margin-bottom: 15px;">
                    <div style="flex: 1;">
                        <label>Data Nascimento: <span style="color:red;">*</span></label>
                        <input type="date" name="DataNascimento" required style="width:100%" value="<%= (a!=null && a.getDataNascimento()!=null) ? a.getDataNascimento().toString() : "" %>">
                    </div>
                    <div style="flex: 1;">
                        <label>Peso Atual (kg): <span style="color:red;">*</span></label>
                        <input type="number" step="0.01" name="PesoAtual" required style="width:100%" value="<%= (a!=null && a.getPesoAtual()!=null) ? a.getPesoAtual() : "" %>">
                    </div>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Filiação (Pai/Mãe):</label>
                    <input type="text" name="Filiacao" style="width:100%" value="<%= (a!=null && a.getFiliacao()!=null) ? a.getFiliacao() : "" %>">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Estado Reprodutivo:</label>
                    <input type="text" name="EstadoReprodutivo" style="width:100%" value="<%= (a!=null && a.getEstadoReprodutivo()!=null) ? a.getEstadoReprodutivo() : "" %>">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Alergias:</label>
                    <input type="text" name="Alergias" style="width:100%" value="<%= (a!=null && a.getAlergias()!=null) ? a.getAlergias() : "" %>">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Cores: <span style="color:red;">*</span></label>
                    <input type="text" name="Cores" style="width:100%" required value="<%= (a!=null && a.getCores()!=null) ? a.getCores() : "" %>">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Características Distintivas:</label>
                    <textarea name="CaracteristicasDistintivas" style="width:100%; height: 60px;"><%= (a!=null && a.getCaracteristicasDistintivas()!=null) ? a.getCaracteristicasDistintivas() : "" %></textarea>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Número Transponder (Microchip):</label>
                    <input type="text" name="NumeroTransponder" style="width:100%" value="<%= (a!=null && a.getNumeroTransponder()!=null) ? a.getNumeroTransponder() : "" %>">
                </div>

                <div style="margin-bottom:15px;">
                    <label>Fotografia (URL):</label>
                    <input type="text" name="Fotografia" placeholder="http://..." style="width:100%" value="<%= (a!=null && a.getFotografia()!=null) ? a.getFotografia() : "" %>">
                </div>

                <button type="submit" class="btn btn-primary">Gravar</button>
                <% if(a!=null && a.getIdAnimal()!=null) { %>
                    <a href="agendamentos?p=new&idAnimal=<%= a.getIdAnimal() %>&idCliente=<%= a.getClienteNif() %>" class="btn btn-info">Marcar Agendamento</a>
                <% } %>
                <a href="animais" class="btn btn-secondary">Cancelar</a>
            </form>
        </div>
    </div>

</body>
</html>
