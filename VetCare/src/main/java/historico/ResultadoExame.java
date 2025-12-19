package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultadoExame extends PrestacaoServico {
    private String tipoExame;
    private String resultadoDetalhado;

    public ResultadoExame() {
        super();
        this.tipoDiscriminador = "ResultadoExame";
    }

    public ResultadoExame(ResultSet rs) throws SQLException {
        super(rs);
        this.tipoExame = rs.getString("TipoExame");
        this.resultadoDetalhado = rs.getString("ResultadoDetalhado");
    }

    public String getTipoExame() { return tipoExame; }
    public void setTipoExame(String tipoExame) { this.tipoExame = tipoExame; }

    public String getResultadoDetalhado() { return resultadoDetalhado; }
    public void setResultadoDetalhado(String resultadoDetalhado) { this.resultadoDetalhado = resultadoDetalhado; }
}
