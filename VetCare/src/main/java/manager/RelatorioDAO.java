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
import cliente.Cliente;

public class RelatorioDAO {

    // Req 4.5: Animais que ultrapassaram a expetativa de vida
    public static List<Animal> getAnimaisExcedentes() {
        List<Animal> list = new ArrayList<>();
        // Logic: TIMESTAMPDIFF(YEAR, DataNascimento, CURDATE()) > Catalogo.ExpectativaVida
        String sql = "SELECT a.*, c.NomeComum as Catalogo_NomeComum FROM Animal a " +
                     "JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                     "WHERE TIMESTAMPDIFF(YEAR, a.DataNascimento, CURDATE()) > c.ExpectativaVida " +
                     "ORDER BY TIMESTAMPDIFF(YEAR, a.DataNascimento, CURDATE()) DESC";
        
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Animal(rs));
        } catch (SQLException e) {
            System.err.println("Erro Req 4.5: " + e.getMessage());
        }
        return list;
    }

    // Req 4.6: Tutores e quantidade de animais com excesso de peso (> PesoAdulto + 20% tolerance? Or just > ?)
    // Assuming > PesoAdulto for simplicity as per requirement wording implied.
    public static Map<String, Integer> getTutoresAnimaisObesos() {
        Map<String, Integer> map = new HashMap<>(); // Name -> Count
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
            System.err.println("Erro Req 4.6: " + e.getMessage());
        }
        return map;
    }

    // Req 4.7: Tutores com mais agendamentos cancelados no último trimestre
    public static List<String> getTutoresCancelamentos() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT cl.NomeCompleto, COUNT(*) as Qtd " +
                     "FROM Agendamento ag " +
                     "JOIN Cliente cl ON ag.Cliente_NIF = cl.NIF " +
                     "WHERE ag.Status = 'Cancelado' " +
                     "AND ag.DataHoraInicio >= DATE_SUB(NOW(), INTERVAL 3 MONTH) " +
                     "GROUP BY cl.NomeCompleto " +
                     "ORDER BY Qtd DESC " +
                     "LIMIT 10"; // Top 10
                     
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("NomeCompleto") + " (" + rs.getInt("Qtd") + ")");
            }
        } catch (SQLException e) {
            System.err.println("Erro Req 4.7: " + e.getMessage());
        }
        return list;
    }

    // Req 4.8: Agendamentos previstos para a próxima semana por serviço
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
            System.err.println("Erro Req 4.8: " + e.getMessage());
        }
        return map;
    }
}
