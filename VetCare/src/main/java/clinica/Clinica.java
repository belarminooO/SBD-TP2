package clinica;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

public class Clinica {

    private Integer idClinica = null;
    private String localidade = null;
    private String moradaCompleta = null;
    private String coordenadasGeograficas = null;

    public Clinica() {
    }

    public Clinica(Integer idClinica, String localidade, String moradaCompleta, String coordenadasGeograficas) {
        this.idClinica = idClinica;
        this.localidade = localidade;
        this.moradaCompleta = moradaCompleta;
        this.coordenadasGeograficas = coordenadasGeograficas;
    }

    public Clinica(ResultSet rs) {
        try {
            this.idClinica = rs.getInt("IDClinica");
            this.localidade = rs.getString("Localidade");
            this.moradaCompleta = rs.getString("MoradaCompleta");
            this.coordenadasGeograficas = rs.getString("CoordenadasGeograficas");
        } catch (SQLException e) {
            System.err.println("Erro ao ler Clinica: " + e.getMessage());
        }
    }
    
    public Clinica(HttpServletRequest request) {
        String idStr = request.getParameter("IDClinica");
        if(idStr != null && !idStr.isEmpty()) {
            this.idClinica = Integer.parseInt(idStr);
        }
        this.localidade = request.getParameter("Localidade");
        this.moradaCompleta = request.getParameter("MoradaCompleta");
        this.coordenadasGeograficas = request.getParameter("CoordenadasGeograficas");
    }

    public Integer getIdClinica() { return idClinica; }
    public void setIdClinica(Integer idClinica) { this.idClinica = idClinica; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getMoradaCompleta() { return moradaCompleta; }
    public void setMoradaCompleta(String moradaCompleta) { this.moradaCompleta = moradaCompleta; }

    public String getCoordenadasGeograficas() { return coordenadasGeograficas; }
    public void setCoordenadasGeograficas(String coordenadasGeograficas) { this.coordenadasGeograficas = coordenadasGeograficas; }

    public boolean valid() {
        return localidade != null && !localidade.isEmpty() && moradaCompleta != null && !moradaCompleta.isEmpty();
    }
    
    @Override
    public String toString() {
        return "Clinica [ID=" + idClinica + ", Localidade=" + localidade + "]";
    }
}
