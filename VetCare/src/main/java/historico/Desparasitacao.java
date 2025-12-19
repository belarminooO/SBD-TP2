package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Desparasitacao extends PrestacaoServico {
    private String tipo; // Interna, Externa
    private String produtosUtilizados;

    public Desparasitacao() {
        super();
        this.tipoDiscriminador = "Desparasitacao";
    }

    public Desparasitacao(ResultSet rs) throws SQLException {
        super(rs);
        this.tipo = rs.getString("Tipo");
        this.produtosUtilizados = rs.getString("ProdutosUtilizados");
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getProdutosUtilizados() { return produtosUtilizados; }
    public void setProdutosUtilizados(String produtosUtilizados) { this.produtosUtilizados = produtosUtilizados; }
}
