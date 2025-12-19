package clinica;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class Horario {
    private Integer idHorario;
    private String diaSemana;
    private Time horaInicio;
    private Time horaFim;
    private Integer clinicaId;

    public Horario() {}

    public Horario(ResultSet rs) throws SQLException {
        this.idHorario = rs.getInt("IDHorario");
        this.diaSemana = rs.getString("DiaSemana");
        this.horaInicio = rs.getTime("HoraInicio");
        this.horaFim = rs.getTime("HoraFim");
        this.clinicaId = rs.getInt("Clinica_IDClinica");
    }

    public Integer getIdHorario() { return idHorario; }
    public void setIdHorario(Integer idHorario) { this.idHorario = idHorario; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public Time getHoraInicio() { return horaInicio; }
    public void setHoraInicio(Time horaInicio) { this.horaInicio = horaInicio; }

    public Time getHoraFim() { return horaFim; }
    public void setHoraFim(Time horaFim) { this.horaFim = horaFim; }
    
    public Integer getClinicaId() { return clinicaId; }
    public void setClinicaId(Integer clinicaId) { this.clinicaId = clinicaId; }
    
    @Override
    public String toString() { 
        return diaSemana + ": " + horaInicio + " - " + horaFim; 
    }
}
