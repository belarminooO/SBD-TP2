package agendamento;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import animal.Animal;
import animal.AnimalDAO;
import cliente.Cliente;
import cliente.ClienteDAO;
import clinica.TipoServico;
import util.DataFormatter;

@WebServlet("/agendamentos")
public class AgendamentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) action = "list";

        if ("new".equals(action)) {
            showNewForm(request, response);
        } else if ("cancel".equals(action)) {
            String id = request.getParameter("id");
            if (id != null) AgendamentoDAO.updateStatus(Integer.parseInt(id), "Cancelado");
            response.sendRedirect("agendamentos");
        } else if ("reject".equals(action)) {
            String id = request.getParameter("id");
            if (id != null) AgendamentoDAO.updateStatus(Integer.parseInt(id), "Rejeitado");
            response.sendRedirect("agendamentos");
        } else if ("resched".equals(action)) {
            String id = request.getParameter("id");
            if (id != null) {
                request.setAttribute("agendamento", AgendamentoDAO.getById(Integer.parseInt(id)));
            }
            showNewForm(request, response);
        } else {
            listAgendamentos(request, response);
        }
    }

    private void listAgendamentos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Supports filter by Vet if 'vet' param present (Req 2.4)
        String vetLicenca = request.getParameter("vet");
        List<Agendamento> list;
        if (vetLicenca != null) {
             list = AgendamentoDAO.getByVeterinario(vetLicenca);
        } else {
             list = AgendamentoDAO.getAll();
        }
        request.setAttribute("listaAgendamentos", list);
        request.getRequestDispatcher("agendamento/lista.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("listaAnimais", AnimalDAO.getAll());
        request.setAttribute("listaClientes", ClienteDAO.getAll());
        request.setAttribute("listaTipos", AgendamentoDAO.getTiposServico());
        // For Horario and Clinica, we might simply list generic ID options or hardcode if we haven't implemented full Horario queries
        // In a real app, date selection would trigger an AJAX fetch of available slots.
        // For this assignment, we might keep it simple: input ID manually or simple selects.
        
        request.getRequestDispatcher("agendamento/novo.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Agendamento a = new Agendamento();
        
        String dataStr = request.getParameter("DataHoraInicio"); // Expecting "yyyy-MM-ddTHH:mm" from datetime-local
        if (dataStr != null && !dataStr.isEmpty()) {
            LocalDateTime ldt = LocalDateTime.parse(dataStr);
            a.setDataHoraInicio(Timestamp.valueOf(ldt));
        }
        
        a.setMotivo(request.getParameter("Motivo"));
        a.setClienteNif(request.getParameter("Cliente_NIF"));
        String animalId = request.getParameter("Animal_IDAnimal");
        if(animalId!=null && !animalId.isEmpty()) a.setAnimalId(Integer.parseInt(animalId));
        
        a.setTipoServicoId(Integer.parseInt(request.getParameter("TipoServico")));
        a.setClinicaId(1); // Default to clinic 1 for simplicity if not multi-clinic context
        a.setHorarioId(1); // Default placeholder ID. Real logic involves finding the Horario ID that matches the day/time.
        
        AgendamentoDAO.save(a);
        response.sendRedirect("agendamentos");
    }
}
