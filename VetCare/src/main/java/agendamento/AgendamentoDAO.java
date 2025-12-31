package agendamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;
import clinica.TipoServico;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * Responsável pela persistência e gestão de agendamentos na base de dados.
 * Implementa validações de calendário para garantir a conformidade com o
 * período de funcionamento da clínica.
 */
public class AgendamentoDAO {

    /**
     * Recupera todos os agendamentos registados.
     * 
     * @return Lista de objetos Agendamento, ordenados por data decrescente.
     */
    public static List<Agendamento> getAll() {
        List<Agendamento> list = new ArrayList<>();
        String sql = "SELECT * FROM Agendamento ORDER BY DataHoraInicio DESC";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Agendamento(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar agendamentos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Recupera todos os horários configurados no sistema.
     * 
     * @return Lista de horários disponíveis.
     */
    public static List<clinica.Horario> getAllHorarios() {
        List<clinica.Horario> list = new ArrayList<>();
        // Added JOIN to get Clinic Name
        String sql = "SELECT h.*, c.Localidade FROM Horario h JOIN Clinica c ON h.Clinica_IDClinica = c.IDClinica ORDER BY c.Localidade, h.DiaSemana, h.HoraInicio";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                clinica.Horario h = new clinica.Horario(rs);
                h.setClinicaNome(rs.getString("Localidade")); // Need to add this setter to Horario
                list.add(h);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar horarios: " + e.getMessage());
        }
        return list;
    }

    /**
     * Armazena um novo agendamento, validando restrições de calendário.
     * 
     * @param a Objeto contendo os dados do agendamento.
     * @return Número de registos afetados ou -2 se a data for inválida.
     */
    public static int save(Agendamento a) {
        if (isWeekendOrHoliday(a.getDataHoraInicio())) {
            System.err.println("Erro: Data de agendamento coincide com período de encerramento.");
            return -2;
        }

        String sql = "INSERT INTO Agendamento (DataHoraInicio, Motivo, Cliente_NIF, Animal_IDAnimal, IDHorario, Clinica_IDClinica, TipoServico_IDServico) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, a.getDataHoraInicio());
            ps.setString(2, a.getMotivo());
            ps.setString(3, a.getClienteNif());

            if (a.getAnimalId() != null)
                ps.setInt(4, a.getAnimalId());
            else
                ps.setNull(4, java.sql.Types.INTEGER);

            ps.setInt(5, a.getHorarioId());
            ps.setInt(6, a.getClinicaId());
            ps.setInt(7, a.getTipoServicoId());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao criar agendamento: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Recupera a lista de tipos de serviço disponíveis.
     * 
     * @return Lista de serviços.
     */
    public static List<TipoServico> getTiposServico() {
        List<TipoServico> tipos = new ArrayList<>();
        String sql = "SELECT * FROM TipoServico";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                tipos.add(new TipoServico(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar tipos servico: " + e.getMessage());
        }
        return tipos;
    }

    /**
     * Recupera os agendamentos associados a um veterinário específico.
     * Realiza a junção entre agendamentos e escalas de serviço.
     * 
     * @param nLicenca Número da licença profissional do veterinário.
     * @return Lista de agendamentos filtrados.
     */
    public static List<Agendamento> getByVeterinario(String nLicenca) {
        List<Agendamento> list = new ArrayList<>();
        String sql = "SELECT a.* FROM Agendamento a " +
                "JOIN Escalonamento e ON a.IDHorario = e.IDHorario AND a.TipoServico_IDServico = e.IDServico " +
                "WHERE e.NLicenca = ? " +
                "ORDER BY a.DataHoraInicio ASC";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nLicenca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(new Agendamento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar agendamentos por veterinario: " + e.getMessage());
        }
        return list;
    }

    /**
     * Altera o estado de um agendamento.
     * 
     * @param id     Identificador do agendamento.
     * @param status Novo estado a aplicar.
     * @return Número de registos atualizados.
     */
    public static int updateStatus(int id, String status) {
        String sql = "UPDATE Agendamento SET Status = ? WHERE IDAgendamento = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status agendamento: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Obtém um agendamento através do seu identificador.
     * 
     * @param id Identificador único do agendamento.
     * @return Objeto Agendamento correspondente ou nulo se não existir.
     */
    public static Agendamento getById(int id) {
        String sql = "SELECT * FROM Agendamento WHERE IDAgendamento = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Agendamento(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler agendamento por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica se uma data corresponde a um fim de semana ou feriado nacional.
     * 
     * @param date Data a ser validada.
     * @return Verdadeiro se a data coincidir com um período de encerramento.
     */
    public static boolean isWeekendOrHoliday(Date date) {
        if (date == null)
            return false;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
            return true;

        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (month == Calendar.JANUARY && day == 1)
            return true; // Ano Novo
        if (month == Calendar.APRIL && day == 25)
            return true; // Dia da Liberdade
        if (month == Calendar.MAY && day == 1)
            return true; // Dia do Trabalhador
        if (month == Calendar.JUNE && day == 10)
            return true; // Dia de Portugal
        if (month == Calendar.AUGUST && day == 15)
            return true; // Assunção de Nossa Senhora
        if (month == Calendar.OCTOBER && day == 5)
            return true; // Implantação da República
        if (month == Calendar.NOVEMBER && day == 1)
            return true; // Todos os Santos
        if (month == Calendar.DECEMBER && day == 1)
            return true; // Restauração da Independência
        if (month == Calendar.DECEMBER && day == 8)
            return true; // Imaculada Conceição
        if (month == Calendar.DECEMBER && day == 25)
            return true; // Natal

        return false;
    }

    /**
     * Recupera agendamentos futuros confirmados para um animal.
     * 
     * @param animalId Identificador do animal.
     * @return Lista de mapas com dados do agendamento (Data, Motivo, Serviço).
     */
    public static List<Map<String, Object>> getFutureAppointments(int animalId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT a.DataHoraInicio, a.Motivo, ts.Nome as Servico " +
                "FROM Agendamento a " +
                "JOIN TipoServico ts ON a.TipoServico_IDServico = ts.IDServico " +
                "WHERE a.Animal_IDAnimal = ? " +
                "AND a.DataHoraInicio > NOW() " +
                "AND a.Status NOT IN ('Cancelado', 'Rejeitado') " +
                "ORDER BY a.DataHoraInicio ASC";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, animalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("DataHora", rs.getTimestamp("DataHoraInicio"));
                    map.put("Motivo", rs.getString("Motivo"));
                    map.put("Servico", rs.getString("Servico"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar agendamentos futuros: " + e.getMessage());
        }
        return list;
    }
}
