package historico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.Configura;

public class HistoricoDAO {

    public static int save(PrestacaoServico ps) {
        Connection con = null;
        int idGerado = -1;
        
        try {
            con = new Configura().getConnection(false);
            
            // 1. Insert Supertype
            String sqlBase = "INSERT INTO PrestacaoServico (DataHora, DetalhesGerais, TipoDiscriminador, Animal_IDAnimal, Agendamento_IDAgendamento, TipoServico_IDServico) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS);
            pstmt.setTimestamp(1, ps.getDataHora());
            pstmt.setString(2, ps.getDetalhesGerais());
            pstmt.setString(3, ps.getTipoDiscriminador());
            pstmt.setInt(4, ps.getAnimalId());
            if(ps.getAgendamentoId()!=null) pstmt.setInt(5, ps.getAgendamentoId()); else pstmt.setNull(5, java.sql.Types.INTEGER);
            pstmt.setInt(6, ps.getTipoServicoId());
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) idGerado = rs.getInt(1);
            pstmt.close();
            
            if (idGerado == -1) { con.rollback(); return -1; }
            
            // 2. Insert Subtype
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
            System.err.println("Erro ao gravar historico: " + e.getMessage());
            try { if(con!=null) con.rollback(); } catch(SQLException ex) {}
            return -1;
        } finally {
            Configura.close(con);
        }
        return idGerado;
    }

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
            }
        } catch (SQLException e) {
            System.err.println("Erro ao ler historico por animal: " + e.getMessage());
        }
        return list;
    }

}
