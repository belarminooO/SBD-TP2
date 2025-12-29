package cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

/**
 * Gere a persistência da hierarquia de clientes na base de dados.
 * Implementa a estratégia de herança por tabela com gestão de transações
 * manuais para assegurar a integridade relacional entre as entidades.
 */
public class ClienteDAO {

    /**
     * Persiste um cliente, efetuando a gravação ou atualização conforme necessário.
     * Utiliza transações para garantir atomicidade entre as tabelas base e
     * especializada.
     * 
     * @param c Objeto cliente a persistir.
     * @return Número de registos afetados no processo.
     */
    public static int save(Cliente c) {
        if (c == null || !c.valid())
            return -1;

        if (getByNif(c.getNif()) != null) {
            return update(c);
        }

        Connection con = null;
        PreparedStatement ps = null;
        int nRows = 0;

        try {
            con = new Configura().getConnection(false);

            String sqlCliente = "INSERT INTO Cliente (NIF, NomeCompleto, Contactos, Morada, Distrito, Concelho, Freguesia, PreferenciasLinguisticas, TipoCliente) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sqlCliente);
            ps.setString(1, c.getNif());
            ps.setString(2, c.getNomeCompleto());
            ps.setString(3, c.getContactos());
            ps.setString(4, c.getMorada());
            ps.setString(5, c.getDistrito());
            ps.setString(6, c.getConcelho());
            ps.setString(7, c.getFreguesia());
            ps.setString(8, c.getPreferenciasLinguisticas());
            ps.setString(9, c.getTipoCliente());

            nRows = ps.executeUpdate();
            ps.close();

            if (c instanceof ClientePessoa) {
                String sqlPessoa = "INSERT INTO ClientePessoa (NIF) VALUES (?)";
                ps = con.prepareStatement(sqlPessoa);
                ps.setString(1, c.getNif());
                ps.executeUpdate();
            } else if (c instanceof ClienteEmpresa) {
                String sqlEmpresa = "INSERT INTO ClienteEmpresa (NIF, CapitalSocial) VALUES (?, ?)";
                ps = con.prepareStatement(sqlEmpresa);
                ps.setString(1, c.getNif());
                ps.setBigDecimal(2, ((ClienteEmpresa) c).getCapitalSocial());
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            System.err.println("Erro ao gravar registo de cliente: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1;
        } finally {
            Configura.close(con);
        }
        return nRows;
    }

    /**
     * Atualiza os dados de um cliente existente.
     * Gere as alterações nas tabelas correspondentes de forma transacional.
     * 
     * @param c Objeto cliente com os novos dados.
     * @return Número de registos atualizados.
     */
    public static int update(Cliente c) {
        if (c == null || !c.valid())
            return -1;

        Connection con = null;
        PreparedStatement ps = null;
        int nRows = 0;

        try {
            con = new Configura().getConnection(false);

            String sqlCliente = "UPDATE Cliente SET NomeCompleto=?, Contactos=?, Morada=?, Distrito=?, Concelho=?, Freguesia=?, PreferenciasLinguisticas=? WHERE NIF=?";
            ps = con.prepareStatement(sqlCliente);
            ps.setString(1, c.getNomeCompleto());
            ps.setString(2, c.getContactos());
            ps.setString(3, c.getMorada());
            ps.setString(4, c.getDistrito());
            ps.setString(5, c.getConcelho());
            ps.setString(6, c.getFreguesia());
            ps.setString(7, c.getPreferenciasLinguisticas());
            ps.setString(8, c.getNif());
            nRows = ps.executeUpdate();
            ps.close();

            if (c instanceof ClienteEmpresa) {
                String sqlEmpresa = "UPDATE ClienteEmpresa SET CapitalSocial=? WHERE NIF=?";
                ps = con.prepareStatement(sqlEmpresa);
                ps.setBigDecimal(1, ((ClienteEmpresa) c).getCapitalSocial());
                ps.setString(2, c.getNif());
                ps.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar registo de cliente: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return -1;
        } finally {
            Configura.close(con);
        }
        return nRows;
    }

    /**
     * Recupera a listagem completa de clientes.
     * Utiliza junção para carregar atributos específicos consoante o tipo de
     * cliente.
     * 
     * @return Lista de objetos Cliente polimórficos.
     */
    public static List<Cliente> getAll() {
        List<Cliente> list = new ArrayList<>();
        String sql = "SELECT c.*, ce.CapitalSocial " +
                "FROM Cliente c " +
                "LEFT JOIN ClienteEmpresa ce ON c.NIF = ce.NIF " +
                "ORDER BY c.NomeCompleto";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String tipo = rs.getString("TipoCliente");
                Cliente c = null;
                if ("Pessoa".equalsIgnoreCase(tipo)) {
                    c = new ClientePessoa(rs);
                } else if ("Empresa".equalsIgnoreCase(tipo)) {
                    c = new ClienteEmpresa(rs);
                }
                if (c != null)
                    list.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar registos de clientes: " + e.getMessage());
        }
        return list;
    }

    /**
     * Localiza um cliente através do seu NIF.
     * 
     * @param nif Número de Identificação Fiscal.
     * @return Objeto Cliente correspondente ou nulo se inexistente.
     */
    public static Cliente getByNif(String nif) {
        String sql = "SELECT c.*, ce.CapitalSocial " +
                "FROM Cliente c " +
                "LEFT JOIN ClienteEmpresa ce ON c.NIF = ce.NIF " +
                "WHERE c.NIF = ?";
        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nif);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String tipo = rs.getString("TipoCliente");
                    if ("Pessoa".equalsIgnoreCase(tipo)) {
                        return new ClientePessoa(rs);
                    } else if ("Empresa".equalsIgnoreCase(tipo)) {
                        return new ClienteEmpresa(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao recuperar registo de cliente por NIF: " + e.getMessage());
        }
        return null;
    }
}
