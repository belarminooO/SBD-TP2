package veterinario;

import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Representa os médicos veterinários pertencentes ao corpo clínico da VetCare.
 * A identificação unívoca é realizada através do número da licença
 * profissional,
 * garantindo a rastreabilidade e legalidade dos atos médicos praticados.
 */
public class Veterinario {

    /** Número da licença profissional (Cédula Profissional). */
    private String nLicenca = null;

    /** Nome completo do médico veterinário. */
    private String nome = null;

    /**
     * Construtor padrão da classe.
     */
    public Veterinario() {
    }

    /**
     * Inicializa um veterinário com os dados especificados.
     * 
     * @param nLicenca Número da licença profissional.
     * @param nome     Nome do veterinário.
     */
    public Veterinario(String nLicenca, String nome) {
        this.nLicenca = nLicenca;
        this.nome = nome;
    }

    /**
     * Inicializa um veterinário a partir de um conjunto de resultados da base de
     * dados.
     * 
     * @param rs ResultSet posicionado no registo pretendido.
     */
    public Veterinario(ResultSet rs) {
        try {
            this.nLicenca = rs.getString("NLicenca");
            this.nome = rs.getString("Nome");
        } catch (SQLException e) {
            System.err.println("Erro ao converter ResultSet para Veterinario: " + e.getMessage());
        }
    }

    /**
     * Inicializa um veterinário com base nos parâmetros de um pedido HTTP.
     * 
     * @param request Pedido HTTP contendo os dados do formulário.
     */
    public Veterinario(HttpServletRequest request) {
        this.nLicenca = request.getParameter("NLicenca");
        this.nome = request.getParameter("Nome");
    }

    /** @return O número da licença. */
    public String getNLicenca() {
        return nLicenca;
    }

    /** @param nLicenca O número da licença a atribuir. */
    public void setNLicenca(String nLicenca) {
        this.nLicenca = nLicenca;
    }

    /** @return O nome do veterinário. */
    public String getNome() {
        return nome;
    }

    /** @param nome O nome a atribuir. */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Verifica se os dados obrigatórios do veterinário estão preenchidos.
     * 
     * @return Verdadeiro se o objeto for válido para persistência.
     */
    public boolean valid() {
        return nLicenca != null && !nLicenca.isEmpty() && nome != null && !nome.isEmpty();
    }

    @Override
    public String toString() {
        return "Dr(a). " + nome + " (" + nLicenca + ")";
    }
}
