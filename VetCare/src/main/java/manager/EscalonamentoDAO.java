package manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

/**
 * Responsável pela gestão dos turnos e alocação de profissionais veterinários.
 * Implementa lógicas de validação para prevenir sobreposições de horários,
 * garantindo a integridade do escalonamento de serviços.
 */
public class EscalonamentoDAO {

    /**
     * Atribui um veterinário a um determinado horário e serviço clínico.
     * Realiza uma verificação prévia de sobreposições para assegurar que o
     * profissional não está alocado a turnos simultâneos.
     * 
     * @param idHorario Identificador do horário pretendido.
     * @param idServico Identificador do serviço a prestar.
     * @param nLicenca  Número da licença do veterinário.
     * @return 1 se bem-sucedido, -2 se existir sobreposição, -1 em caso de erro
     *         técnico.
     */
    public static int atribuir(int idHorario, int idServico, String nLicenca) {
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
        } catch (SQLException e) {
            return -1;
        }

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
                    return -2;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Added debug
            System.err.println("Erro ao validar sobreposição: " + e.getMessage());
        }

        String sqlDel = "DELETE FROM Escalonamento WHERE IDHorario=? AND IDServico=?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sqlDel)) {
            ps.setInt(1, idHorario);
            ps.setInt(2, idServico);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }

        String sqlIns = "INSERT INTO Escalonamento (IDHorario, IDServico, NLicenca) VALUES (?, ?, ?)";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sqlIns)) {
            ps.setInt(1, idHorario);
            ps.setInt(2, idServico);
            ps.setString(3, nLicenca);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Added debug
            System.err.println("Erro ao gravar escalonamento: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Recupera a escala completa de alocações atuais para consulta visual.
     * 
     * @return Lista de objetos Escala contendo as informações consolidadas.
     */
    public static List<Escala> getAll() {
        List<Escala> list = new ArrayList<>();
        String sql = "SELECT e.*, h.DiaSemana, h.HoraInicio, c.Localidade, ts.Nome as Servico, v.Nome as Vet " +
                "FROM Escalonamento e " +
                "JOIN Horario h ON e.IDHorario = h.IDHorario " +
                "JOIN Clinica c ON h.Clinica_IDClinica = c.IDClinica " + // Added join
                "JOIN TipoServico ts ON e.IDServico = ts.IDServico " +
                "JOIN Veterinario v ON e.NLicenca = v.NLicenca " +
                "ORDER BY c.Localidade, h.DiaSemana"; // Ordered by clinic too
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Escala es = new Escala();
                es.idHorario = rs.getInt("IDHorario");
                es.idServico = rs.getInt("IDServico");
                es.nLicenca = rs.getString("NLicenca");
                es.dia = rs.getString("DiaSemana");
                es.hora = rs.getTime("HoraInicio").toString();
                es.clinica = rs.getString("Localidade");
                es.servicoNome = rs.getString("Servico");
                es.vetNome = rs.getString("Vet");
                list.add(es);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar escalonamento: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza uma atribuição de escala, garantindo integridade transacional.
     * Remove o registo antigo e insere o novo. Se houver conflito, reverte a
     * operação.
     * 
     * @param oldHorario ID do horário original.
     * @param oldServico ID do serviço original.
     * @param newHorario Novo ID de horário.
     * @param newServico Novo ID de serviço.
     * @param nLicenca   Licença do veterinário.
     * @return 1 (Sucesso), -2 (Sobreposição), -1 (Erro).
     */
    public static int update(int oldHorario, int oldServico, int newHorario, int newServico, String nLicenca) {
        Connection con = null;
        try {
            con = new Configura().getConnection();
            con.setAutoCommit(false);

            String sqlDel = "DELETE FROM Escalonamento WHERE IDHorario=? AND IDServico=?";
            try (PreparedStatement ps = con.prepareStatement(sqlDel)) {
                ps.setInt(1, oldHorario);
                ps.setInt(2, oldServico);
                ps.executeUpdate();
            }

            String sqlTarget = "SELECT DiaSemana, HoraInicio, HoraFim FROM Horario WHERE IDHorario = ?";
            String diaTarget = "";
            java.sql.Time inicioTarget = null;
            java.sql.Time fimTarget = null;

            try (PreparedStatement ps = con.prepareStatement(sqlTarget)) {
                ps.setInt(1, newHorario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        diaTarget = rs.getString("DiaSemana");
                        inicioTarget = rs.getTime("HoraInicio");
                        fimTarget = rs.getTime("HoraFim");
                    }
                }
            }

            String sqlCheck = "SELECT COUNT(*) FROM Escalonamento e " +
                    "JOIN Horario h ON e.IDHorario = h.IDHorario " +
                    "WHERE e.NLicenca = ? AND h.DiaSemana = ? " +
                    "AND (h.HoraInicio < ? AND h.HoraFim > ?) " +
                    "AND (e.IDHorario <> ? OR e.IDServico <> ?)";

            boolean conflict = false;
            try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setString(1, nLicenca);
                ps.setString(2, diaTarget);
                ps.setTime(3, fimTarget);
                ps.setTime(4, inicioTarget);
                ps.setInt(5, newHorario);
                ps.setInt(6, newServico);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0)
                        conflict = true;
                }
            }

            if (conflict) {
                con.rollback();
                return -2;
            }

            String sqlIns = "INSERT INTO Escalonamento (IDHorario, IDServico, NLicenca) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlIns)) {
                ps.setInt(1, newHorario);
                ps.setInt(2, newServico);
                ps.setString(3, nLicenca);
                ps.executeUpdate();
            }

            con.commit();
            return 1;

        } catch (SQLException e) {
            System.err.println("Erro no update de escala: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return -1;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
