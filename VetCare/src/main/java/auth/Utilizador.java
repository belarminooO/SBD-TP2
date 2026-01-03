package auth;

import java.sql.ResultSet;
import java.sql. SQLException;
import java.sql. Timestamp;

/**
 * Entidade que representa um utilizador autenticado no sistema VetCare.
 */
public class Utilizador {
    
    private int id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private String nlicencaVeterinario;
    private String nifCliente;
    private boolean ativo;
    private Timestamp dataCriacao;
    private Timestamp ultimoLogin;
    
    public Utilizador() {}
    
    public Utilizador(ResultSet rs) throws SQLException {
        this. id = rs.getInt("id");
        this.username = rs.getString("username");
        this.password = rs.getString("password");
        this.email = rs.getString("email");
        this.role = Role.valueOf(rs.getString("role"));
        this.nlicencaVeterinario = rs.getString("nlicenca_veterinario");
        this.nifCliente = rs.getString("nif_cliente");
        this.ativo = rs.getBoolean("ativo");
        this.dataCriacao = rs.getTimestamp("data_criacao");
        this.ultimoLogin = rs.getTimestamp("ultimo_login");
    }
    
    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    
    public String getNlicencaVeterinario() { return nlicencaVeterinario; }
    public void setNlicencaVeterinario(String nlicencaVeterinario) { 
        this.nlicencaVeterinario = nlicencaVeterinario; 
    }
    
    public String getNifCliente() { return nifCliente; }
    public void setNifCliente(String nifCliente) { this.nifCliente = nifCliente; }
    
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    public Timestamp getDataCriacao() { return dataCriacao; }
    public Timestamp getUltimoLogin() { return ultimoLogin; }
    
    // Métodos de verificação de permissões
    public boolean isGerente() { return role == Role.GERENTE; }
    public boolean isVeterinario() { return role == Role. VETERINARIO; }
    public boolean isCliente() { return role == Role.CLIENTE; }
    
    public boolean podeEditarClientes() {
        return role == Role.GERENTE;
    }
    
    public boolean podeEditarAnimais() {
        return role == Role. GERENTE;
    }
    
    public boolean podeVerTodosClientes() {
        return role == Role.GERENTE || role == Role.VETERINARIO;
    }
    
    public boolean podeVerTodosAnimais() {
        return role == Role.GERENTE || role == Role.VETERINARIO;
    }
    
    public boolean podeAcederVeterinarios() {
        return role == Role.GERENTE;
    }
    
    public boolean podeAcederGestao() {
        return role == Role.GERENTE;
    }
}