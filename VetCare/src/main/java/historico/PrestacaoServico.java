package historico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Representa a entidade base para todos os registos do histórico clínico.
 * Implementa a estratégia de herança Table-Per-Type (TPT), servindo como classe
 * abstrata para os diversos tipos de intervenções clínicas.
 */
public abstract class PrestacaoServico {

    /** Identificador único da prestação de serviço. */
    protected Integer idPrestacao;

    /** Data e hora em que a intervenção foi realizada. */
    protected Timestamp dataHora;

    /** Notas e observações gerais sobre o procedimento realizado. */
    protected String detalhesGerais;

    /**
     * Identificador do subtipo concreto da prestação.
     * Utilizado para discriminar a classe e a tabela específica associada.
     */
    protected String tipoDiscriminador;

    /** Identificador do animal associado à prestação. */
    protected Integer animalId;

    /** Identificador do agendamento que originou a prestação (opcional). */
    protected Integer agendamentoId;

    /** Identificador do tipo de serviço categorizado. */
    protected Integer tipoServicoId;

    /**
     * Construtor padrão da classe.
     */
    public PrestacaoServico() {
    }

    /**
     * Inicializa uma nova instância com base num conjunto de resultados da base de
     * dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public PrestacaoServico(ResultSet rs) throws SQLException {
        this.idPrestacao = rs.getInt("IDPrestacao");
        this.dataHora = rs.getTimestamp("DataHora");
        this.detalhesGerais = rs.getString("DetalhesGerais");
        this.tipoDiscriminador = rs.getString("TipoDiscriminador");
        this.animalId = rs.getInt("Animal_IDAnimal");
        this.agendamentoId = rs.getInt("Agendamento_IDAgendamento");

        if (rs.wasNull()) {
            this.agendamentoId = null;
        }

        this.tipoServicoId = rs.getInt("TipoServico_IDServico");
    }

    /** @return O identificador da prestação. */
    public Integer getIdPrestacao() {
        return idPrestacao;
    }

    /** @param idPrestacao O identificador a atribuir. */
    public void setIdPrestacao(Integer idPrestacao) {
        this.idPrestacao = idPrestacao;
    }

    /** @return A data e hora da intervenção. */
    public Timestamp getDataHora() {
        return dataHora;
    }

    /** @param dataHora A data e hora a atribuir. */
    public void setDataHora(Timestamp dataHora) {
        this.dataHora = dataHora;
    }

    /** @return Os detalhes gerais da prestação. */
    public String getDetalhesGerais() {
        return detalhesGerais;
    }

    /** @param detalhesGerais Os detalhes a atribuir. */
    public void setDetalhesGerais(String detalhesGerais) {
        this.detalhesGerais = detalhesGerais;
    }

    /** @return O discriminador de tipo. */
    public String getTipoDiscriminador() {
        return tipoDiscriminador;
    }

    /** @param tipoDiscriminador O discriminador a atribuir. */
    public void setTipoDiscriminador(String tipoDiscriminador) {
        this.tipoDiscriminador = tipoDiscriminador;
    }

    /** @return O identificador do animal. */
    public Integer getAnimalId() {
        return animalId;
    }

    /** @param animalId O identificador do animal a atribuir. */
    public void setAnimalId(Integer animalId) {
        this.animalId = animalId;
    }

    /** @return O identificador do agendamento. */
    public Integer getAgendamentoId() {
        return agendamentoId;
    }

    /** @param agendamentoId O identificador do agendamento a atribuir. */
    public void setAgendamentoId(Integer agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    /** @return O identificador do tipo de serviço. */
    public Integer getTipoServicoId() {
        return tipoServicoId;
    }

    /** @param tipoServicoId O identificador do tipo de serviço a atribuir. */
    public void setTipoServicoId(Integer tipoServicoId) {
        this.tipoServicoId = tipoServicoId;
    }
}
