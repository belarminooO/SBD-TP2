package agendamento;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
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
        
        String idAnimal = request.getParameter("idAnimal");
        String idCliente = request.getParameter("idCliente");
        
        if (idAnimal != null) request.setAttribute("selectedAnimalId", Integer.parseInt(idAnimal));
        if (idCliente != null) request.setAttribute("selectedClienteNif", idCliente);

        request.getRequestDispatcher("agendamento/novo.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Agendamento a = new Agendamento();
        
        // 1. Basic Fields
        a.setMotivo(request.getParameter("Motivo"));
        String nif = request.getParameter("Cliente_NIF");
        a.setClienteNif(nif);
        
        String animalIdStr = request.getParameter("Animal_IDAnimal");
        if(animalIdStr != null && !animalIdStr.isEmpty()) {
            int animalId = Integer.parseInt(animalIdStr);
            Animal animal = AnimalDAO.getById(animalId);
            if (animal != null && !animal.getClienteNif().equals(nif)) {
                response.sendRedirect("agendamentos?error=invalid_tutor");
                return;
            }
            a.setAnimalId(animalId);
        }
        
        a.setTipoServicoId(Integer.parseInt(request.getParameter("TipoServico")));
        a.setClinicaId(1); 

        // 2. Date Parsing and Validation
        String dataStr = request.getParameter("DataHoraInicio");
        if (dataStr != null && !dataStr.isEmpty()) {
            LocalDateTime ldt = LocalDateTime.parse(dataStr);
            LocalDate localDate = ldt.toLocalDate();
            
            // VALIDATION: Check for weekends and holidays
            if (util.Calendario.fimDeSemana(localDate) || !util.Calendario.getDescricaoCompleta(localDate).isEmpty()) {
                response.sendRedirect("agendamentos?p=new&error=weekend_holiday");
                return;
            }
            
            a.setDataHoraInicio(Timestamp.valueOf(ldt));
            
            // 3. Derived Fields (Horario)
            int derivedHorarioId = deriveHorarioId(ldt, a.getTipoServicoId(), a.getClinicaId());
            a.setHorarioId(derivedHorarioId); 
        }
        
        AgendamentoDAO.save(a);
        response.sendRedirect("agendamentos");
    }

    private int deriveHorarioId(LocalDateTime ldt, int serviceId, int clinicId) {
        String[] days = {"Domingo", "Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado"};
        String dayName = days[ldt.getDayOfWeek().getValue() % 7]; // Fix: getValue() is 1-7 (Mon-Sun)
        // Actually java.time (1=Mon, 7=Sun). My ENUM is Segunda, Terca...
        // Let's mapping:
        switch(ldt.getDayOfWeek()) {
            case MONDAY: dayName = "Segunda"; break;
            case TUESDAY: dayName = "Terca"; break;
            case WEDNESDAY: dayName = "Quarta"; break;
            case THURSDAY: dayName = "Quinta"; break;
            case FRIDAY: dayName = "Sexta"; break;
            default: dayName = "Weekend";
        }
        
        String timeStr = ldt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        // Find Horario that starts at or before this time and ends after it
        String sql = "SELECT IDHorario FROM Horario WHERE Clinica_IDClinica = ? AND DiaSemana = ? AND ? >= HoraInicio AND ? < HoraFim";
        try (java.sql.Connection con = new util.Configura().getConnection();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setString(2, dayName);
            ps.setString(3, timeStr);
            ps.setString(4, timeStr);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("IDHorario");
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
        return 1; // Fallback to ID 1 if not found
    }
}
