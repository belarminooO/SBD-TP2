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
import clinica.ClinicaDAO;

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
        request.setAttribute("listaClinicas", ClinicaDAO.getAll());

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

        String clinicaIdStr = request.getParameter("ClinicaID");
        if (clinicaIdStr != null && !clinicaIdStr.isEmpty()) {
            a.setClinicaId(Integer.parseInt(clinicaIdStr));
        } else {
            a.setClinicaId(1);
        }

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

            if (derivedHorarioId == -1) {
                String dayName = getDayName(ldt.getDayOfWeek());
                String hours = getWorkingHours(a.getClinicaId(), dayName);
                String msg = "A clínica está fechada neste horário."
                        + (hours.isEmpty() ? "" : " Horário de funcionamento: " + hours);

                response.sendRedirect(
                        "agendamentos?p=new&error=clinic_closed&msg=" + java.net.URLEncoder.encode(msg, "UTF-8"));
                return;
            }

            a.setHorarioId(derivedHorarioId);
        }

        int result = AgendamentoDAO.save(a);
        if (result == -3) {
            response.sendRedirect("agendamentos?p=new&error=overlap_animal");
        } else if (result == -4) {
            response.sendRedirect("agendamentos?p=new&error=overlap_service");
        } else if (result == -2) {
            response.sendRedirect("agendamentos?p=new&error=closed");
        } else {
            response.sendRedirect("agendamentos");
        }
    }

    /**
     * Converte o dia da semana (enum) para o nome do dia em formato string.
     * Utilizado para mapeamento com a base de dados.
     * 
     * @param day Dia da semana.
     * @return Nome do dia (Ex: "Segunda", "Terca").
     */
    private String getDayName(java.time.DayOfWeek day) {
        if (day == java.time.DayOfWeek.MONDAY)
            return "Segunda";
        if (day == java.time.DayOfWeek.TUESDAY)
            return "Terca";
        if (day == java.time.DayOfWeek.WEDNESDAY)
            return "Quarta";
        if (day == java.time.DayOfWeek.THURSDAY)
            return "Quinta";
        if (day == java.time.DayOfWeek.FRIDAY)
            return "Sexta";
        if (day == java.time.DayOfWeek.SATURDAY)
            return "Sabado";
        if (day == java.time.DayOfWeek.SUNDAY)
            return "Domingo";
        return "";
    }

    /**
     * Obtém as horas de funcionamento de uma clínica para um dia específico.
     * 
     * @param clinicId Identificador da clínica.
     * @param dayName  Nome do dia da semana.
     * @return String formatada com os horários de funcionamento (Ex:
     *         "09:00-18:00").
     */
    private String getWorkingHours(int clinicId, String dayName) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT HoraInicio, HoraFim FROM Horario WHERE Clinica_IDClinica = ? AND DiaSemana = ? ORDER BY HoraInicio";
        try (java.sql.Connection con = new util.Configura().getConnection();
                java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, clinicId);
            ps.setString(2, dayName);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first)
                        sb.append(", ");
                    sb.append(rs.getTime("HoraInicio").toString().substring(0, 5))
                            .append("-")
                            .append(rs.getTime("HoraFim").toString().substring(0, 5));
                    first = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Identifica o Identificador do Horário correspondente à data e hora
     * selecionadas.
     * 
     * @param ldt       Data e hora em processamento.
     * @param serviceId Identificador do tipo de serviço.
     * @param clinicId  Identificador da clínica.
     * @return Identificador do horário encontrado ou -1 se fechado.
     */
    private int deriveHorarioId(LocalDateTime ldt, int serviceId, int clinicId) {
        String dayName = getDayName(ldt.getDayOfWeek());
        if (dayName.isEmpty())
            return -1;

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
        return -1;
    }
}
