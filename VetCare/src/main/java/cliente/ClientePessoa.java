package cliente;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Representa um cliente do tipo pessoa singular (tutor individual).
 * Estende a classe base Cliente e define o discriminador de tipo como 'Pessoa'.
 */
public class ClientePessoa extends Cliente {

    /**
     * Inicializa um novo cliente do tipo pessoa singular.
     */
    public ClientePessoa() {
        this.tipoCliente = "Pessoa";
    }

    /**
     * Inicializa uma pessoa singular a partir de um conjunto de resultados da base
     * de dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public ClientePessoa(ResultSet rs) throws SQLException {
        super(rs);
    }

    /**
     * Inicializa uma pessoa singular com base nos parâmetros de um pedido HTTP.
     * 
     * @param request Pedido HTTP contendo os dados do formulário.
     */
    public ClientePessoa(HttpServletRequest request) {
        super(request);
        this.tipoCliente = "Pessoa";
    }
}
