package animal;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import jakarta.servlet.http.HttpServletRequest;
import util.DataFormatter;

/**
 * Entidade representativa de um animal no sistema.
 * Atua como objeto de transferência de dados entre a base de dados, a lógica
 * de negócio e o interface web. Inclui métodos para cálculo de métricas
 * biológicas e validação de integridade.
 */
public class Animal {

    /** Identificador único do animal. */
    private Integer idAnimal;

    /** Nome do animal. */
    private String nome;

    /** Raça do animal. */
    private String raca;

    /** Sexo do animal (M para Macho, F para Fêmea). */
    private String sexo;

    /** Data de nascimento do animal. */
    private Date dataNascimento;

    /** Nome do progenitor para registos de linhagem. */
    private String filiacao;

    /** Estado reprodutivo (ex: Castrado, Inteiro). */
    private String estadoReprodutivo;

    /** Lista de alergias conhecidas. */
    private String alergias;

    /** Descrição das cores do animal. */
    private String cores;

    /** Peso atual em quilogramas. */
    private BigDecimal pesoAtual;

    /** Marcas ou comportamentos distintivos. */
    private String caracteristicasDistintivas;

    /** Código do microchip ou transponder. */
    private String numeroTransponder;

    /** Dados binários da fotografia (armazenados em BLOB na BD). */
    private byte[] fotografia;

    /** NIF do cliente que detém a tutoria do animal. */
    private String clienteNif;

    /** Nome comum da espécie no catálogo biológico. */
    private String catalogoNomeComum;

    /** Expectativa de vida média da espécie (em anos). */
    private int expectativaVida;

    /**
     * Construtor por omissão.
     */
    public Animal() {
    }

    /**
     * Construtor que inicializa o objeto a partir de um registo da base de dados.
     * 
     * @param rs ResultSet posicionado no registo.
     * @throws SQLException Caso ocorra um erro no mapeamento dos campos.
     */
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
        this.fotografia = rs.getBytes("Fotografia");
        this.clienteNif = rs.getString("Cliente_NIF");
        this.catalogoNomeComum = rs.getString("Catalogo_NomeComum");

        try {
            this.expectativaVida = rs.getInt("ExpectativaVida");
        } catch (SQLException e) {
        }
    }

    /**
     * Construtor que captura dados a partir de um pedido HTTP.
     * Efetua a conversão de tipos de dados para o modelo interno.
     * 
     * @param request Pedido contendo os parâmetros do formulário.
     */
    public Animal(HttpServletRequest request) {
        String idStr = request.getParameter("IDAnimal");
        if (idStr != null && !idStr.isEmpty())
            this.idAnimal = Integer.parseInt(idStr);

        this.nome = request.getParameter("Nome");
        this.raca = request.getParameter("Raca");
        this.sexo = request.getParameter("Sexo");

        this.dataNascimento = DataFormatter.StringToSqlDate(request.getParameter("DataNascimento"));

        this.filiacao = request.getParameter("Filiacao");
        this.estadoReprodutivo = request.getParameter("EstadoReprodutivo");
        this.alergias = request.getParameter("Alergias");
        this.cores = request.getParameter("Cores");

        String peso = request.getParameter("PesoAtual");
        if (peso != null && !peso.isEmpty())
            this.pesoAtual = new BigDecimal(peso.replace(",", "."));

        this.caracteristicasDistintivas = request.getParameter("CaracteristicasDistintivas");
        this.numeroTransponder = request.getParameter("NumeroTransponder");
        // A fotografia binária é tratada separadamente no Servlet
        this.clienteNif = request.getParameter("Cliente_NIF");
        this.catalogoNomeComum = request.getParameter("Catalogo_NomeComum");
    }

    /** @return Identificador do animal. */
    public Integer getIdAnimal() {
        return idAnimal;
    }

    /** @param idAnimal Define o identificador. */
    public void setIdAnimal(Integer idAnimal) {
        this.idAnimal = idAnimal;
    }

    /** @return Nome do animal. */
    public String getNome() {
        return nome;
    }

    /** @param nome Define o nome. */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /** @return Raça do animal. */
    public String getRaca() {
        return raca;
    }

    /** @param raca Define a raça. */
    public void setRaca(String raca) {
        this.raca = raca;
    }

    /** @return Sexo do animal. */
    public String getSexo() {
        return sexo;
    }

    /** @param sexo Define o sexo. */
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    /** @return Data de nascimento. */
    public Date getDataNascimento() {
        return dataNascimento;
    }

    /** @param dataNascimento Define a data de nascimento. */
    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /** @return Nome do progenitor. */
    public String getFiliacao() {
        return filiacao;
    }

    /** @param filiacao Define a filiação. */
    public void setFiliacao(String filiacao) {
        this.filiacao = filiacao;
    }

    /** @return Estado reprodutivo. */
    public String getEstadoReprodutivo() {
        return estadoReprodutivo;
    }

    /** @param estadoReprodutivo Define o estado reprodutivo. */
    public void setEstadoReprodutivo(String estadoReprodutivo) {
        this.estadoReprodutivo = estadoReprodutivo;
    }

    /** @return Lista de alergias. */
    public String getAlergias() {
        return alergias;
    }

    /** @param alergias Define as alergias. */
    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    /** @return Descrição das cores. */
    public String getCores() {
        return cores;
    }

    /** @param cores Define as cores. */
    public void setCores(String cores) {
        this.cores = cores;
    }

    /** @return Peso atual em kg. */
    public BigDecimal getPesoAtual() {
        return pesoAtual;
    }

    /** @param pesoAtual Define o peso atual. */
    public void setPesoAtual(BigDecimal pesoAtual) {
        this.pesoAtual = pesoAtual;
    }

    /** @return Marcas distintivas. */
    public String getCaracteristicasDistintivas() {
        return caracteristicasDistintivas;
    }

    /** @param caracteristicasDistintivas Define as características. */
    public void setCaracteristicasDistintivas(String caracteristicasDistintivas) {
        this.caracteristicasDistintivas = caracteristicasDistintivas;
    }

    /** @return Número do microchip. */
    public String getNumeroTransponder() {
        return numeroTransponder;
    }

    /** @param numeroTransponder Define o número do transponder. */
    public void setNumeroTransponder(String numeroTransponder) {
        this.numeroTransponder = numeroTransponder;
    }

    /** @return Dados binários da fotografia. */
    public byte[] getFotografia() {
        return fotografia;
    }

    /** @param fotografia Define os dados binários da foto. */
    public void setFotografia(byte[] fotografia) {
        this.fotografia = fotografia;
    }

    /**
     * Retorna a fotografia convertida para Base64 para exibição em HTML.
     * 
     * @return String no formato "data:image/jpeg;base64,..." ou null.
     */
    public String getFotografiaBase64() {
        if (fotografia == null || fotografia.length == 0)
            return null;
        return "data:image/jpeg;base64," + java.util.Base64.getEncoder().encodeToString(fotografia);
    }

    /** @return NIF do tutor. */
    public String getClienteNif() {
        return clienteNif;
    }

    /** @param clienteNif Define o NIF do tutor. */
    public void setClienteNif(String clienteNif) {
        this.clienteNif = clienteNif;
    }

    /** @return Nome comum da espécie. */
    public String getCatalogoNomeComum() {
        return catalogoNomeComum;
    }

    /** @param catalogoNomeComum Define a espécie. */
    public void setCatalogoNomeComum(String catalogoNomeComum) {
        this.catalogoNomeComum = catalogoNomeComum;
    }

    /** @return Expectativa de vida da espécie. */
    public int getExpectativaVida() {
        return expectativaVida;
    }

    /** @param expectativaVida Define a expectativa de vida. */
    public void setExpectativaVida(int expectativaVida) {
        this.expectativaVida = expectativaVida;
    }

    /**
     * Formata a idade para apresentação textual (ex: "5 anos").
     * 
     * @return String com a idade formatada.
     */
    public String getIdadeFormatada() {
        return util.IdadeCalculator.getIdadeFormatada(this.dataNascimento);
    }

    /**
     * Calcula a idade detalhada em dias, semanas, meses ou anos.
     * 
     * @return String com a idade detalhada.
     */
    public String getIdadeDetalhada() {
        return util.IdadeCalculator.getIdadeDetalhada(this.dataNascimento);
    }

    /**
     * Determina o escalão etário baseando-se na expectativa de vida.
     * 
     * @param expectativaVida Valor de referência da espécie.
     * @return Escalão etário correspondente.
     */
    public String getEscalaoEtario(int expectativaVida) {
        return util.IdadeCalculator.getEscalaoEtario(this.dataNascimento, expectativaVida);
    }

    /**
     * Verifica se o objeto possui os dados mínimos obrigatórios.
     * 
     * @return Verdadeiro se o objeto for válido para gravação.
     */
    public boolean valid() {
        return nome != null && !nome.isEmpty() && clienteNif != null && catalogoNomeComum != null && pesoAtual != null
                && estadoReprodutivo != null;
    }
}
