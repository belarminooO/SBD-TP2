package historico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

/**
 * Responsável pela persistência e gestão dos dados do histórico clínico.
 * Implementa o padrão Table-Per-Type (TPT), gerindo a inserção atómica de dados
 * na tabela base e nas respetivas tabelas satélite através de transações.
 */
public class HistoricoDAO {

    /**
     * Persiste um registo de prestação de serviço de forma polimórfica.
     * O processo envolve a inserção na tabela base PrestacaoServico, a recuperação
     * do identificador gerado e a inserção subsequente na tabela específica
     * correspondente ao subtipo.
     * 
     * @param ps Objeto que representa a prestação de serviço.
     * @return O identificador da prestação gerada ou -1 em caso de falha.
     */
    public static int save(PrestacaoServico ps) {
        Connection con = null;
        int idGerado = -1;

        try {
            con = new Configura().getConnection(false);

            String sqlBase = "INSERT INTO PrestacaoServico (DataHora, DetalhesGerais, TipoDiscriminador, Animal_IDAnimal, Agendamento_IDAgendamento, TipoServico_IDServico) VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = con.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS);
            pstmt.setTimestamp(1, ps.getDataHora());
            pstmt.setString(2, ps.getDetalhesGerais());
            pstmt.setString(3, ps.getTipoDiscriminador());
            pstmt.setInt(4, ps.getAnimalId());

            if (ps.getAgendamentoId() != null) {
                pstmt.setInt(5, ps.getAgendamentoId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }

            pstmt.setInt(6, ps.getTipoServicoId());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getInt(1);
            }
            pstmt.close();

            if (idGerado == -1) {
                con.rollback();
                return -1;
            }

            if (ps instanceof Consulta) {
                Consulta c = (Consulta) ps;
                String sqlSub = "INSERT INTO Consulta (IDPrestacao, Motivo, Sintomas, Diagnostico, MedicacaoPrescrita) VALUES (?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setString(2, c.getMotivo());
                pstmt.setString(3, c.getSintomas());
                pstmt.setString(4, c.getDiagnostico());
                pstmt.setString(5, c.getMedicacaoPrescrita());
                pstmt.executeUpdate();
            } else if (ps instanceof Vacinacao) {
                Vacinacao v = (Vacinacao) ps;
                String sqlSub = "INSERT INTO Vacinacao (IDPrestacao, TipoVacina, Fabricante) VALUES (?, ?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setString(2, v.getTipoVacina());
                pstmt.setString(3, v.getFabricante());
                pstmt.executeUpdate();
            } else if (ps instanceof ExameFisico) {
                ExameFisico ef = (ExameFisico) ps;
                String sqlSub = "INSERT INTO ExameFisico (IDPrestacao, Temperatura, Peso, FrequenciaCardiaca, FrequenciaRespiratoria) VALUES (?, ?, ?, ?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setBigDecimal(2, ef.getTemperatura());
                pstmt.setBigDecimal(3, ef.getPeso());
                pstmt.setInt(4, ef.getFrequenciaCardiaca());
                pstmt.setInt(5, ef.getFrequenciaRespiratoria());
                pstmt.executeUpdate();
            } else if (ps instanceof ResultadoExame) {
                ResultadoExame re = (ResultadoExame) ps;
                String sqlSub = "INSERT INTO ResultadoExame (IDPrestacao, TipoExame, ResultadoDetalhado) VALUES (?, ?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setString(2, re.getTipoExame());
                pstmt.setString(3, re.getResultadoDetalhado());
                pstmt.executeUpdate();
            } else if (ps instanceof Desparasitacao) {
                Desparasitacao d = (Desparasitacao) ps;
                String sqlSub = "INSERT INTO Desparasitacao (IDPrestacao, Tipo, ProdutosUtilizados) VALUES (?, ?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setString(2, d.getTipo());
                pstmt.setString(3, d.getProdutosUtilizados());
                pstmt.executeUpdate();
            } else if (ps instanceof Cirurgia) {
                Cirurgia cir = (Cirurgia) ps;
                String sqlSub = "INSERT INTO Cirurgia (IDPrestacao, TipoCirurgia, NotasPosOperatorias) VALUES (?, ?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setString(2, cir.getTipoCirurgia());
                pstmt.setString(3, cir.getNotasPosOperatorias());
                pstmt.executeUpdate();
            } else if (ps instanceof TratamentoTerapeutico) {
                TratamentoTerapeutico tt = (TratamentoTerapeutico) ps;
                String sqlSub = "INSERT INTO TratamentoTerapeutico (IDPrestacao, Descricao) VALUES (?, ?)";
                pstmt = con.prepareStatement(sqlSub);
                pstmt.setInt(1, idGerado);
                pstmt.setString(2, tt.getDescricao());
                pstmt.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            System.err.println("Erro ao gravar histórico clínico: " + e.getMessage());
            try {
                if (con != null)
                    con.rollback();
            } catch (SQLException ex) {

            }
            return -1;
        } finally {
            Configura.close(con);
        }
        return idGerado;
    }

    /**
     * Recupera a lista completa do histórico clínico de um animal específico.
     * Utiliza uma vista SQL que consolida dados polimórficos de forma eficiente,
     * transformando cada registo num mapa de pares chave-valor.
     * 
     * @param animalId Identificador único do animal.
     * @return Lista de mapas contendo os dados do histórico.
     */
    public static List<java.util.Map<String, Object>> getHistoryByAnimal(int animalId) {
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM HistoricoClinico WHERE IDAnimal = ? ORDER BY DataHora DESC";

        try (Connection con = new Configura().getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, animalId);
            try (ResultSet rs = ps.executeQuery()) {
                java.sql.ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();

                while (rs.next()) {
                    java.util.Map<String, Object> row = new java.util.HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(meta.getColumnName(i), rs.getObject(i));
                    }
                    list.add(row);
                }
                logDebug("HistoricoDAO.getHistoryByAnimal(" + animalId + ") View 'HistoricoClinico' returned "
                        + list.size() + " rows.");
            }

            try (PreparedStatement psRaw = con
                    .prepareStatement("SELECT count(*) FROM PrestacaoServico WHERE Animal_IDAnimal = ?")) {
                psRaw.setInt(1, animalId);
                try (ResultSet rsRaw = psRaw.executeQuery()) {
                    if (rsRaw.next()) {
                        logDebug("Raw count in 'PrestacaoServico' for animal " + animalId + ": " + rsRaw.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler histórico clínico: " + e.getMessage());
        }
        return list;
    }

    /**
     * Regista mensagens de depuração na consola.
     * Útil para rastrear a execução de operações complexas como a leitura de
     * vistas.
     * 
     * @param msg Mensagem a ser registada.
     */
    public static void logDebug(String msg) {
        System.out.println("[HistoricoDAO] " + msg);
    }
}
