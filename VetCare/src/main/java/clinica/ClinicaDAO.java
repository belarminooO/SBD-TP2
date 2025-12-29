package clinica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

/**
 * Responsável pela persistência e gestão dos dados das unidades físicas
 * (clínicas).
 * Centraliza as operações de acesso à base de dados para a entidade Clinica.
 */
public class ClinicaDAO {

    /**
     * Regista uma nova unidade clínica no sistema.
     * 
     * @param c Objeto contendo os dados da clínica a persistir.
     * @return O número de linhas afetadas pela operação de inserção.
     */
    public static int save(Clinica c) {
        if (c == null || !c.valid())
            return -1;

        String sql = "INSERT INTO Clinica (Localidade, MoradaCompleta, CoordenadasGeograficas) VALUES (?, ?, ?)";
        int nRows = 0;

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, c.getLocalidade());
            ps.setString(2, c.getMoradaCompleta());
            ps.setString(3, c.getCoordenadasGeograficas());

            nRows = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir clínica: " + e.getMessage());
        }
        return nRows;
    }

    /**
     * Recupera os dados de uma clínica específica através do seu identificador.
     * 
     * @param id Identificador único da clínica.
     * @return Objeto Clinica preenchido ou nulo se não encontrado.
     */
    public static Clinica getById(int id) {
        String sql = "SELECT * FROM Clinica WHERE IDClinica = ?";
        Clinica c = null;

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Clinica(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter clínica por identificador: " + e.getMessage());
        }
        return c;
    }

    /**
     * Lista todas as clínicas registadas no sistema, ordenadas por localidade.
     * 
     * @return Lista de objetos Clinica.
     */
    public static List<Clinica> getAll() {
        List<Clinica> list = new ArrayList<>();
        String sql = "SELECT * FROM Clinica ORDER BY Localidade";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Clinica(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar clínicas: " + e.getMessage());
        }
        return list;
    }
}
