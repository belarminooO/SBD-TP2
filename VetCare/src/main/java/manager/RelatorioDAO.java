package manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Configura;
import animal.Animal;

/**
 * Fornece métodos analíticos para a geração de relatórios estatísticos.
 * Transforma dados transacionais em informação de suporte à decisão para
 * a gestão da clínica.
 */
public class RelatorioDAO {

    /**
     * Identifica os animais cuja idade atual ultrapassa a expectativa de vida
     * definida no catálogo de espécies.
     * 
     * @return Lista de animais com idade superior à média da espécie.
     */
    public static List<Animal> getAnimaisExcedentes() {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT a.*, c.NomeComum as Catalogo_NomeComum FROM Animal a " +
                "JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                "WHERE TIMESTAMPDIFF(YEAR, a.DataNascimento, CURDATE()) > c.ExpectativaVida " +
                "ORDER BY TIMESTAMPDIFF(YEAR, a.DataNascimento, CURDATE()) DESC";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Animal(rs));
        } catch (SQLException e) {
            System.err.println("Erro na geração de relatório etário: " + e.getMessage());
        }
        return list;
    }

    /**
     * Identifica os tutores que possuem animais com peso superior ao peso de
     * referência para a fase adulta.
     * 
     * @return Mapa associando o nome do tutor ao número de animais com excesso de
     *         peso.
     */
    public static Map<String, Integer> getTutoresAnimaisObesos() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT cl.NomeCompleto, COUNT(*) as Qtd " +
                "FROM Animal a " +
                "JOIN Catalogo cap ON a.Catalogo_NomeComum = cap.NomeComum " +
                "JOIN Cliente cl ON a.Cliente_NIF = cl.NIF " +
                "WHERE a.PesoAtual > cap.PesoAdulto " +
                "GROUP BY cl.NomeCompleto " +
                "ORDER BY cl.NomeCompleto ASC";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("NomeCompleto"), rs.getInt("Qtd"));
            }
        } catch (SQLException e) {
            System.err.println("Erro na geração de relatório de obesidade: " + e.getMessage());
        }
        return map;
    }

    /**
     * Analisa a frequência de cancelamentos por tutor no período dos últimos
     * três meses.
     * 
     * @return Lista formatada com os nomes dos tutores e respetiva quantidade de
     *         cancelamentos.
     */
    public static List<String> getTutoresCancelamentos() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT cl.NomeCompleto, COUNT(*) as Qtd " +
                "FROM Agendamento ag " +
                "JOIN Cliente cl ON ag.Cliente_NIF = cl.NIF " +
                "WHERE ag.Status = 'Cancelado' " +
                "AND ag.DataHoraInicio >= DATE_SUB(NOW(), INTERVAL 3 MONTH) " +
                "GROUP BY cl.NomeCompleto " +
                "ORDER BY Qtd DESC " +
                "LIMIT 10";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("NomeCompleto") + " (" + rs.getInt("Qtd") + ")");
            }
        } catch (SQLException e) {
            System.err.println("Erro na geração de relatório de cancelamentos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Quantifica o volume de trabalho previsto para a próxima semana civil,
     * agrupando por tipo de serviço.
     * 
     * @return Mapa associando o tipo de serviço à quantidade de agendamentos
     *         previstos.
     */
    public static Map<String, Integer> getAgendaProximaSemana() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT ts.Nome, COUNT(*) as Qtd " +
                "FROM Agendamento a " +
                "JOIN TipoServico ts ON a.TipoServico_IDServico = ts.IDServico " +
                "WHERE a.DataHoraInicio BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
                "GROUP BY ts.Nome";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("Nome"), rs.getInt("Qtd"));
            }
        } catch (SQLException e) {
            System.err.println("Erro na geração de relatório de agenda: " + e.getMessage());
        }
        return map;
    }
}
