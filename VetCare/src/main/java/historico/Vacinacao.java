package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Vacinacao extends PrestacaoServico {
    private String tipoVacina;
    private String fabricante;

    public Vacinacao() {
        this.tipoDiscriminador = "Vacinacao";
    }

    public Vacinacao(ResultSet rs) throws SQLException {
        super(rs);
        try {
            this.tipoVacina = rs.getString("TipoVacina");
            this.fabricante = rs.getString("Fabricante");
        } catch(SQLException e) {}
    }

    public String getTipoVacina() { return tipoVacina; }
    public void setTipoVacina(String tipoVacina) { this.tipoVacina = tipoVacina; }

    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
}
