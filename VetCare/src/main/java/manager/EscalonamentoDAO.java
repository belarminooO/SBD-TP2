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
            // Check if this vet is already supervising another service at this same time (idHorario)
            String sqlCheck = "SELECT COUNT(*) FROM Escalonamento WHERE IDHorario = ? AND NLicenca = ? AND IDServico <> ?";
            try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setInt(1, idHorario);
                ps.setString(2, nLicenca);
                ps.setInt(3, idServico);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.err.println("Erro: Veterinário já tem supervisão noutro serviço neste horário.");
                        return -2; // Overlap error
                    }
                }
            } catch (SQLException e) {}

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
