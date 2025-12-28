    package manager;

    import java.sql.Connection;
    import java.sql.PreparedStatement;
    import java.sql.SQLException;
    import java.sql.ResultSet;
    import java.util.ArrayList;
    import java.util.List;
    import util.Configura;
    import clinica.Horario;
    import clinica.TipoServico;
    import veterinario.Veterinario;

    public class EscalonamentoDAO {

        // Helper class to represent a row in Escalonamento view
        public static class Escala {
            public int idHorario;
            public int idServico;
            public String nLicenca;
            public String dia; // Helper for display
            public String hora; // Helper
            public String servicoNome; // Helper
            public String vetNome; // Helper
        }

        public static int atribuir(int idHorario, int idServico, String nLicenca) {
            // Req 4.2: No overlap constraint
            // Fetch target horario details
            String sqlTarget = "SELECT DiaSemana, HoraInicio, HoraFim FROM Horario WHERE IDHorario = ?";
            String diaTarget = "";
            java.sql.Time inicioTarget = null;
            java.sql.Time fimTarget = null;
            
            try (Connection con = new Configura().getConnection();
                 PreparedStatement ps = con.prepareStatement(sqlTarget)) {
                ps.setInt(1, idHorario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        diaTarget = rs.getString("DiaSemana");
                        inicioTarget = rs.getTime("HoraInicio");
                        fimTarget = rs.getTime("HoraFim");
                    }
                }
            } catch (SQLException e) { return -1; }

            // Check if vet is already scaled for any overlapping period on the same day
            String sqlCheck = "SELECT COUNT(*) FROM Escalonamento e " +
                             "JOIN Horario h ON e.IDHorario = h.IDHorario " +
                             "WHERE e.NLicenca = ? AND h.DiaSemana = ? " +
                             "AND (h.HoraInicio < ? AND h.HoraFim > ?) " +
                             "AND (e.IDHorario <> ? OR e.IDServico <> ?)";
            
            try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setString(1, nLicenca);
                ps.setString(2, diaTarget);
                ps.setTime(3, fimTarget);
                ps.setTime(4, inicioTarget);
                ps.setInt(5, idHorario);
                ps.setInt(6, idServico);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.err.println("Erro: Veterinário já tem supervisão num período sobreposto.");
                        return -2; // Overlap error
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao validar sobreposição: " + e.getMessage());
            }

            String sqlDel = "DELETE FROM Escalonamento WHERE IDHorario=? AND IDServico=?";
            try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sqlDel)) {
                ps.setInt(1, idHorario);
                ps.setInt(2, idServico);
                ps.executeUpdate();
            } catch (SQLException e) {} // Ignore if empty
            
            String sqlIns = "INSERT INTO Escalonamento (IDHorario, IDServico, NLicenca) VALUES (?, ?, ?)";
            try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sqlIns)) {
                ps.setInt(1, idHorario);
                ps.setInt(2, idServico);
                ps.setString(3, nLicenca);
                return ps.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erro Escalonamento: " + e.getMessage());
            }
            return -1;
        }
        
        // Simple list for UI
        public static List<Escala> getAll() {
            List<Escala> list = new ArrayList<>();
            String sql = "SELECT e.*, h.DiaSemana, h.HoraInicio, ts.Nome as Servico, v.Nome as Vet " +
                        "FROM Escalonamento e " +
                        "JOIN Horario h ON e.IDHorario = h.IDHorario " +
                        "JOIN TipoServico ts ON e.IDServico = ts.IDServico " +
                        "JOIN Veterinario v ON e.NLicenca = v.NLicenca " +
                        "ORDER BY h.DiaSemana";
            try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Escala es = new Escala();
                    es.idHorario = rs.getInt("IDHorario");
                    es.idServico = rs.getInt("IDServico");
                    es.nLicenca = rs.getString("NLicenca");
                    es.dia = rs.getString("DiaSemana");
                    es.hora = rs.getTime("HoraInicio").toString();
                    es.servicoNome = rs.getString("Servico");
                    es.vetNome = rs.getString("Vet");
                    list.add(es);
                }
            } catch(SQLException e) { System.err.println("Erro List Escalonamento: " + e.getMessage()); }
            return list;
        }
    }