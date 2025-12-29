package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma prestação de serviço do tipo resultado de exame.
 * Estende a classe base PrestacaoServico, focando-se no registo de diagnósticos
 * complementares e laboratoriais.
 */
public class ResultadoExame extends PrestacaoServico {

    /** Categoria ou tipo de exame realizado (ex: Hemograma, Raio-X). */
    private String tipoExame;

    /** Descrição detalhada dos resultados e achados técnicos do exame. */
    private String resultadoDetalhado;

    /**
     * Inicializa uma nova instância de resultado de exame definindo o discriminador
     * correspondente.
     */
    public ResultadoExame() {
        super();
        this.tipoDiscriminador = "ResultadoExame";
    }

    /**
     * Inicializa um resultado de exame a partir de um conjunto de resultados da
     * base de dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public ResultadoExame(ResultSet rs) throws SQLException {
        super(rs);
        this.tipoExame = rs.getString("TipoExame");
        this.resultadoDetalhado = rs.getString("ResultadoDetalhado");
    }

    /** @return O tipo de exame. */
    public String getTipoExame() {
        return tipoExame;
    }

    /** @param tipoExame O tipo de exame a atribuir. */
    public void setTipoExame(String tipoExame) {
        this.tipoExame = tipoExame;
    }

    /** @return O resultado detalhado. */
    public String getResultadoDetalhado() {
        return resultadoDetalhado;
    }

    /** @param resultadoDetalhado O resultado detalhado a atribuir. */
    public void setResultadoDetalhado(String resultadoDetalhado) {
        this.resultadoDetalhado = resultadoDetalhado;
    }
}
