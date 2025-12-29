<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%--
    Interface para criação de novos registos clínicos.
    Adapta o formulário dinamicamente consoante o tipo de serviço selecionado (Consulta, Cirurgia, etc.).
--%>
<%@ page import="java.util.List" %>
<%@ page import="animal.*" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>VetCare - Novo Registo Clínico</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<script>
/**
 * Altera os campos visíveis no formulário dependendo do serviço escolhido.
 * @param val O nome do tipo de discriminador (Consulta, Vacina, etc.)
 */
function toggleType(val) {
    const types = ['divConsulta', 'divVacina', 'divExameFisico', 'divResultadoExame', 'divDesparasitacao', 'divCirurgia', 'divTratamento'];

    types.forEach(t => {
        const el = document.getElementById(t);
        if (el) {
            el.style.display = 'none';
            const inputs = el.querySelectorAll('input, select, textarea');
            inputs.forEach(i => i.disabled = true);
        }
    });

    let target = '';
    if (val === 'Consulta') target = 'divConsulta';
    else if (val === 'Vacinacao') target = 'divVacina';
    else if (val === 'ExameFisico') target = 'divExameFisico';
    else if (val === 'ResultadoExame') target = 'divResultadoExame';
    else if (val === 'Desparasitacao') target = 'divDesparasitacao';
    else if (val === 'Cirurgia') target = 'divCirurgia';
    else if (val === 'TratamentoTerapeutico') target = 'divTratamento';

    if (target) {
        const targetEl = document.getElementById(target);
        if (targetEl) {
            targetEl.style.display = 'block';
            const inputs = targetEl.querySelectorAll('input, select, textarea');
            inputs.forEach(i => i.disabled = false);
        }
    }
}
</script>
</head>
<body onload="toggleType(document.getElementsByName('TipoDiscriminador')[0].value)">

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
            <h2>Novo Registo Clínico</h2>

            <form action="historico" method="post">
                <div style="margin-bottom:15px;">
                    <label>Tipo de Serviço:</label>
                    <select name="TipoDiscriminador" onchange="toggleType(this.value)" class="form-control">
                        <option value="Consulta">Consulta</option>
                        <option value="Vacinacao">Vacinação</option>
                        <option value="ExameFisico">Exame Físico</option>
                        <option value="ResultadoExame">Resultado de Exame</option>
                        <option value="Desparasitacao">Desparasitação</option>
                        <option value="Cirurgia">Cirurgia</option>
                        <option value="TratamentoTerapeutico">Tratamento Terapêutico</option>
                    </select>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Animal:</label>
                    <select name="Animal_IDAnimal" required class="form-control">
                        <%
                           Integer selectedId = (Integer) request.getAttribute("selectedAnimalId");
                           List<Animal> animais = (List<Animal>) request.getAttribute("listaAnimais");
                           if(animais!=null) for(Animal a : animais) {
                                boolean isSelected = (selectedId != null && selectedId.equals(a.getIdAnimal()));
                        %>
                           <option value="<%= a.getIdAnimal() %>" <%= isSelected ? "selected" : "" %>><%= a.getNome() %></option>
                        <% } %>
                    </select>
                </div>

                <div style="margin-bottom:15px;">
                    <label>Observações / Detalhes Gerais:</label>
                    <textarea name="DetalhesGerais" rows="2" style="width:100%" placeholder="Notas adicionais..."></textarea>
                </div>

                <hr>

                <!-- Campos Consulta -->
                <div id="divConsulta">
                    <h3>Dados da Consulta</h3>
                    <div style="margin-bottom:10px;">
                        <label>Motivo:</label>
                        <input type="text" name="Motivo" style="width:100%">
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Sintomas:</label>
                        <input type="text" name="Sintomas" style="width:100%">
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Diagnóstico:</label>
                        <input type="text" name="Diagnostico" style="width:100%">
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Medicação Prescrita:</label>
                        <input type="text" name="MedicacaoPrescrita" style="width:100%">
                    </div>
                </div>

                <!-- Campos Vacina -->
                <div id="divVacina" style="display:none;">
                    <h3>Dados da Vacinação</h3>
                    <div style="margin-bottom:10px;">
                        <label>Tipo Vacina:</label>
                        <input type="text" name="TipoVacina" style="width:100%">
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Fabricante:</label>
                        <input type="text" name="Fabricante" style="width:100%">
                    </div>
                </div>

                <!-- Campos Exame Fisico -->
                <div id="divExameFisico" style="display:none;">
                    <h3>Dados do Exame Físico</h3>
                    <div style="display:flex; gap:10px; margin-bottom:10px;">
                        <div style="flex:1">
                            <label>Temperatura (ºC):</label>
                            <input type="number" step="0.1" name="Temperatura" value="38.5" style="width:100%">
                        </div>
                        <div style="flex:1">
                            <label>Peso (kg):</label>
                            <input type="number" step="0.01" name="Peso" style="width:100%">
                        </div>
                    </div>
                    <div style="display:flex; gap:10px; margin-bottom:10px;">
                        <div style="flex:1">
                            <label>Freq. Cardíaca (bpm):</label>
                            <input type="number" name="FrequenciaCardiaca" style="width:100%">
                        </div>
                        <div style="flex:1">
                            <label>Freq. Respiratória (cpm):</label>
                            <input type="number" name="FrequenciaRespiratoria" style="width:100%">
                        </div>
                    </div>
                </div>

                <!-- Campos Resultado Exame -->
                <div id="divResultadoExame" style="display:none;">
                    <h3>Resultado de Exame</h3>
                    <div style="margin-bottom:10px;">
                        <label>Tipo de Exame:</label>
                        <input type="text" name="TipoExame" style="width:100%" placeholder="ex: Sangue, Raio-X">
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Resultado Detalhado:</label>
                        <textarea name="ResultadoDetalhado" style="width:100%; height:100px;"></textarea>
                    </div>
                </div>

                <!-- Campos Desparasitacao -->
                <div id="divDesparasitacao" style="display:none;">
                    <h3>Dados da Desparasitação</h3>
                    <div style="margin-bottom:10px;">
                        <label>Tipo:</label>
                        <select name="Tipo" style="width:100%">
                            <option value="Interna">Interna</option>
                            <option value="Externa">Externa</option>
                        </select>
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Produtos Utilizados:</label>
                        <input type="text" name="ProdutosUtilizados" style="width:100%">
                    </div>
                </div>

                <!-- Campos Cirurgia -->
                <div id="divCirurgia" style="display:none;">
                    <h3>Dados da Cirurgia</h3>
                    <div style="margin-bottom:10px;">
                        <label>Tipo de Cirurgia:</label>
                        <input type="text" name="TipoCirurgia" style="width:100%">
                    </div>
                    <div style="margin-bottom:10px;">
                        <label>Notas Pós-Operatórias:</label>
                        <textarea name="NotasPosOperatorias" style="width:100%; height:80px;"></textarea>
                    </div>
                </div>

                <!-- Campos Tratamento -->
                <div id="divTratamento" style="display:none;">
                    <h3>Tratamento Terapêutico</h3>
                    <div style="margin-bottom:10px;">
                        <label>Descrição do Tratamento:</label>
                        <textarea name="Descricao" style="width:100%; height:100px;"></textarea>
                    </div>
                </div>

                <br>
                <button type="submit" class="btn btn-primary">Gravar</button>
            </form>
        </div>
    </div>

</body>
</html>
