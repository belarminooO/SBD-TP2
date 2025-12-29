package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma prestação de serviço do tipo cirurgia.
 * Estende a classe base PrestacaoServico, incorporando detalhes específicos
 * sobre o procedimento operatório e recomendações pós-cirúrgicas.
 */
public class Cirurgia extends PrestacaoServico {

    /** Descrição técnica do procedimento cirúrgico realizado. */
    private String tipoCirurgia;

    /** Notas e recomendações críticas para a recuperação pós-operatória. */
    private String notasPosOperatorias;

    /**
     * Inicializa uma nova instância de cirurgia definindo o discriminador
     * correspondente.
     */
    public Cirurgia() {
        super();
        this.tipoDiscriminador = "Cirurgia";
    }

    /**
     * Inicializa uma cirurgia a partir de um conjunto de resultados da base de
     * dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public Cirurgia(ResultSet rs) throws SQLException {
        super(rs);
        this.tipoCirurgia = rs.getString("TipoCirurgia");
        this.notasPosOperatorias = rs.getString("NotasPosOperatorias");
    }

    /** @return O tipo de cirurgia. */
    public String getTipoCirurgia() {
        return tipoCirurgia;
    }

    /** @param tipoCirurgia O tipo de cirurgia a atribuir. */
    public void setTipoCirurgia(String tipoCirurgia) {
        this.tipoCirurgia = tipoCirurgia;
    }

    /** @return As notas pós-operatórias. */
    public String getNotasPosOperatorias() {
        return notasPosOperatorias;
    }

    /** @param notasPosOperatorias As notas a atribuir. */
    public void setNotasPosOperatorias(String notasPosOperatorias) {
        this.notasPosOperatorias = notasPosOperatorias;
    }
}
