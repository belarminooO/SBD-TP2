package historico;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma prestação de serviço do tipo exame físico.
 * Estende a classe base PrestacaoServico, permitindo o registo objetivo de
 * sinais vitais e métricas físicas essenciais para o diagnóstico clínico.
 */
public class ExameFisico extends PrestacaoServico {

    /** Temperatura retal do animal, medida em graus Celsius. */
    private BigDecimal temperatura;

    /** Massa corporal do animal em quilogramas (Kg). */
    private BigDecimal peso;

    /** Frequência cardíaca medida em batimentos por minuto (BPM). */
    private Integer frequenciaCardiaca;

    /** Frequência respiratória medida em ciclos por minuto. */
    private Integer frequenciaRespiratoria;

    /**
     * Inicializa uma nova instância de exame físico definindo o discriminador
     * correspondente.
     */
    public ExameFisico() {
        super();
        this.tipoDiscriminador = "ExameFisico";
    }

    /**
     * Inicializa um exame físico a partir de um conjunto de resultados da base de
     * dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public ExameFisico(ResultSet rs) throws SQLException {
        super(rs);
        this.temperatura = rs.getBigDecimal("Temperatura");
        this.peso = rs.getBigDecimal("Peso");
        this.frequenciaCardiaca = rs.getInt("FrequenciaCardiaca");
        this.frequenciaRespiratoria = rs.getInt("FrequenciaRespiratoria");
    }

    /** @return A temperatura corporal. */
    public BigDecimal getTemperatura() {
        return temperatura;
    }

    /** @param temperatura A temperatura a atribuir. */
    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    /** @return O peso corporal. */
    public BigDecimal getPeso() {
        return peso;
    }

    /** @param peso O peso a atribuir. */
    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    /** @return A frequência cardíaca. */
    public Integer getFrequenciaCardiaca() {
        return frequenciaCardiaca;
    }

    /** @param frequenciaCardiaca A frequência cardíaca a atribuir. */
    public void setFrequenciaCardiaca(Integer frequenciaCardiaca) {
        this.frequenciaCardiaca = frequenciaCardiaca;
    }

    /** @return A frequência respiratória. */
    public Integer getFrequenciaRespiratoria() {
        return frequenciaRespiratoria;
    }

    /** @param frequenciaRespiratoria A frequência respiratória a atribuir. */
    public void setFrequenciaRespiratoria(Integer frequenciaRespiratoria) {
        this.frequenciaRespiratoria = frequenciaRespiratoria;
    }
}
