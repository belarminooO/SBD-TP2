package clinica;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Representa uma unidade física de prestação de serviços veteriniários.
 * Contém informações sobre a localização geográfica e morada da clínica.
 */
public class Clinica {

    /** Identificador único da clínica. */
    private Integer idClinica = null;

    /** Localidade ou cidade onde a clínica está situada. */
    private String localidade = null;

    /** Morada postal completa da unidade. */
    private String moradaCompleta = null;

    /** Coordenadas geográficas (latitude/longitude) para mapeamento. */
    private String coordenadasGeograficas = null;

    /**
     * Construtor padrão da classe.
     */
    public Clinica() {
    }

    /**
     * Inicializa uma nova instância com dados completos.
     * 
     * @param idClinica              Identificador único da clínica.
     * @param localidade             Nome da localidade.
     * @param moradaCompleta         Endereço completo.
     * @param coordenadasGeograficas Coordenadas para geolocalização.
     */
    public Clinica(Integer idClinica, String localidade, String moradaCompleta, String coordenadasGeograficas) {
        this.idClinica = idClinica;
        this.localidade = localidade;
        this.moradaCompleta = moradaCompleta;
        this.coordenadasGeograficas = coordenadasGeograficas;
    }

    /**
     * Inicializa uma clínica a partir de um conjunto de resultados da base de
     * dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     */
    public Clinica(ResultSet rs) {
        try {
            this.idClinica = rs.getInt("IDClinica");
            this.localidade = rs.getString("Localidade");
            this.moradaCompleta = rs.getString("MoradaCompleta");
            this.coordenadasGeograficas = rs.getString("CoordenadasGeograficas");
        } catch (SQLException e) {
            System.err.println("Erro ao converter ResultSet para Clinica: " + e.getMessage());
        }
    }

    /**
     * Inicializa uma clínica com base nos parâmetros de um pedido HTTP.
     * 
     * @param request Pedido HTTP contendo os dados do formulário.
     */
    public Clinica(HttpServletRequest request) {
        String idStr = request.getParameter("IDClinica");
        if (idStr != null && !idStr.isEmpty()) {
            this.idClinica = Integer.parseInt(idStr);
        }
        this.localidade = request.getParameter("Localidade");
        this.moradaCompleta = request.getParameter("MoradaCompleta");
        this.coordenadasGeograficas = request.getParameter("CoordenadasGeograficas");
    }

    /** @return O identificador da clínica. */
    public Integer getIdClinica() {
        return idClinica;
    }

    /** @param idClinica O identificador a atribuir. */
    public void setIdClinica(Integer idClinica) {
        this.idClinica = idClinica;
    }

    /** @return A localidade da clínica. */
    public String getLocalidade() {
        return localidade;
    }

    /** @param localidade A localidade a atribuir. */
    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    /** @return A morada completa. */
    public String getMoradaCompleta() {
        return moradaCompleta;
    }

    /** @param moradaCompleta A morada a atribuir. */
    public void setMoradaCompleta(String moradaCompleta) {
        this.moradaCompleta = moradaCompleta;
    }

    /** @return As coordenadas geográficas. */
    public String getCoordenadasGeograficas() {
        return coordenadasGeograficas;
    }

    /** @param coordenadasGeograficas As coordenadas a atribuir. */
    public void setCoordenadasGeograficas(String coordenadasGeograficas) {
        this.coordenadasGeograficas = coordenadasGeograficas;
    }

    /**
     * Verifica se os dados mínimos obrigatórios da clínica estão preenchidos.
     * 
     * @return Verdadeiro se a clínica for válida para registo.
     */
    public boolean valid() {
        return localidade != null && !localidade.isEmpty() && moradaCompleta != null && !moradaCompleta.isEmpty();
    }

    @Override
    public String toString() {
        return "Clinica [ID=" + idClinica + ", Localidade=" + localidade + "]";
    }
}
