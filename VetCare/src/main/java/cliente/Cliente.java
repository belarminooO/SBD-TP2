package cliente;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

public abstract class Cliente {
    protected String nif;
    protected String nomeCompleto;
    protected String contactos;
    protected String morada;
    protected String distrito;
    protected String concelho;
    protected String freguesia;
    protected String preferenciasLinguisticas;
    protected String tipoCliente; 

    public Cliente() {}

    public Cliente(ResultSet rs) throws SQLException {
        this.nif = rs.getString("NIF");
        this.nomeCompleto = rs.getString("NomeCompleto");
        this.contactos = rs.getString("Contactos");
        this.morada = rs.getString("Morada");
        this.distrito = rs.getString("Distrito");
        this.concelho = rs.getString("Concelho");
        this.freguesia = rs.getString("Freguesia");
        this.preferenciasLinguisticas = rs.getString("PreferenciasLinguisticas");
        this.tipoCliente = rs.getString("TipoCliente");
    }
    
    public Cliente(HttpServletRequest request) {
        this.nif = request.getParameter("NIF");
        this.nomeCompleto = request.getParameter("NomeCompleto");
        this.contactos = request.getParameter("Contactos");
        this.morada = request.getParameter("Morada");
        this.distrito = request.getParameter("Distrito");
        this.concelho = request.getParameter("Concelho");
        this.freguesia = request.getParameter("Freguesia");
        this.preferenciasLinguisticas = request.getParameter("PreferenciasLinguisticas");
        this.tipoCliente = request.getParameter("TipoCliente");
    }

    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = nif; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getContactos() { return contactos; }
    public void setContactos(String contactos) { this.contactos = contactos; }
    
    public String getMorada() { return morada; }
    public void setMorada(String morada) { this.morada = morada; }
    
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    
    public String getConcelho() { return concelho; }
    public void setConcelho(String concelho) { this.concelho = concelho; }
    
    public String getFreguesia() { return freguesia; }
    public void setFreguesia(String freguesia) { this.freguesia = freguesia; }

    public String getPreferenciasLinguisticas() { return preferenciasLinguisticas; }
    public void setPreferenciasLinguisticas(String preferenciasLinguisticas) { this.preferenciasLinguisticas = preferenciasLinguisticas; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public boolean valid() {
        return nif != null && nif.matches("\\d{9}") && nomeCompleto != null && !nomeCompleto.isEmpty();
    }
    
    @Override
    public String toString() { return nomeCompleto + " (" + nif + ")"; }
}
