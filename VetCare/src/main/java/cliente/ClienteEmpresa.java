package cliente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Representa um cliente do tipo pessoa coletiva (empresa, abrigo ou
 * associação).
 * Estende a classe base Cliente e introduz atributos específicos para entidades
 * jurídicas, como o capital social.
 */
public class ClienteEmpresa extends Cliente {

    /** Capital social da entidade coletiva. */
    private BigDecimal capitalSocial;

    /**
     * Inicializa um novo cliente do tipo empresa.
     */
    public ClienteEmpresa() {
        this.tipoCliente = "Empresa";
    }

    /**
     * Inicializa uma empresa a partir de um conjunto de resultados da base de
     * dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public ClienteEmpresa(ResultSet rs) throws SQLException {
        super(rs);
        this.capitalSocial = rs.getBigDecimal("CapitalSocial");
    }

    /**
     * Inicializa uma empresa com base nos parâmetros de um pedido HTTP.
     * 
     * @param request Pedido HTTP contendo os dados do formulário.
     */
    public ClienteEmpresa(HttpServletRequest request) {
        super(request);
        this.tipoCliente = "Empresa";
        String cs = request.getParameter("CapitalSocial");
        if (cs != null && !cs.isEmpty()) {
            try {
                this.capitalSocial = new BigDecimal(cs);
            } catch (NumberFormatException e) {
                this.capitalSocial = BigDecimal.ZERO;
            }
        }
    }

    // --- Getters e Setters ---

    /** @return O capital social da empresa. */
    public BigDecimal getCapitalSocial() {
        return capitalSocial;
    }

    /** @param capitalSocial O montante a atribuir ao capital social. */
    public void setCapitalSocial(BigDecimal capitalSocial) {
        this.capitalSocial = capitalSocial;
    }
}
