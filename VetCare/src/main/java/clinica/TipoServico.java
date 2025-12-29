package clinica;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma categoria de serviço clínico disponibilizado pela clínica.
 * Funciona como uma tabela de domínio para classificar intervenções como
 * consultas, cirurgias, vacinações ou urgências.
 */
public class TipoServico {

    /** Identificador único do tipo de serviço. */
    private Integer idServico;

    /** Nome descritivo da categoria de serviço. */
    private String nome;

    /**
     * Construtor padrão da classe.
     */
    public TipoServico() {
    }

    /**
     * Inicializa uma nova categoria de serviço com dados específicos.
     * 
     * @param idServico Identificador único.
     * @param nome      Nome da categoria.
     */
    public TipoServico(Integer idServico, String nome) {
        this.idServico = idServico;
        this.nome = nome;
    }

    /**
     * Inicializa um tipo de serviço a partir de um conjunto de resultados da base
     * de dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public TipoServico(ResultSet rs) throws SQLException {
        this.idServico = rs.getInt("IDServico");
        this.nome = rs.getString("Nome");
    }

    /** @return O identificador do serviço. */
    public Integer getIdServico() {
        return idServico;
    }

    /** @param idServico O identificador a atribuir. */
    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    /** @return O nome da categoria de serviço. */
    public String getNome() {
        return nome;
    }

    /** @param nome O nome a atribuir. */
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
