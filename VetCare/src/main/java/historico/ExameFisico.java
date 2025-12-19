package historico;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExameFisico extends PrestacaoServico {
    private BigDecimal temperatura;
    private BigDecimal peso;
    private Integer frequenciaCardiaca;
    private Integer frequenciaRespiratoria;

    public ExameFisico() {
        super();
        this.tipoDiscriminador = "ExameFisico";
    }

    public ExameFisico(ResultSet rs) throws SQLException {
        super(rs);
        this.temperatura = rs.getBigDecimal("Temperatura");
        this.peso = rs.getBigDecimal("Peso");
        this.frequenciaCardiaca = rs.getInt("FrequenciaCardiaca");
        this.frequenciaRespiratoria = rs.getInt("FrequenciaRespiratoria");
    }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getFrequenciaCardiaca() { return frequenciaCardiaca; }
    public void setFrequenciaCardiaca(Integer frequenciaCardiaca) { this.frequenciaCardiaca = frequenciaCardiaca; }

    public Integer getFrequenciaRespiratoria() { return frequenciaRespiratoria; }
    public void setFrequenciaRespiratoria(Integer frequenciaRespiratoria) { this.frequenciaRespiratoria = frequenciaRespiratoria; }
}
