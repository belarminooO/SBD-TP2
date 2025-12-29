package cliente;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Entidade abstrata representativa de um cliente no sistema VetCare.
 * Serve como base para a gestão de tutores, suportando tanto pessoas singulares
 * como entidades coletivas através de uma arquitetura de herança.
 */
public abstract class Cliente {

    /** Número de Identificação Fiscal do cliente. */
    protected String nif;

    /** Nome completo do cliente ou denominação social. */
    protected String nomeCompleto;

    /** Informação de contacto, incluindo telefone ou correio eletrónico. */
    protected String contactos;

    /** Morada de residência ou sede social. */
    protected String morada;

    /** Distrito da localização. */
    protected String distrito;

    /** Concelho da localização. */
    protected String concelho;

    /** Freguesia da localização. */
    protected String freguesia;

    /** Preferências de idioma para comunicações e interface. */
    protected String preferenciasLinguisticas;

    /** Discriminador que identifica a especialização do cliente. */
    protected String tipoCliente;

    /**
     * Construtor por omissão.
     */
    public Cliente() {
    }

    /**
     * Construtor que inicializa os atributos comuns a partir de um registo da base
     * de dados.
     * 
     * @param rs ResultSet posicionado no registo a processar.
     * @throws SQLException Caso ocorra um erro no acesso aos campos.
     */
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

    /**
     * Construtor que mapeia os dados recebidos através de um pedido web.
     * 
     * @param request Pedido HTTP contendo os parâmetros do cliente.
     */
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

    /** @return NIF do cliente. */
    public String getNif() {
        return nif;
    }

    /** @param nif Define o NIF. */
    public void setNif(String nif) {
        this.nif = nif;
    }

    /** @return Nome completo. */
    public String getNomeCompleto() {
        return nomeCompleto;
    }

    /** @param nomeCompleto Define o nome. */
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    /** @return Contactos registados. */
    public String getContactos() {
        return contactos;
    }

    /** @param contactos Define os contactos. */
    public void setContactos(String contactos) {
        this.contactos = contactos;
    }

    /** @return Morada. */
    public String getMorada() {
        return morada;
    }

    /** @param morada Define a morada. */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /** @return Distrito. */
    public String getDistrito() {
        return distrito;
    }

    /** @param distrito Define o distrito. */
    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    /** @return Concelho. */
    public String getConcelho() {
        return concelho;
    }

    /** @param concelho Define o concelho. */
    public void setConcelho(String concelho) {
        this.concelho = concelho;
    }

    /** @return Freguesia. */
    public String getFreguesia() {
        return freguesia;
    }

    /** @param freguesia Define a freguesia. */
    public void setFreguesia(String freguesia) {
        this.freguesia = freguesia;
    }

    /** @return Preferências linguísticas. */
    public String getPreferenciasLinguisticas() {
        return preferenciasLinguisticas;
    }

    /** @param preferenciasLinguisticas Define o idioma preferencial. */
    public void setPreferenciasLinguisticas(String preferenciasLinguisticas) {
        this.preferenciasLinguisticas = preferenciasLinguisticas;
    }

    /** @return Tipo de cliente. */
    public String getTipoCliente() {
        return tipoCliente;
    }

    /** @param tipoCliente Define o tipo discriminador. */
    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    /**
     * Valida os dados obrigatórios do cliente, assegurando que o NIF
     * cumpre o formato legal de 9 dígitos.
     * 
     * @return Verdadeiro se os dados forem consistentes.
     */
    public boolean valid() {
        return nif != null && nif.matches("\\d{9}") && nomeCompleto != null && !nomeCompleto.isEmpty();
    }

    /** @return Representação textual do cliente. */
    @Override
    public String toString() {
        return nomeCompleto + " (" + nif + ")";
    }
}
