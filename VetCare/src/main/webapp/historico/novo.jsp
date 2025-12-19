<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="animal.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Novo Registo Clínico</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script>
function toggleType(val) {
    document.getElementById('divConsulta').style.display = (val === 'Consulta') ? 'block' : 'none';
    document.getElementById('divVacina').style.display = (val === 'Vacinacao') ? 'block' : 'none';
}
</script>
</head>
<body>

    <div class="container">
        <div class="card">
            <h2>Novo Registo Clínico</h2>
            
            <form action="historico" method="post">
                <div style="margin-bottom:15px;">
                    <label>Tipo:</label>
                    <select name="TipoDiscriminador" onchange="toggleType(this.value)">
                        <option value="Consulta">Consulta</option>
                        <option value="Vacinacao">Vacinação</option>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Animal:</label>
                    <select name="Animal_IDAnimal" required>
                        <% List<Animal> animais = (List<Animal>) request.getAttribute("listaAnimais");
                           if(animais!=null) for(Animal a : animais) { %>
                           <option value="<%= a.getIdAnimal() %>"><%= a.getNome() %></option>
                        <% } %>
                    </select>
                </div>
                
                <div style="margin-bottom:15px;">
                    <label>Detalhes Gerais:</label>
                    <textarea name="DetalhesGerais" rows="2" style="width:100%"></textarea>
                </div>
                
                <!-- Campos Consulta -->
                <div id="divConsulta">
                    <h3>Dados Consulta</h3>
                    <label>Sintomas:</label><input type="text" name="Sintomas" style="width:100%"><br>
                    <label>Diagnóstico:</label><input type="text" name="Diagnostico" style="width:100%"><br>
                    <label>Medicação:</label><input type="text" name="MedicacaoPrescrita" style="width:100%">
                </div>
                
                <!-- Campos Vacina -->
                <div id="divVacina" style="display:none;">
                    <h3>Dados Vacinação</h3>
                    <label>Tipo Vacina:</label><input type="text" name="TipoVacina"><br>
                    <label>Fabricante:</label><input type="text" name="Fabricante">
                </div>

                <br>
                <button type="submit" class="btn btn-primary">Gravar</button>
            </form>
        </div>
    </div>

</body>
</html>
