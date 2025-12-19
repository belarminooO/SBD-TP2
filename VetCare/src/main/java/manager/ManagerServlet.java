package manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import animal.Animal;
import util.DataTransfer;

@WebServlet("/manager")
public class ManagerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) action = "dashboard";

        if ("dashboard".equals(action)) {
            request.setAttribute("animaisVelhos", RelatorioDAO.getAnimaisExcedentes());
            request.setAttribute("tutoresObesos", RelatorioDAO.getTutoresAnimaisObesos());
            request.setAttribute("topCancelamentos", RelatorioDAO.getTutoresCancelamentos());
            request.setAttribute("agendaSemana", RelatorioDAO.getAgendaProximaSemana());
            request.getRequestDispatcher("manager/dashboard.jsp").forward(request, response);
        } else if ("horarios".equals(action)) {
            // Req 4.2: List current assignments and show form
            // Need generic lists of Horarios, Tipes, Vets
            request.setAttribute("listaEscalas", EscalonamentoDAO.getAll());
            // We need DAO methods to get all Horarios/Vets/Servicos. 
            // Assuming AgendamentoDAO/VeterinarioDAO have them or simple getters.
            request.setAttribute("listaVets", veterinario.VeterinarioDAO.getAll());
            request.setAttribute("listaTipos", agendamento.AgendamentoDAO.getTiposServico());
            // Need getAllHorarios - Assuming exists or mocking/adding to AgendamentoDAO if needed. 
            // Actually Horario table needs a DAO. Doing quick inline fetch or assuming AgendamentoDAO could carry it.
            // Let's rely on a helper or just not list empty slots for simplicity, 
            // OR strictly: we need to assign vets to *existing* slots. 
            // I'll add a helper to AgendamentoDAO for getAllHorarios quickly if not there.
            request.setAttribute("listaHorarios", agendamento.AgendamentoDAO.getAllHorarios()); 
            request.getRequestDispatcher("manager/horarios.jsp").forward(request, response);
        } else if ("xml".equals(action)) {
            exportFullProfile(request, response, "xml");
        } else if ("json".equals(action)) {
            exportFullProfile(request, response, "json");
        } else if ("import".equals(action)) {
             String xmlContent = request.getParameter("xmlData");
             if (xmlContent != null) {
                 DataTransfer.importAnimalFullProfileXml(new java.io.ByteArrayInputStream(xmlContent.getBytes()));
                 response.sendRedirect("manager?msg=Importado+com+sucesso");
             } else {
                 response.sendRedirect("manager?msg=Erro+na+importacao");
             }
        }
    }

    private void exportFullProfile(HttpServletRequest request, HttpServletResponse response, String format) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) return;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("atribuir".equals(action)) {
            int fh = Integer.parseInt(request.getParameter("IDHorario"));
            int fs = Integer.parseInt(request.getParameter("IDServico"));
            String lic = request.getParameter("NLicenca");
            EscalonamentoDAO.atribuir(fh, fs, lic);
            response.sendRedirect("manager?p=horarios");
        }
    }
}
