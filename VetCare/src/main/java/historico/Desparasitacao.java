package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma prestação de serviço do tipo desparasitação.
 * Estende a classe base PrestacaoServico, permitindo o registo de tratamentos
 * preventivos contra endoparasitas e ectoparasitas.
 */
public class Desparasitacao extends PrestacaoServico {

    /** Categoria do tratamento, tipicamente 'Interna' ou 'Externa'. */
    private String tipo;

    /** Nome comercial ou princípio ativo do desparasitante utilizado. */
    private String produtosUtilizados;

    /**
     * Inicializa uma nova instância de desparasitação definindo o discriminador
     * correspondente.
     */
    public Desparasitacao() {
        super();
        this.tipoDiscriminador = "Desparasitacao";
    }

    /**
     * Inicializa uma desparasitação a partir de um conjunto de resultados da base
     * de dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public Desparasitacao(ResultSet rs) throws SQLException {
        super(rs);
        this.tipo = rs.getString("Tipo");
        this.produtosUtilizados = rs.getString("ProdutosUtilizados");
    }

    /** @return O tipo de desparasitação. */
    public String getTipo() {
        return tipo;
    }

    /** @param tipo O tipo a atribuir. */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /** @return Os produtos utilizados. */
    public String getProdutosUtilizados() {
        return produtosUtilizados;
    }

    /** @param produtosUtilizados Os produtos a atribuir. */
    public void setProdutosUtilizados(String produtosUtilizados) {
        this.produtosUtilizados = produtosUtilizados;
    }
}
