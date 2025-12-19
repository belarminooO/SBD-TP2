package cliente;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

public class ClientePessoa extends Cliente {
    
    public ClientePessoa() {
        this.tipoCliente = "Pessoa";
    }

    public ClientePessoa(ResultSet rs) throws SQLException {
        super(rs);
        // Tabela ClientePessoa não tem colunas extra além do NIF, 
        // mas se tivesse, leria aqui.
    }
    
    public ClientePessoa(HttpServletRequest request) {
        super(request);
        this.tipoCliente = "Pessoa";
    }
}
