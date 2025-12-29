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
import cliente.ClienteDAO;

/**
 * Controlador responsável pela gestão do agendamento de serviços clínicos.
 * Gere os fluxos de marcação, cancelamento, rejeição e reagendamento,
 * assegurando a validação de disponibilidade e integridade dos dados.
 */
@WebServlet("/agendamentos")
public class AgendamentoServlet extends HttpServlet {
    /** Identificador de versão para serialização. */
    private static final long serialVersionUID = 1L;

    /**
     * Processa os pedidos GET para navegação e operações de agendamento.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null)
            action = "list";

        if ("new".equals(action)) {
            showNewForm(request, response);
        } else if ("cancel".equals(action)) {
            String id = request.getParameter("id");
            if (id != null)
                AgendamentoDAO.updateStatus(Integer.parseInt(id), "Cancelado");
            response.sendRedirect("agendamentos");
        } else if ("reject".equals(action)) {
            String id = request.getParameter("id");
            if (id != null)
                AgendamentoDAO.updateStatus(Integer.parseInt(id), "Rejeitado");
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

    /**
     * Prepara e exibe a lista de agendamentos.
     * Permite a filtragem por veterinário quando o respetivo parâmetro é fornecido.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     */
    private void listAgendamentos(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

    /**
     * Apresenta o formulário para criação ou edição de agendamentos.
     * Carrega as informações necessárias para o preenchimento dos seletores de
     * interface.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("listaAnimais", AnimalDAO.getAll());
        request.setAttribute("listaClientes", ClienteDAO.getAll());
        request.setAttribute("listaTipos", AgendamentoDAO.getTiposServico());

        String idAnimal = request.getParameter("idAnimal");
        String idCliente = request.getParameter("idCliente");

        if (idAnimal != null)
            request.setAttribute("selectedAnimalId", Integer.parseInt(idAnimal));
        if (idCliente != null)
            request.setAttribute("selectedClienteNif", idCliente);

        request.getRequestDispatcher("agendamento/novo.jsp").forward(request, response);
    }

    /**
     * Processa a submissão de dados de agendamento.
     * Efetua validações de consistência entre animal e tutor e restrições de
     * calendário.
     * 
     * @param request  Pedido HTTP com os dados do formulário.
     * @param response Resposta HTTP.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Agendamento a = new Agendamento();

        a.setMotivo(request.getParameter("Motivo"));
        String nif = request.getParameter("Cliente_NIF");
        a.setClienteNif(nif);

        String animalIdStr = request.getParameter("Animal_IDAnimal");
        if (animalIdStr != null && !animalIdStr.isEmpty()) {
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

        String dataStr = request.getParameter("DataHoraInicio");
        if (dataStr != null && !dataStr.isEmpty()) {
            LocalDateTime ldt = LocalDateTime.parse(dataStr);
            LocalDate localDate = ldt.toLocalDate();

            if (util.Calendario.fimDeSemana(localDate) || !util.Calendario.getDescricaoCompleta(localDate).isEmpty()) {
                response.sendRedirect("agendamentos?p=new&error=weekend_holiday");
                return;
            }

            a.setDataHoraInicio(Timestamp.valueOf(ldt));

            int derivedHorarioId = deriveHorarioId(ldt, a.getTipoServicoId(), a.getClinicaId());
            a.setHorarioId(derivedHorarioId);
        }

        AgendamentoDAO.save(a);
        response.sendRedirect("agendamentos");
    }

    /**
     * Identifica o Identificador do Horário correspondente à data e hora
     * selecionadas.
     * 
     * @param ldt       Data e hora em processamento.
     * @param serviceId Identificador do tipo de serviço.
     * @param clinicId  Identificador da clínica.
     * @return Identificador do horário encontrado ou valor por omissão (1).
     */
    private int deriveHorarioId(LocalDateTime ldt, int serviceId, int clinicId) {
        String dayName = "";
        switch (ldt.getDayOfWeek()) {
            case MONDAY:
                dayName = "Segunda";
                break;
            case TUESDAY:
                dayName = "Terca";
                break;
            case WEDNESDAY:
                dayName = "Quarta";
                break;
            case THURSDAY:
                dayName = "Quinta";
                break;
            case FRIDAY:
                dayName = "Sexta";
                break;
            default:
                dayName = "Weekend";
        }

        String timeStr = ldt.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        String sql = "SELECT IDHorario FROM Horario WHERE Clinica_IDClinica = ? AND DiaSemana = ? AND ? >= HoraInicio AND ? < HoraFim";
        try (java.sql.Connection con = new util.Configura().getConnection();
                java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setString(2, dayName);
            ps.setString(3, timeStr);
            ps.setString(4, timeStr);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt("IDHorario");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
