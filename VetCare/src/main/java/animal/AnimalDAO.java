package animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

public class AnimalDAO {

    public static int save(Animal a) {
        if (a == null || !a.valid()) return -1;
        
        String sql = "INSERT INTO Animal (Nome, Raca, Sexo, DataNascimento, Filiacao, EstadoReprodutivo, Alergias, Cores, PesoAtual, CaracteristicasDistintivas, NumeroTransponder, Fotografia, Cliente_NIF, Catalogo_NomeComum) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, a.getNome());
            ps.setString(2, a.getRaca());
            ps.setString(3, a.getSexo());
            ps.setDate(4, a.getDataNascimento());
            ps.setString(5, a.getFiliacao());
            ps.setString(6, a.getEstadoReprodutivo());
            ps.setString(7, a.getAlergias());
            ps.setString(8, a.getCores());
            ps.setBigDecimal(9, a.getPesoAtual());
            ps.setString(10, a.getCaracteristicasDistintivas());
            ps.setString(11, a.getNumeroTransponder());
            ps.setString(12, a.getFotografia());
            ps.setString(13, a.getClienteNif());
            ps.setString(14, a.getCatalogoNomeComum());
            
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Animal: " + e.getMessage());
        }
        return -1;
    }

    public static List<Animal> getAll() {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT * FROM Animal ORDER BY Nome";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Animal(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar Animais: " + e.getMessage());
        }
        return list;
    }
    
    public static List<Catalogo> getEspecies() {
        List<Catalogo> list = new ArrayList<>();
        String sql = "SELECT * FROM Catalogo ORDER BY NomeComum";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Catalogo(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar especies: " + e.getMessage());
        }
        return list;
    }
    public static Animal getById(int id) {
        String sql = "SELECT a.*, c.ExpectativaVida FROM Animal a " +
                     "JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                     "WHERE a.IDAnimal = ?";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Animal a = new Animal(rs);
                    // We can set the expectation if we add the field
                    return a;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler Animal por ID: " + e.getMessage());
        }
        return null;
    }

    public static List<Animal> searchByTutor(String tutorName) {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT a.* FROM Animal a JOIN Cliente c ON a.Cliente_NIF = c.NIF WHERE c.NomeCompleto LIKE ? ORDER BY a.Nome";
        try (Connection con = new Configura().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + tutorName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Animal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar animais: " + e.getMessage());
        }
        return list;
    }
}
