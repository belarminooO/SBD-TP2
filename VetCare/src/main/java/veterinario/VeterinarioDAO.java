package veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

/**
 * Responsável pela persistência e gestão dos dados do corpo clínico
 * veterinário.
 * Centraliza as operações de acesso à base de dados para a entidade
 * Veterinario,
 * permitindo o registo, consulta e atualização de perfis profissionais.
 */
public class VeterinarioDAO {

    /**
     * Regista um novo médico veterinário no sistema.
     * 
     * @param v Objeto contendo os dados do veterinário a persistir.
     * @return O número de linhas afetadas pela operação.
     */
    public static int save(Veterinario v) {
        if (v == null || !v.valid())
            return -1;
        String sql = "INSERT INTO Veterinario (NLicenca, Nome) VALUES (?, ?)";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getNLicenca());
            ps.setString(2, v.getNome());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao gravar veterinário: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Recupera os dados de um veterinário através da sua licença profissional.
     * 
     * @param nLicenca Número da licença profissional.
     * @return Objeto Veterinario preenchido ou nulo se não encontrado.
     */
    public static Veterinario getByLicenca(String nLicenca) {
        String sql = "SELECT * FROM Veterinario WHERE NLicenca = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nLicenca);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Veterinario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter veterinário: " + e.getMessage());
        }
        return null;
    }

    /**
     * Lista todos os médicos veterinários registados no sistema.
     * 
     * @return Lista de objetos Veterinario ordenados por nome.
     */
    public static List<Veterinario> getAll() {
        List<Veterinario> list = new ArrayList<>();
        String sql = "SELECT * FROM Veterinario ORDER BY Nome";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Veterinario(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar veterinários: " + e.getMessage());
        }
        return list;
    }

    /**
     * Atualiza os dados de um médico veterinário existente.
     * 
     * @param v Objeto contendo os dados atualizados.
     * @return O número de linhas afetadas pela operação.
     */
    public static int update(Veterinario v) {
        if (v == null || !v.valid())
            return -1;
        String sql = "UPDATE Veterinario SET Nome = ? WHERE NLicenca = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getNome());
            ps.setString(2, v.getNLicenca());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar veterinário: " + e.getMessage());
            return -1;
        }
    }
}
