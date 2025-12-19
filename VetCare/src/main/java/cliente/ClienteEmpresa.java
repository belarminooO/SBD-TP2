package cliente;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;

public class ClienteEmpresa extends Cliente {
    private BigDecimal capitalSocial;

    public ClienteEmpresa() {
        this.tipoCliente = "Empresa";
    }

    public ClienteEmpresa(ResultSet rs) throws SQLException {
        super(rs);
        this.capitalSocial = rs.getBigDecimal("CapitalSocial");
    }
    
    public ClienteEmpresa(HttpServletRequest request) {
        super(request);
        this.tipoCliente = "Empresa";
        String cs = request.getParameter("CapitalSocial");
        if (cs != null && !cs.isEmpty()) {
            this.capitalSocial = new BigDecimal(cs);
        }
    }

    public BigDecimal getCapitalSocial() { return capitalSocial; }
    public void setCapitalSocial(BigDecimal capitalSocial) { this.capitalSocial = capitalSocial; }
}
