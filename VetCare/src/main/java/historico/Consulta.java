package historico;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

public class Consulta extends PrestacaoServico {
    private String motivo;
    private String sintomas;
    private String diagnostico;
    private String medicacaoPrescrita;

    public Consulta() {
        this.tipoDiscriminador = "Consulta";
    }

    public Consulta(ResultSet rs) throws SQLException {
        super(rs);
        // Assuming joined query or separated fetch. If joined:
        try {
            this.motivo = rs.getString("Motivo");
            this.sintomas = rs.getString("Sintomas");
            this.diagnostico = rs.getString("Diagnostico");
            this.medicacaoPrescrita = rs.getString("MedicacaoPrescrita");
        } catch(SQLException e) {
            // Columns might not be present if not joined
        }
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getMedicacaoPrescrita() { return medicacaoPrescrita; }
    public void setMedicacaoPrescrita(String medicacaoPrescrita) { this.medicacaoPrescrita = medicacaoPrescrita; }
}
