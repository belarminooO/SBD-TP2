package agendamento;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;
import util.DataFormatter;

public class Agendamento {
    private Integer idAgendamento;
    private Timestamp dataHoraInicio;
    private Timestamp dataHoraFim;
    private String motivo;
    private String status;
    private BigDecimal custo;
    private String clienteNif;
    private Integer animalId;
    private Integer horarioId;
    private Integer clinicaId;
    private Integer tipoServicoId;

    public Agendamento() {}

    public Agendamento(ResultSet rs) throws SQLException {
        this.idAgendamento = rs.getInt("IDAgendamento");
        this.dataHoraInicio = rs.getTimestamp("DataHoraInicio");
        this.dataHoraFim = rs.getTimestamp("DataHoraFim");
        this.motivo = rs.getString("Motivo");
        this.status = rs.getString("Status");
        this.custo = rs.getBigDecimal("Custo");
        this.clienteNif = rs.getString("Cliente_NIF");
        this.animalId = rs.getInt("Animal_IDAnimal");
        if(rs.wasNull()) this.animalId = null;
        this.horarioId = rs.getInt("IDHorario");
        this.clinicaId = rs.getInt("Clinica_IDClinica");
        this.tipoServicoId = rs.getInt("TipoServico_IDServico");
    }
    
    public Agendamento(HttpServletRequest request) {
        // Parsing logic would go here
    }

    public Integer getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(Integer idAgendamento) { this.idAgendamento = idAgendamento; }

    public Timestamp getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(Timestamp dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }

    public Timestamp getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(Timestamp dataHoraFim) { this.dataHoraFim = dataHoraFim; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }

    public String getClienteNif() { return clienteNif; }
    public void setClienteNif(String clienteNif) { this.clienteNif = clienteNif; }

    public Integer getAnimalId() { return animalId; }
    public void setAnimalId(Integer animalId) { this.animalId = animalId; }

    public Integer getHorarioId() { return horarioId; }
    public void setHorarioId(Integer horarioId) { this.horarioId = horarioId; }

    public Integer getClinicaId() { return clinicaId; }
    public void setClinicaId(Integer clinicaId) { this.clinicaId = clinicaId; }

    public Integer getTipoServicoId() { return tipoServicoId; }
    public void setTipoServicoId(Integer tipoServicoId) { this.tipoServicoId = tipoServicoId; }
}
