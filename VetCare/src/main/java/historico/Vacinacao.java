package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma prestação de serviço do tipo vacinação.
 * Estende a classe base PrestacaoServico, focando-se no registo da
 * administração
 * de imunizantes e na respetiva rastreabilidade farmacêutica.
 */
public class Vacinacao extends PrestacaoServico {

    /** Nome ou tipo da vacina administrada (ex: Rábica, Polivalente). */
    private String tipoVacina;

    /** Laboratório ou fabricante responsável pela produção da vacina. */
    private String fabricante;

    /**
     * Inicializa uma nova instância de vacinação definindo o discriminador
     * correspondente.
     */
    public Vacinacao() {
        this.tipoDiscriminador = "Vacinacao";
    }

    /**
     * Inicializa uma vacinação a partir de um conjunto de resultados da base de
     * dados.
     * Tenta recuperar as colunas específicas, permitindo a criação do objeto
     * mesmo que apenas os dados base estejam presentes.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro grave no acesso à base de dados.
     */
    public Vacinacao(ResultSet rs) throws SQLException {
        super(rs);
        try {
            this.tipoVacina = rs.getString("TipoVacina");
            this.fabricante = rs.getString("Fabricante");
        } catch (SQLException e) {
            // Permite o funcionamento parcial em consultas que não incluam as tabelas
            // satélite.
        }
    }

    /** @return O tipo de vacina. */
    public String getTipoVacina() {
        return tipoVacina;
    }

    /** @param tipoVacina O tipo de vacina a atribuir. */
    public void setTipoVacina(String tipoVacina) {
        this.tipoVacina = tipoVacina;
    }

    /** @return O fabricante da vacina. */
    public String getFabricante() {
        return fabricante;
    }

    /** @param fabricante O fabricante a atribuir. */
    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }
}
