package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa um protocolo de tratamento terapêutico contínuo.
 * Utilizada para registar intervenções que não se limitam a uma consulta
 * pontual, abrangendo planos de fisioterapia ou regimes posológicos
 * prolongados.
 */
public class TratamentoTerapeutico extends PrestacaoServico {

    /**
     * Descrição detalhada do plano de tratamento ou protocolo clínico.
     */
    private String descricao;

    /**
     * Construtor por omissão.
     * Inicializa o tipo discriminador como "TratamentoTerapeutico".
     */
    public TratamentoTerapeutico() {
        super();
        this.tipoDiscriminador = "TratamentoTerapeutico";
    }

    /**
     * Construtor que inicializa o objeto a partir de um registo da base de dados.
     * 
     * @param rs ResultSet posicionado no registo a ler.
     * @throws SQLException Caso ocorra um erro no acesso aos dados.
     */
    public TratamentoTerapeutico(ResultSet rs) throws SQLException {
        super(rs);
        this.descricao = rs.getString("Descricao");
    }

    /**
     * Obtém a descrição do tratamento.
     * 
     * @return String contendo os detalhes do tratamento.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Define a descrição do tratamento.
     * 
     * @param descricao Texto detalhado do plano terapêutico.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
