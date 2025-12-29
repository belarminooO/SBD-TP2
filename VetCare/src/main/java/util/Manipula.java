package util;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * Motor de execução SQL e camada de abstração de dados.
 * 
 * Atua como intermediário entre a aplicação e o JDBC, gerindo conexões e
 * recursos
 * de forma automática.
 * 
 * Funcionalidades:
 * - Mapeamento de dados relacionais para objetos Java.
 * - Gestão automática de recursos (try-with-resources).
 * - Conversão de tipos entre SQL e Java.
 */
public class Manipula {

    /**
     * Configuração da base de dados.
     */
    private final Configura cfg;

    /**
     * Inicializa a instância com a configuração padrão do sistema.
     */
    public Manipula() {
        this(new Configura());
    }

    /**
     * Inicializa a instância com uma configuração personalizada.
     * 
     * @param cfg Objeto de configuração da base de dados.
     */
    public Manipula(Configura cfg) {
        this.cfg = Objects.requireNonNull(cfg, "A configuração é obrigatória para o funcionamento do motor.");
    }

    /**
     * Obtém uma conexão ativa à base de dados.
     * 
     * @return Conexão ativa.
     * @throws SQLException Se ocorrer erro na ligação.
     */
    public Connection getLigacao() throws SQLException {
        return cfg.getConnection();
    }

    /**
     * Obtém uma conexão com controlo manual de transação.
     * 
     * @param autocommit Define o modo de commit automático.
     * @return Conexão ativa configurada.
     */
    public Connection getLigacao(boolean autocommit) {
        return cfg.getConnection(autocommit);
    }

    /**
     * Executa comandos SQL de manipulação (INSERT, UPDATE, DELETE).
     * 
     * Utiliza PreparedStatement para garantir a segurança contra injeção SQL,
     * mapeando automaticamente os tipos de dados Java para SQL.
     * 
     * @param sqlSegura     Comando SQL parametrizado.
     * @param objParametros Lista de valores para os parâmetros.
     * @return Número de registos afetados ou -1 em caso de erro.
     */
    public int xDirectiva(String sqlSegura, List<Object> objParametros) {
        try (Connection conexao = getLigacao();
                PreparedStatement preparedStatement = conexao.prepareStatement(sqlSegura)) {

            if (objParametros != null) {
                int index = 1;
                for (Object param : objParametros) {
                    if (param == null)
                        preparedStatement.setNull(index, Types.NULL);
                    else if (param instanceof String)
                        preparedStatement.setString(index, (String) param);
                    else if (param instanceof Integer)
                        preparedStatement.setInt(index, (Integer) param);
                    else if (param instanceof BigDecimal)
                        preparedStatement.setBigDecimal(index, (BigDecimal) param);
                    else if (param instanceof java.util.Date)
                        preparedStatement.setTimestamp(index, new Timestamp(((java.util.Date) param).getTime()));
                    else
                        preparedStatement.setObject(index, param);
                    index++;
                }
            }

            int linhasAfetadasLocal = preparedStatement.executeUpdate();
            System.out.println(getAfetadas(linhasAfetadasLocal));
            return linhasAfetadasLocal;

        } catch (SQLException e) {
            System.err.println("Erro em xDirectiva (PreparedStatement): " + e.getMessage());
            return -1;
        }
    }

    /**
     * Executa consultas SQL e retorna os dados em estrutura de lista.
     * 
     * Mapeia dinamicamente as colunas do resultado para um mapa chave-valor,
     * independente da tabela consultada.
     * 
     * @param interroga Comando SQL SELECT.
     * @return Lista de mapas com os dados encontrados.
     */
    public List<Map<String, Object>> getResultado(String interroga) {
        List<Map<String, Object>> data = new ArrayList<>();

        try (Connection conexao = getLigacao();
                Statement stmt = conexao.createStatement();
                ResultSet rs = stmt.executeQuery(interroga)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                data.add(row);
            }
            return data;
        } catch (SQLException e) {
            System.err.println("Erro em getResultado: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gera uma mensagem legível sobre o resultado da operação SQL.
     * 
     * @param linhasAfetadas Quantidade de registos alterados.
     * @return Texto descritivo do resultado.
     */
    public static String getAfetadas(int linhasAfetadas) {
        if (linhasAfetadas == -1)
            return "Falha crítica na execução do comando SQL.";
        if (linhasAfetadas == 0)
            return "O comando foi executado, mas nenhuma linha foi alterada.";
        return "Sucesso: " + linhasAfetadas + " registos processados com sucesso.";
    }
}
