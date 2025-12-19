package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TratamentoTerapeutico extends PrestacaoServico {
    private String descricao;

    public TratamentoTerapeutico() {
        super();
        this.tipoDiscriminador = "TratamentoTerapeutico";
    }

    public TratamentoTerapeutico(ResultSet rs) throws SQLException {
        super(rs);
        this.descricao = rs.getString("Descricao");
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
