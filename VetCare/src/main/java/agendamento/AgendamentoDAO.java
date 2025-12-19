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

public class AgendamentoDAO {
    
    public static List<Agendamento> getAll() {
        List<Agendamento> list = new ArrayList<>();
        String sql = "SELECT * FROM Agendamento ORDER BY DataHoraInicio DESC";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Agendamento(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar agendamentos: " + e.getMessage());
        }
        return list;
    }
    
    public static List<clinica.Horario> getAllHorarios() {
        List<clinica.Horario> list = new ArrayList<>();
        String sql = "SELECT * FROM Horario ORDER BY DiaSemana, HoraInicio";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new clinica.Horario(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar horarios: " + e.getMessage());
        }
        return list;
    }

    public static int save(Agendamento a) {
        if (isWeekendOrHoliday(a.getDataHoraInicio())) {
            System.err.println("Erro: Tentativa de agendamento em fim de semana ou feriado.");
            return -2;
        }
        String sql = "INSERT INTO Agendamento (DataHoraInicio, Motivo, Cliente_NIF, Animal_IDAnimal, IDHorario, Clinica_IDClinica, TipoServico_IDServico) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // ...
        // Note: DataHoraFim is handled by Trigger usually, or we set it manually. Trigger expects it to be null or auto-calc? 
        // Trigger trg_agendamento_duracao updates DataHoraFim adds 30 min.
        
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, a.getDataHoraInicio());
            ps.setString(2, a.getMotivo());
            ps.setString(3, a.getClienteNif());
            if (a.getAnimalId() != null) ps.setInt(4, a.getAnimalId()); else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setInt(5, a.getHorarioId());
            ps.setInt(6, a.getClinicaId());
            ps.setInt(7, a.getTipoServicoId());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao criar agendamento: " + e.getMessage());
        }
        return -1;
    }
    
    public static List<TipoServico> getTiposServico() {
        List<Agendamento> list = new ArrayList<>(); // Typo in local variable type in previous context, fixing to correct type
        List<TipoServico> tipos = new ArrayList<>();
        String sql = "SELECT * FROM TipoServico";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) tipos.add(new TipoServico(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar tipos servico: " + e.getMessage());
        }
        return tipos;
    }

    public static List<Agendamento> getByVeterinario(String nLicenca) {
        List<Agendamento> list = new ArrayList<>();
        // Join with Escalonamento to find appointments under this vet's supervision
        // Note: Agendamento links to Horario. Escalonamento links Horario+Servico to Vet.
        String sql = "SELECT a.* FROM Agendamento a " +
                     "JOIN Escalonamento e ON a.IDHorario = e.IDHorario AND a.TipoServico_IDServico = e.IDServico " +
                     "WHERE e.NLicenca = ? " +
                     "ORDER BY a.DataHoraInicio ASC";
        
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nLicenca);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Agendamento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar agendamentos por veterinario: " + e.getMessage());
        }
        return list;
    }
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

    public static Agendamento getById(int id) {
        String sql = "SELECT * FROM Agendamento WHERE IDAgendamento = ?";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Agendamento(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler agendamento por ID: " + e.getMessage());
        }
        return null;
    }

    public static boolean isWeekendOrHoliday(Date date) {

        if (date == null) return false;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) return true;
        
        // Simple list of Portuguese national holidays (non-exhaustive)
        int month = cal.get(Calendar.MONTH); // 0-based
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        if (month == Calendar.JANUARY && day == 1) return true;      // Ano Novo
        if (month == Calendar.APRIL && day == 25) return true;      // Liberdade
        if (month == Calendar.MAY && day == 1) return true;         // Trabalhador
        if (month == Calendar.JUNE && day == 10) return true;       // Portugal
        if (month == Calendar.AUGUST && day == 15) return true;     // Assunção
        if (month == Calendar.OCTOBER && day == 5) return true;     // República
        if (month == Calendar.NOVEMBER && day == 1) return true;    // Todos os Santos
        if (month == Calendar.DECEMBER && day == 1) return true;    // Restauração
        if (month == Calendar.DECEMBER && day == 8) return true;    // Imaculada Conceição
        if (month == Calendar.DECEMBER && day == 25) return true;   // Natal
        
        return false;
    }
}
