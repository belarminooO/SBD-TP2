package veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

public class VeterinarioDAO {
    
    public static int save(Veterinario v) {
        if (v == null || !v.valid()) return -1;
        String sql = "INSERT INTO Veterinario (NLicenca, Nome) VALUES (?, ?)";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getNLicenca());
            ps.setString(2, v.getNome());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Veterinario: " + e.getMessage());
            return -1;
        }
    }

    public static Veterinario getByLicenca(String nLicenca) {
        String sql = "SELECT * FROM Veterinario WHERE NLicenca = ?";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nLicenca);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Veterinario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler Veterinario: " + e.getMessage());
        }
        return null;
    }

    public static List<Veterinario> getAll() {
        List<Veterinario> list = new ArrayList<>();
        String sql = "SELECT * FROM Veterinario ORDER BY Nome";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Veterinario(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar Veterinarios: " + e.getMessage());
        }
        return list;
    }

    public static int update(Veterinario v) {
        if (v == null || !v.valid()) return -1;
        String sql = "UPDATE Veterinario SET Nome = ? WHERE NLicenca = ?";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getNome());
            ps.setString(2, v.getNLicenca());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar Veterinario: " + e.getMessage());
            return -1;
        }
    }
}
