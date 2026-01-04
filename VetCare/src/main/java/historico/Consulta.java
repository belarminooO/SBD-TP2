package historico;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Representa uma prestação de serviço do tipo consulta clínica.
 * Estende a classe base PrestacaoServico, adicionando atributos específicos
 * para o registo de observações, diagnóstico e prescrições.
 */
public class Consulta extends PrestacaoServico {

    /** Motivo principal da visita do animal à clínica. */
    private String motivo;

    /** Descrição dos sintomas observados durante a consulta. */
    private String sintomas;

    /** Conclusão clínica ou diagnóstico determinado pelo médico veterinário. */
    private String diagnostico;

    /** Lista de medicamentos ou tratamentos prescritos. */
    private String medicacaoPrescrita;

    /**
     * Inicializa uma nova consulta definindo o discriminador correspondente.
     */
    public Consulta() {
        this.tipoDiscriminador = "Consulta";
    }

    /**
     * Inicializa uma consulta a partir de um conjunto de resultados da base de
     * dados.
     * Tenta recuperar as colunas específicas, ignorando falhas caso estas não
     * estejam
     * presentes na consulta SQL original.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     * @throws SQLException Em caso de erro grave no acesso à base de dados.
     */
    public Consulta(ResultSet rs) throws SQLException {
        super(rs);
        try {
            this.motivo = rs.getString("Motivo");
            this.sintomas = rs.getString("Sintomas");
            this.diagnostico = rs.getString("Diagnostico");
            this.medicacaoPrescrita = rs.getString("MedicacaoPrescrita");
        } catch (SQLException e) {
        }
    }

    /** @return O motivo da consulta. */
    public String getMotivo() {
        return motivo;
    }

    /** @param motivo O motivo a atribuir. */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /** @return Os sintomas observados. */
    public String getSintomas() {
        return sintomas;
    }

    /** @param sintomas Os sintomas a atribuir. */
    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    /** @return O diagnóstico determinado. */
    public String getDiagnostico() {
        return diagnostico;
    }

    /** @param diagnostico O diagnóstico a atribuir. */
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    /** @return A medicação prescrita. */
    public String getMedicacaoPrescrita() {
        return medicacaoPrescrita;
    }

    /** @param medicacaoPrescrita A medicação a atribuir. */
    public void setMedicacaoPrescrita(String medicacaoPrescrita) {
        this.medicacaoPrescrita = medicacaoPrescrita;
    }
}
