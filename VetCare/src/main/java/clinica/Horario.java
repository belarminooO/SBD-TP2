package clinica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

/**
 * Representa o período operacional de uma unidade clínica num determinado dia.
 * Define as janelas temporais de funcionamento, sendo essencial para a
 * validação de agendamentos e gestão de escalas.
 */
public class Horario {

    /** Identificador único do registo de horário. */
    private Integer idHorario;

    /** Dia da semana associado ao horário (ex: Segunda-feira). */
    private String diaSemana;

    /** Hora de abertura ou início da atividade clínica. */
    private Time horaInicio;

    /** Hora de fecho ou término da atividade clínica. */
    private Time horaFim;

    /** Identificador da clínica à qual este horário pertence. */
    private Integer clinicaId;

    /**
     * Construtor padrão da classe.
     */
    public Horario() {
    }

    /**
     * Inicializa um horário a partir de um conjunto de resultados da base de dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public Horario(ResultSet rs) throws SQLException {
        this.idHorario = rs.getInt("IDHorario");
        this.diaSemana = rs.getString("DiaSemana");
        this.horaInicio = rs.getTime("HoraInicio");
        this.horaFim = rs.getTime("HoraFim");
        this.clinicaId = rs.getInt("Clinica_IDClinica");
    }

    /** @return O identificador do horário. */
    public Integer getIdHorario() {
        return idHorario;
    }

    /** @param idHorario O identificador a atribuir. */
    public void setIdHorario(Integer idHorario) {
        this.idHorario = idHorario;
    }

    /** @return O dia da semana. */
    public String getDiaSemana() {
        return diaSemana;
    }

    /** @param diaSemana O dia da semana a atribuir. */
    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    /** @return A hora de início. */
    public Time getHoraInicio() {
        return horaInicio;
    }

    /** @param horaInicio A hora de início a atribuir. */
    public void setHoraInicio(Time horaInicio) {
        this.horaInicio = horaInicio;
    }

    /** @return A hora de fim. */
    public Time getHoraFim() {
        return horaFim;
    }

    /** @param horaFim A hora de fim a atribuir. */
    public void setHoraFim(Time horaFim) {
        this.horaFim = horaFim;
    }

    /** @return O identificador da clínica associada. */
    public Integer getClinicaId() {
        return clinicaId;
    }

    /** @param clinicaId O identificador da clínica a atribuir. */
    public void setClinicaId(Integer clinicaId) {
        this.clinicaId = clinicaId;
    }

    private String clinicaNome; // Temp field for display

    public void setClinicaNome(String nome) {
        this.clinicaNome = nome;
    }

    public String getClinicaNome() {
        return clinicaNome;
    }

    @Override
    public String toString() {
        return (clinicaNome != null ? clinicaNome + " - " : "") + diaSemana + ": " + horaInicio + " - " + horaFim;
    }
}
