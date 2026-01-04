package manager;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.DataTransfer;

/**
 * Controlador central para as operações de gestão administrativa da clínica.
 * Gere a apresentação do dashboard de indicadores, a escala de profissionais
 * e as operações de intercâmbio de dados de pacientes.
 */
@WebServlet("/manager")
public class ManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processa pedidos GET para visualização do dashboard, gestão de horários e
     * exportação de dados.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     * @throws ServletException Em caso de erro no processamento do servlet.
     * @throws IOException      Em caso de erro de entrada/saída.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) {
            action = "dashboard";
        }

        if ("dashboard".equals(action)) {

            request.setAttribute("animaisVelhos", RelatorioDAO.getAnimaisExcedentes());
            request.setAttribute("tutoresObesos", RelatorioDAO.getTutoresAnimaisObesos());
            request.setAttribute("topCancelamentos", RelatorioDAO.getTutoresCancelamentos());
            request.setAttribute("agendaSemana", RelatorioDAO.getAgendaProximaSemana());
            request.getRequestDispatcher("manager/dashboard.jsp").forward(request, response);
        } else if ("horarios".equals(action)) {

            request.setAttribute("listaEscalas", EscalonamentoDAO.getAll());
            request.setAttribute("listaVets", veterinario.VeterinarioDAO.getAll());
            request.setAttribute("listaTipos", agendamento.AgendamentoDAO.getTiposServico());

            java.util.List<clinica.Clinica> todasClinicas = clinica.ClinicaDAO.getAll();
            request.setAttribute("listaClinicas", todasClinicas);

            int selectedClinicaId = -1;
            String filterStr = request.getParameter("filterClinica");
            if (filterStr != null && !filterStr.isEmpty()) {
                try {
                    selectedClinicaId = Integer.parseInt(filterStr);
                } catch (Exception e) {
                }
            } else if (!todasClinicas.isEmpty()) {
                selectedClinicaId = todasClinicas.get(0).getIdClinica();
            }
            request.setAttribute("selectedClinicaId", selectedClinicaId);

            java.util.List<clinica.Horario> allHorarios = agendamento.AgendamentoDAO.getAllHorarios();
            java.util.List<clinica.Horario> filteredHorarios = new java.util.ArrayList<>();

            if (allHorarios != null) {
                for (clinica.Horario h : allHorarios) {
                    if (h.getClinicaId() == selectedClinicaId) {
                        filteredHorarios.add(h);
                    }
                }
            }
            request.setAttribute("listaHorarios", filteredHorarios);

            request.getRequestDispatcher("manager/horarios.jsp").forward(request, response);
        } else if ("xml".equals(action)) {
            exportFullProfile(request, response, "xml");
        } else if ("json".equals(action)) {
            exportFullProfile(request, response, "json");
        } else if ("import".equals(action)) {
            processImport(request, response);
        }
    }

    /**
     * Processa a importação de perfis de animais em formato XML ou JSON.
     * 
     * @param request  Pedido HTTP contendo os dados de importação.
     * @param response Resposta HTTP.
     * @throws IOException Em caso de erro de entrada/saída.
     */
    private void processImport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String xmlContent = request.getParameter("xmlData");
        String jsonContent = request.getParameter("jsonData");
        String msg = "Erro: Nenhum conteúdo fornecido.";

        if (xmlContent != null && !xmlContent.trim().isEmpty()) {
            msg = DataTransfer.importAnimalFullProfileXml(
                    new java.io.ByteArrayInputStream(xmlContent.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } else if (jsonContent != null && !jsonContent.trim().isEmpty()) {
            // Fix encoding for JSON parameter (force UTF-8)
            String fixedJson = new String(jsonContent.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1),
                    java.nio.charset.StandardCharsets.UTF_8);
            msg = DataTransfer.importAnimalFullProfileJson(fixedJson);
        }

        response.sendRedirect("manager?msg=" + java.net.URLEncoder.encode(msg, "UTF-8"));
    }

    /**
     * Executa a exportação do perfil completo de um animal no formato solicitado.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     * @param format   Formato de exportação ("xml" ou "json").
     * @throws IOException Em caso de erro de escrita na resposta.
     */
    private void exportFullProfile(HttpServletRequest request, HttpServletResponse response, String format)
            throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            return;
        }
        int animalId = Integer.parseInt(idStr);

        if ("xml".equals(format)) {
            response.setContentType("text/xml");
            response.setHeader("Content-Disposition", "attachment; filename=\"animal_" + animalId + ".xml\"");
            DataTransfer.exportAnimalFullProfileXml(animalId, response.getWriter());
        } else {
            response.setContentType("application/json");
            response.setHeader("Content-Disposition", "attachment; filename=\"animal_" + animalId + ".json\"");
            DataTransfer.exportAnimalFullProfileJson(animalId, response.getWriter());
        }
    }

    /**
     * Processa pedidos POST para atualizações na escala de profissionais.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     * @throws ServletException Em caso de erro no processamento.
     * @throws IOException      Em caso de erro de entrada/saída.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("atribuir".equals(action)) {
            String idHorarioStr = request.getParameter("IDHorario");
            String idServicoStr = request.getParameter("IDServico");
            String nLicenca = request.getParameter("NLicenca");

            String oldIDHorarioStr = request.getParameter("oldIDHorario");
            String oldIDServicoStr = request.getParameter("oldIDServico");

            int result;
            if (oldIDHorarioStr != null && !oldIDHorarioStr.isEmpty() &&
                    oldIDServicoStr != null && !oldIDServicoStr.isEmpty()) {

                result = EscalonamentoDAO.update(
                        Integer.parseInt(oldIDHorarioStr),
                        Integer.parseInt(oldIDServicoStr),
                        Integer.parseInt(idHorarioStr),
                        Integer.parseInt(idServicoStr),
                        nLicenca);
            } else {
                result = EscalonamentoDAO.atribuir(
                        Integer.parseInt(idHorarioStr),
                        Integer.parseInt(idServicoStr),
                        nLicenca);
            }

            String msg = "Operação realizada com sucesso!";
            if (result == -2) {
                msg = "Aviso: Existe sobreposição de horários para este profissional.";
            } else if (result == -1) {
                msg = "Erro técnico ao gravar escala.";
            }

            response.sendRedirect("manager?p=horarios&msg=" + java.net.URLEncoder.encode(msg, "UTF-8")
                    + "&filterClinica=" + request.getParameter("filterClinica"));
        }
    }
}
