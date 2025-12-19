package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Cirurgia extends PrestacaoServico {
    private String tipoCirurgia;
    private String notasPosOperatorias;

    public Cirurgia() {
        super();
        this.tipoDiscriminador = "Cirurgia";
    }

    public Cirurgia(ResultSet rs) throws SQLException {
        super(rs);
        this.tipoCirurgia = rs.getString("TipoCirurgia");
        this.notasPosOperatorias = rs.getString("NotasPosOperatorias");
    }

    public String getTipoCirurgia() { return tipoCirurgia; }
    public void setTipoCirurgia(String tipoCirurgia) { this.tipoCirurgia = tipoCirurgia; }

    public String getNotasPosOperatorias() { return notasPosOperatorias; }
    public void setNotasPosOperatorias(String notasPosOperatorias) { this.notasPosOperatorias = notasPosOperatorias; }
}
