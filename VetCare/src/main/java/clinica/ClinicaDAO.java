package clinica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

public class ClinicaDAO {

    public static int save(Clinica c) {
        if (c == null || !c.valid()) return -1;
        
        String sql = "INSERT INTO Clinica (Localidade, MoradaCompleta, CoordenadasGeograficas) VALUES (?, ?, ?)";
        int nRows = 0;
        
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, c.getLocalidade());
            ps.setString(2, c.getMoradaCompleta());
            ps.setString(3, c.getCoordenadasGeograficas());
            
            nRows = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao inserir Clinica: " + e.getMessage());
        }
        return nRows;
    }

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
            System.err.println("Erro ao obter Clinica por ID: " + e.getMessage());
        }
        return c;
    }

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
            System.err.println("Erro ao listar Clinicas: " + e.getMessage());
        }
        return list;
    }
}
