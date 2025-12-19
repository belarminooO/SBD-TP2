package historico;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import jakarta.servlet.http.HttpServletRequest;
import util.DataFormatter;

public abstract class PrestacaoServico {
    protected Integer idPrestacao;
    protected Timestamp dataHora;
    protected String detalhesGerais;
    protected String tipoDiscriminador;
    protected Integer animalId;
    protected Integer agendamentoId;
    protected Integer tipoServicoId;

    public PrestacaoServico() {}

    public PrestacaoServico(ResultSet rs) throws SQLException {
        this.idPrestacao = rs.getInt("IDPrestacao");
        this.dataHora = rs.getTimestamp("DataHora");
        this.detalhesGerais = rs.getString("DetalhesGerais");
        this.tipoDiscriminador = rs.getString("TipoDiscriminador");
        this.animalId = rs.getInt("Animal_IDAnimal");
        this.agendamentoId = rs.getInt("Agendamento_IDAgendamento");
        if(rs.wasNull()) this.agendamentoId = null;
        this.tipoServicoId = rs.getInt("TipoServico_IDServico");
    }

    public Integer getIdPrestacao() { return idPrestacao; }
    public void setIdPrestacao(Integer idPrestacao) { this.idPrestacao = idPrestacao; }

    public Timestamp getDataHora() { return dataHora; }
    public void setDataHora(Timestamp dataHora) { this.dataHora = dataHora; }

    public String getDetalhesGerais() { return detalhesGerais; }
    public void setDetalhesGerais(String detalhesGerais) { this.detalhesGerais = detalhesGerais; }

    public String getTipoDiscriminador() { return tipoDiscriminador; }
    public void setTipoDiscriminador(String tipoDiscriminador) { this.tipoDiscriminador = tipoDiscriminador; }

    public Integer getAnimalId() { return animalId; }
    public void setAnimalId(Integer animalId) { this.animalId = animalId; }

    public Integer getAgendamentoId() { return agendamentoId; }
    public void setAgendamentoId(Integer agendamentoId) { this.agendamentoId = agendamentoId; }

    public Integer getTipoServicoId() { return tipoServicoId; }
    public void setTipoServicoId(Integer tipoServicoId) { this.tipoServicoId = tipoServicoId; }
}
