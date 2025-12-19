package clinica;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TipoServico {
    private Integer idServico;
    private String nome;

    public TipoServico() {}

    public TipoServico(Integer idServico, String nome) {
        this.idServico = idServico;
        this.nome = nome;
    }

    public TipoServico(ResultSet rs) throws SQLException {
        this.idServico = rs.getInt("IDServico");
        this.nome = rs.getString("Nome");
    }

    public Integer getIdServico() { return idServico; }
    public void setIdServico(Integer idServico) { this.idServico = idServico; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    @Override
    public String toString() { return nome; }
}
