package agendamento;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Entidade representativa de um agendamento no sistema VetCare.
 * Centraliza as informações relativas ao compromisso clínico entre tutor,
 * animal e clínica, incluindo a gestão de tempos e estados do serviço.
 */
public class Agendamento {
    /** Identificador único do agendamento. */
    private Integer idAgendamento;
    /** Data e hora de início previstas para o serviço. */
    private Timestamp dataHoraInicio;
    /** Data e hora de fim previstas para o serviço. */
    private Timestamp dataHoraFim;
    /** Motivo ou descrição sumária da marcação. */
    private String motivo;
    /** Estado atual do agendamento (ex: Pendente, Confirmado, Cancelado). */
    private String status;
    /** Custo estimado ou real do serviço prestado. */
    private BigDecimal custo;
    /** NIF do cliente que efetuou a marcação. */
    private String clienteNif;
    /** Identificador do animal associado ao agendamento. */
    private Integer animalId;
    /** Identificador do horário (slot) atribuído. */
    private Integer horarioId;
    /** Identificador da clínica onde se realiza o serviço. */
    private Integer clinicaId;
    /** Identificador do tipo de serviço a prestar. */
    private Integer tipoServicoId;

    /**
     * Construtor por omissão.
     */
    public Agendamento() {
    }

    /**
     * Construtor que inicializa o objeto a partir de um registo da base de dados.
     * 
     * @param rs ResultSet posicionado no registo a ler.
     * @throws SQLException Caso ocorra um erro no acesso aos dados.
     */
    public Agendamento(ResultSet rs) throws SQLException {
        this.idAgendamento = rs.getInt("IDAgendamento");
        this.dataHoraInicio = rs.getTimestamp("DataHoraInicio");
        this.dataHoraFim = rs.getTimestamp("DataHoraFim");
        this.motivo = rs.getString("Motivo");
        this.status = rs.getString("Status");
        this.custo = rs.getBigDecimal("Custo");
        this.clienteNif = rs.getString("Cliente_NIF");
        this.animalId = rs.getInt("Animal_IDAnimal");
        if (rs.wasNull())
            this.animalId = null;
        this.horarioId = rs.getInt("IDHorario");
        this.clinicaId = rs.getInt("Clinica_IDClinica");
        this.tipoServicoId = rs.getInt("TipoServico_IDServico");
    }

    /**
     * Construtor para inicialização a partir de um pedido HTTP.
     * 
     * @param request Pedido contendo os parâmetros do agendamento.
     */
    public Agendamento(HttpServletRequest request) {
    }

    /** @return Identificador do agendamento. */
    public Integer getIdAgendamento() {
        return idAgendamento;
    }

    /** @param idAgendamento Define o identificador. */
    public void setIdAgendamento(Integer idAgendamento) {
        this.idAgendamento = idAgendamento;
    }

    /** @return Data de início. */
    public Timestamp getDataHoraInicio() {
        return dataHoraInicio;
    }

    /** @param dataHoraInicio Define a data de início. */
    public void setDataHoraInicio(Timestamp dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
    }

    /** @return Data de fim. */
    public Timestamp getDataHoraFim() {
        return dataHoraFim;
    }

    /** @param dataHoraFim Define a data de fim. */
    public void setDataHoraFim(Timestamp dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
    }

    /** @return Motivo da marcação. */
    public String getMotivo() {
        return motivo;
    }

    /** @param motivo Define o motivo. */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /** @return Estado do agendamento. */
    public String getStatus() {
        return status;
    }

    /** @param status Define o estado. */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return Custo do serviço. */
    public BigDecimal getCusto() {
        return custo;
    }

    /** @param custo Define o custo. */
    public void setCusto(BigDecimal custo) {
        this.custo = custo;
    }

    /** @return NIF do cliente. */
    public String getClienteNif() {
        return clienteNif;
    }

    /** @param clienteNif Define o NIF do cliente. */
    public void setClienteNif(String clienteNif) {
        this.clienteNif = clienteNif;
    }

    /** @return Identificador do animal. */
    public Integer getAnimalId() {
        return animalId;
    }

    /** @param animalId Define o identificador do animal. */
    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    /** @return Identificador do horário. */
    public Integer getHorarioId() {
        return horarioId;
    }

    /** @param horarioId Define o horário. */
    public void setHorarioId(Integer horarioId) {
        this.horarioId = horarioId;
    }

    /** @return Identificador da clínica. */
    public Integer getClinicaId() {
        return clinicaId;
    }

    /** @param clinicaId Define a clínica. */
    public void setClinicaId(Integer clinicaId) {
        this.clinicaId = clinicaId;
    }

    /** @return Identificador do tipo de serviço. */
    public Integer getTipoServicoId() {
        return tipoServicoId;
    }

    /** @param tipoServicoId Define o tipo de serviço. */
    public void setTipoServicoId(Integer tipoServicoId) {
        this.tipoServicoId = tipoServicoId;
    }
}
