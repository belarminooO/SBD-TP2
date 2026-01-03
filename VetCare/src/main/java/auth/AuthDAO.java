package auth;

import java.sql.*;
import util.Configura;

/**
 * Data Access Object para operações de autenticação e gestão de utilizadores.
 */
public class AuthDAO {
    
    /**
     * Autentica um utilizador com username e password.
     */
    public static Utilizador authenticate(String username, String password) {
        String sql = "SELECT * FROM Utilizador WHERE username = ? AND password = ?  AND ativo = TRUE";
        Configura config = new Configura();
        Connection conn = null;
        
        try {
            conn = config.getConnection();
            
            if (conn == null) {
                System.err.println("ERRO: Não foi possível estabelecer conexão à base de dados!");
                return null;
            }
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Utilizador user = new Utilizador(rs);
                updateLastLogin(user. getId());
                System.out.println("Login bem sucedido:  " + username);
                return user;
            } else {
                System.out.println("Credenciais inválidas para:  " + username);
            }
        } catch (SQLException e) {
            System.err.println("Erro na autenticação: " + e. getMessage());
            e.printStackTrace();
        } finally {
            Configura.close(conn);
        }
        return null;
    }
    
    /**
     * Atualiza a data/hora do último login. 
     */
    private static void updateLastLogin(int userId) {
        String sql = "UPDATE Utilizador SET ultimo_login = CURRENT_TIMESTAMP WHERE id = ? ";
        Configura config = new Configura();
        Connection conn = null;
        
        try {
            conn = config.getConnection();
            if (conn == null) return;
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar último login: " + e.getMessage());
        } finally {
            Configura.close(conn);
        }
    }
    
    /**
     * Obtém um utilizador pelo seu ID.
     */
    public static Utilizador getById(int id) {
        String sql = "SELECT * FROM Utilizador WHERE id = ?";
        Configura config = new Configura();
        Connection conn = null;
        
        try {
            conn = config.getConnection();
            if (conn == null) return null;
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new Utilizador(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao obter utilizador: " + e.getMessage());
        } finally {
            Configura.close(conn);
        }
        return null;
    }
}