package animal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

/**
 * Responsável por todas as operações de persistência relacionadas à entidade
 * Animal.
 * Atua como intermediário entre o modelo de objetos Java e a base de dados
 * relacional, isolando a lógica de acesso a dados.
 */
public class AnimalDAO {
    private static String lastError = "";

    public static String getLastError() {
        return lastError;
    }

    /**
     * Efetua a gravação ou atualização de um registo de animal.
     * Determina automaticamente se deve realizar uma inserção ou uma
     * atualização com base na existência de um identificador válido.
     * 
     * @param a Objeto animal a ser persistido.
     * @return Número de registos afetados ou -1 em caso de erro.
     */
    public static int save(Animal a) {
        if (a == null || !a.valid())
            return -1;

        if (a.getIdAnimal() != null && a.getIdAnimal() > 0) {
            return update(a);
        }

        if (a.getNumeroTransponder() != null && !a.getNumeroTransponder().isEmpty()) {
            Animal existing = getByTransponder(a.getNumeroTransponder());
            if (existing != null) {

                return -2;
            }
        }

        Animal existing = getByNomeAndNif(a.getNome(), a.getClienteNif());
        if (existing != null) {

            return -2;
        }

        String sql = "INSERT INTO Animal (Nome, Raca, Sexo, DataNascimento, Filiacao, EstadoReprodutivo, Alergias, Cores, PesoAtual, CaracteristicasDistintivas, NumeroTransponder, Fotografia, Cliente_NIF, Catalogo_NomeComum) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

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
            ps.setBytes(12, a.getFotografia());
            ps.setString(13, a.getClienteNif());
            ps.setString(14, a.getCatalogoNomeComum());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.err.println("Erro ao gravar registo de Animal: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Atualiza os dados de um animal existente na base de dados.
     * 
     * @param a Objeto animal com os dados atualizados.
     * @return Número de registos modificados.
     */
    public static int update(Animal a) {
        if (a == null || !a.valid())
            return -1;

        String sql = "UPDATE Animal SET Nome=?, Raca=?, Sexo=?, DataNascimento=?, Filiacao=?, EstadoReprodutivo=?, Alergias=?, Cores=?, PesoAtual=?, CaracteristicasDistintivas=?, NumeroTransponder=?, Fotografia=?, Cliente_NIF=?, Catalogo_NomeComum=? WHERE IDAnimal=?";

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
            ps.setBytes(12, a.getFotografia());
            ps.setString(13, a.getClienteNif());
            ps.setString(14, a.getCatalogoNomeComum());
            ps.setInt(15, a.getIdAnimal());

            return ps.executeUpdate();
        } catch (SQLException e) {
            lastError = e.getMessage();
            System.err.println("Erro ao atualizar registo de Animal: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Recupera a lista completa de animais registados.
     * Realiza a junção com o catálogo biológico para obter dados de longevidade.
     * 
     * @return Lista de objetos Animal.
     */
    public static List<Animal> getAll() {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT a.*, c.ExpectativaVida FROM Animal a " +
                "LEFT JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                "ORDER BY a.Nome";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Animal(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar animais: " + e.getMessage());
        }
        return list;
    }

    /**
     * Recupera as espécies configuradas no catálogo biológico de referência.
     * 
     * @return Lista de objetos descrevendo as espécies.
     */
    public static List<Catalogo> getEspecies() {
        List<Catalogo> list = new ArrayList<>();
        String sql = "SELECT * FROM Catalogo ORDER BY NomeComum";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Catalogo c = new Catalogo();
                c.setNomeComum(rs.getString("NomeComum"));
                c.setNomeCientifico(rs.getString("NomeCientifico"));
                c.setExpectativaVida(rs.getInt("ExpectativaVida"));
                c.setPesoAdulto(rs.getBigDecimal("PesoAdulto"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar catálogo de espécies: " + e.getMessage());
        }
        return list;
    }

    /**
     * Obtém um animal específico através do seu identificador único.
     * 
     * @param id Identificador do animal.
     * @return Objeto Animal ou nulo se não for encontrado.
     */
    public static Animal getById(int id) {
        String sql = "SELECT a.*, c.ExpectativaVida FROM Animal a " +
                "JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                "WHERE a.IDAnimal = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Animal(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao recuperar animal por identificador: " + e.getMessage());
        }
        return null;
    }

    /**
     * Pesquisa animais associados a um tutor através do nome deste.
     * 
     * @param tutorName Nome ou parte do nome do tutor.
     * @return Lista de animais encontrados.
     */
    public static List<Animal> searchByTutor(String tutorName) {
        List<Animal> list = new ArrayList<>();
        String sql = "SELECT a.*, cat.ExpectativaVida FROM Animal a " +
                "JOIN Cliente c ON a.Cliente_NIF = c.NIF " +
                "LEFT JOIN Catalogo cat ON a.Catalogo_NomeComum = cat.NomeComum " +
                "WHERE c.NomeCompleto LIKE ? ORDER BY a.Nome";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + tutorName + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(new Animal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro na pesquisa de animais por tutor: " + e.getMessage());
        }
        return list;
    }

    /**
     * Localiza um animal através do número do microchip.
     * 
     * @param transponder Código do transponder eletrónico.
     * @return Objeto Animal correspondente.
     */
    public static Animal getByTransponder(String transponder) {
        String sql = "SELECT a.*, c.ExpectativaVida FROM Animal a " +
                "JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                "WHERE a.NumeroTransponder = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, transponder);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Animal(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro na localização por transponder: " + e.getMessage());
        }
        return null;
    }

    /**
     * Localiza um animal através do par Nome + NIF do Tutor.
     * Útil para detetar animais sem chip registados anteriormente.
     * 
     * @param nome Nome do animal.
     * @param nif  NIF do tutor.
     * @return Objeto Animal ou nulo se não for encontrado.
     */
    public static Animal getByNomeAndNif(String nome, String nif) {
        String sql = "SELECT a.*, c.ExpectativaVida FROM Animal a " +
                "JOIN Catalogo c ON a.Catalogo_NomeComum = c.NomeComum " +
                "WHERE a.Nome = ? AND a.Cliente_NIF = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, nif);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Animal(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro na localização por nome+NIF: " + e.getMessage());
        }
        return null;
    }
}
