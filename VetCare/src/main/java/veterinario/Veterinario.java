package veterinario;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

public class Veterinario {
    private String nLicenca = null;
    private String nome = null;

    public Veterinario() {}
    
    public Veterinario(String nLicenca, String nome) {
        this.nLicenca = nLicenca;
        this.nome = nome;
    }

    public Veterinario(ResultSet rs) {
        try {
            this.nLicenca = rs.getString("NLicenca");
            this.nome = rs.getString("Nome");
        } catch (SQLException e) {
            System.err.println("Erro ao ler Veterinario: " + e.getMessage());
        }
    }

    public Veterinario(HttpServletRequest request) {
        this.nLicenca = request.getParameter("NLicenca");
        this.nome = request.getParameter("Nome");
    }

    public String getNLicenca() { return nLicenca; }
    public void setNLicenca(String nLicenca) { this.nLicenca = nLicenca; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public boolean valid() { return nLicenca != null && !nLicenca.isEmpty() && nome != null && !nome.isEmpty(); }
    
    @Override
    public String toString() { return "Dr(a). " + nome + " (" + nLicenca + ")"; }
}
