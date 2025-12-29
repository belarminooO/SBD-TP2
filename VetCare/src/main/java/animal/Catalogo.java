package animal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

/**
 * Representa um catálogo de informações biológicas e cuidados específicos por
 * espécie.
 * Atua como um repositório de conhecimento para suporte à triagem clínica e
 * monitorização do desenvolvimento dos animais.
 */
public class Catalogo {

    /** Nome comum da espécie ou raça. */
    private String nomeComum;

    /** Nome científico da espécie. */
    private String nomeCientifico;

    /** Tipo de dieta recomendada para a espécie. */
    private String regimeAlimentar;

    /** Descrição dos padrões de atividade e comportamento. */
    private String padroesAtividade;

    /** Tipo e características da vocalização. */
    private String vocalizacao;

    /** Expectativa de vida média em anos. */
    private int expectativaVida;

    /** Peso médio de um exemplar adulto em quilogramas. */
    private BigDecimal pesoAdulto;

    /** Comprimento médio de um exemplar adulto em centímetros. */
    private BigDecimal comprimentoAdulto;

    /** Classificação do porte do animal (ex: Pequeno, Médio, Grande). */
    private String porte;

    /** Lista de doenças ou condições com predisposição genética. */
    private String predisposicoesGeneticas;

    /** Conjunto de recomendações e cuidados específicos para a espécie. */
    private String cuidadosEspecificos;

    /**
     * Construtor padrão da classe.
     */
    public Catalogo() {
    }

    /**
     * Inicializa um objeto de catálogo a partir de um conjunto de resultados da
     * base de dados.
     * 
     * @param rs ResultSet contendo os dados da espécie.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public Catalogo(ResultSet rs) throws SQLException {
        this.nomeComum = rs.getString("NomeComum");
        this.nomeCientifico = rs.getString("NomeCientifico");
        this.regimeAlimentar = rs.getString("RegimeAlimentar");
        this.padroesAtividade = rs.getString("PadroesAtividade");
        this.vocalizacao = rs.getString("Vocalizacao");
        this.expectativaVida = rs.getInt("ExpectativaVida");
        this.pesoAdulto = rs.getBigDecimal("PesoAdulto");
        this.comprimentoAdulto = rs.getBigDecimal("ComprimentoAdulto");
        this.porte = rs.getString("Porte");
        this.predisposicoesGeneticas = rs.getString("PredisposicoesGeneticas");
        this.cuidadosEspecificos = rs.getString("CuidadosEspecificos");
    }

    /** @return O nome comum. */
    public String getNomeComum() {
        return nomeComum;
    }

    /** @param nomeComum O nome comum a atribuir. */
    public void setNomeComum(String nomeComum) {
        this.nomeComum = nomeComum;
    }

    /** @return O nome científico. */
    public String getNomeCientifico() {
        return nomeCientifico;
    }

    /** @param nomeCientifico O nome científico a atribuir. */
    public void setNomeCientifico(String nomeCientifico) {
        this.nomeCientifico = nomeCientifico;
    }

    /** @return O regime alimentar. */
    public String getRegimeAlimentar() {
        return regimeAlimentar;
    }

    /** @param regimeAlimentar O regime alimentar a atribuir. */
    public void setRegimeAlimentar(String regimeAlimentar) {
        this.regimeAlimentar = regimeAlimentar;
    }

    /** @return Os padrões de atividade. */
    public String getPadroesAtividade() {
        return padroesAtividade;
    }

    /** @param padroesAtividade Os padrões a atribuir. */
    public void setPadroesAtividade(String padroesAtividade) {
        this.padroesAtividade = padroesAtividade;
    }

    /** @return A vocalização. */
    public String getVocalizacao() {
        return vocalizacao;
    }

    /** @param vocalizacao A vocalização a atribuir. */
    public void setVocalizacao(String vocalizacao) {
        this.vocalizacao = vocalizacao;
    }

    /** @return A expectativa de vida. */
    public int getExpectativaVida() {
        return expectativaVida;
    }

    /** @param expectativaVida A expectativa a atribuir. */
    public void setExpectativaVida(int expectativaVida) {
        this.expectativaVida = expectativaVida;
    }

    /** @return O peso adulto de referência. */
    public BigDecimal getPesoAdulto() {
        return pesoAdulto;
    }

    /** @param pesoAdulto O peso a atribuir. */
    public void setPesoAdulto(BigDecimal pesoAdulto) {
        this.pesoAdulto = pesoAdulto;
    }

    /** @return O comprimento adulto de referência. */
    public BigDecimal getComprimentoAdulto() {
        return comprimentoAdulto;
    }

    /** @param comprimentoAdulto O comprimento a atribuir. */
    public void setComprimentoAdulto(BigDecimal comprimentoAdulto) {
        this.comprimentoAdulto = comprimentoAdulto;
    }

    /** @return O porte. */
    public String getPorte() {
        return porte;
    }

    /** @param porte O porte a atribuir. */
    public void setPorte(String porte) {
        this.porte = porte;
    }

    /** @return As predisposições genéticas. */
    public String getPredisposicoesGeneticas() {
        return predisposicoesGeneticas;
    }

    /** @param predisposicoesGeneticas As predisposições a atribuir. */
    public void setPredisposicoesGeneticas(String predisposicoesGeneticas) {
        this.predisposicoesGeneticas = predisposicoesGeneticas;
    }

    /** @return Os cuidados específicos. */
    public String getCuidadosEspecificos() {
        return cuidadosEspecificos;
    }

    /** @param cuidadosEspecificos Os cuidados a atribuir. */
    public void setCuidadosEspecificos(String cuidadosEspecificos) {
        this.cuidadosEspecificos = cuidadosEspecificos;
    }

    @Override
    public String toString() {
        return nomeComum;
    }
}
