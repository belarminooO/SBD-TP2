package animal;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;
import util.DataFormatter;

public class Animal {
    private Integer idAnimal;
    private String nome;
    private String raca;
    private String sexo; // M, F
    private Date dataNascimento;
    private String filiacao;
    private String estadoReprodutivo;
    private String alergias;
    private String cores;
    private BigDecimal pesoAtual;
    private String caracteristicasDistintivas;
    private String numeroTransponder;
    private String fotografia;
    private String clienteNif;
    private String catalogoNomeComum;
    private int expectativaVida;

    public Animal() {}

    public Animal(ResultSet rs) throws SQLException {
        this.idAnimal = rs.getInt("IDAnimal");
        this.nome = rs.getString("Nome");
        this.raca = rs.getString("Raca");
        this.sexo = rs.getString("Sexo");
        this.dataNascimento = rs.getDate("DataNascimento");
        this.filiacao = rs.getString("Filiacao");
        this.estadoReprodutivo = rs.getString("EstadoReprodutivo");
        this.alergias = rs.getString("Alergias");
        this.cores = rs.getString("Cores");
        this.pesoAtual = rs.getBigDecimal("PesoAtual");
        this.caracteristicasDistintivas = rs.getString("CaracteristicasDistintivas");
        this.numeroTransponder = rs.getString("NumeroTransponder");
        this.fotografia = rs.getString("Fotografia");
        this.clienteNif = rs.getString("Cliente_NIF");
        this.catalogoNomeComum = rs.getString("Catalogo_NomeComum");
        try { this.expectativaVida = rs.getInt("ExpectativaVida"); } catch (SQLException e) {}
    }

    public Animal(HttpServletRequest request) {
        String idStr = request.getParameter("IDAnimal");
        if (idStr != null && !idStr.isEmpty()) this.idAnimal = Integer.parseInt(idStr);
        
        this.nome = request.getParameter("Nome");
        this.raca = request.getParameter("Raca");
        this.sexo = request.getParameter("Sexo");
        this.dataNascimento = DataFormatter.StringToSqlDate(request.getParameter("DataNascimento"));
        this.filiacao = request.getParameter("Filiacao");
        this.estadoReprodutivo = request.getParameter("EstadoReprodutivo");
        this.alergias = request.getParameter("Alergias");
        this.cores = request.getParameter("Cores");
        String peso = request.getParameter("PesoAtual");
        if (peso != null && !peso.isEmpty()) this.pesoAtual = new BigDecimal(peso);
        this.caracteristicasDistintivas = request.getParameter("CaracteristicasDistintivas");
        this.numeroTransponder = request.getParameter("NumeroTransponder");
        this.fotografia = request.getParameter("Fotografia");
        this.clienteNif = request.getParameter("Cliente_NIF");
        this.catalogoNomeComum = request.getParameter("Catalogo_NomeComum");
    }

    public Integer getIdAnimal() { return idAnimal; }
    public void setIdAnimal(Integer idAnimal) { this.idAnimal = idAnimal; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    // Getters necessary for DAO
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getFiliacao() { return filiacao; }
    public void setFiliacao(String filiacao) { this.filiacao = filiacao; }

    public String getEstadoReprodutivo() { return estadoReprodutivo; }
    public void setEstadoReprodutivo(String estadoReprodutivo) { this.estadoReprodutivo = estadoReprodutivo; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getCores() { return cores; }
    public void setCores(String cores) { this.cores = cores; }

    public BigDecimal getPesoAtual() { return pesoAtual; }
    public void setPesoAtual(BigDecimal pesoAtual) { this.pesoAtual = pesoAtual; }

    public String getCaracteristicasDistintivas() { return caracteristicasDistintivas; }
    public void setCaracteristicasDistintivas(String caracteristicasDistintivas) { this.caracteristicasDistintivas = caracteristicasDistintivas; }

    public String getNumeroTransponder() { return numeroTransponder; }
    public void setNumeroTransponder(String numeroTransponder) { this.numeroTransponder = numeroTransponder; }

    public String getFotografia() { return fotografia; }
    public void setFotografia(String fotografia) { this.fotografia = fotografia; }

    public String getClienteNif() { return clienteNif; }
    public void setClienteNif(String clienteNif) { this.clienteNif = clienteNif; }

    public String getCatalogoNomeComum() { return catalogoNomeComum; }
    public void setCatalogoNomeComum(String catalogoNomeComum) { this.catalogoNomeComum = catalogoNomeComum; }
    public int getExpectativaVida() { return expectativaVida; }
    public void setExpectativaVida(int expectativaVida) { this.expectativaVida = expectativaVida; }

    public String getIdadeFormatada() {
        return util.IdadeCalculator.getIdadeFormatada(this.dataNascimento);
    }
    
    public String getIdadeDetalhada() {
        return util.IdadeCalculator.getIdadeDetalhada(this.dataNascimento);
    }
    
    public String getEscalaoEtario(int expectativaVida) {
        return util.IdadeCalculator.getEscalaoEtario(this.dataNascimento, expectativaVida);
    }

    public boolean valid() {
        return nome != null && !nome.isEmpty() && clienteNif != null && catalogoNomeComum != null && pesoAtual != null;
    }
}
