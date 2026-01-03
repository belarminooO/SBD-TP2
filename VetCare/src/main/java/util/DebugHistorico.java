package util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Removed dependencies on animal.* to avoid compilation issues with Jakarta/Java8 mismatch
public class DebugHistorico {
  public static void main(String[] args) {
    System.out.println("Starting DebugHistorico (Pure JDBC)...");

    try (Connection con = new Configura().getConnection()) {
      // 1. Find the imported animals by Name
      Map<Integer, String> animalsFound = new HashMap<>();

      String findAnimalSQL = "SELECT IDAnimal, Nome FROM Animal WHERE Nome LIKE '%Mittens%' OR Nome LIKE '%Rex%'";
      try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(findAnimalSQL)) {
        while (rs.next()) {
          int id = rs.getInt("IDAnimal");
          String nome = rs.getString("Nome");
          System.out.println("Found Animal in DB: " + nome + " (ID: " + id + ")");
          animalsFound.put(id, nome);
        }
      }

      // Check TipoServico existence
      System.out.println("\n[TipoServico Table Content]:");
      try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM TipoServico")) {
        boolean foundId1 = false;
        while (rs.next()) {
          int id = rs.getInt("IDServico");
          String nome = rs.getString("Nome");
          System.out.println(" - ID: " + id + ", Nome: " + nome);
          if (id == 1)
            foundId1 = true;
        }
        if (!foundId1)
          System.err.println("WARNING: TipoServico with ID 1 (used by Import) NOT FOUND!");
      }

      if (animalsFound.isEmpty()) {
        System.out.println("No animals found matching 'Mittens' or 'Rex'.");
      }

      for (Map.Entry<Integer, String> entry : animalsFound.entrySet()) {
        checkHistory(con, entry.getKey(), entry.getValue());
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void checkHistory(Connection con, int animalId, String name) throws SQLException {
    System.out.println("\n--------------------------------------------------");
    System.out.println("Checking history for " + name + " (ID: " + animalId + ")...");

    // Check 1: Direct Table Query
    String sql = "SELECT IDPrestacao, TipoDiscriminador, DataHora, DetalhesGerais FROM PrestacaoServico WHERE Animal_IDAnimal = ? ORDER BY DataHora DESC";
    int tableCount = 0;
    try (PreparedStatement ps = con.prepareStatement(sql)) {
      ps.setInt(1, animalId);
      try (ResultSet rs = ps.executeQuery()) {
        System.out.println("[PrestacaoServico Table Content]:");
        while (rs.next()) {
          tableCount++;
          System.out.println(" - ID: " + rs.getInt("IDPrestacao") +
              ", Type: " + rs.getString("TipoDiscriminador") +
              ", Date: " + rs.getTimestamp("DataHora") +
              ", Detalhes: " + rs.getString("DetalhesGerais"));
        }
        if (tableCount == 0)
          System.out.println("   No records found in PrestacaoServico.");
      }
    }

    // Check 2: Specific Sub-Tables (Sample check)
    String[] subtypes = { "Cirurgia", "Consulta", "Vacinacao", "Desparasitacao", "ExameFisico", "ResultadoExame",
        "TratamentoTerapeutico" };
    if (tableCount > 0) {
      System.out.println("\n[Sub-Table Verification]:");
      for (String type : subtypes) {
        // Check if any record of this type exists linked to the PrestacaoServico IDs we
        // found?
        // Easier: Join
        String subSql = "SELECT count(*) FROM " + type
            + " T JOIN PrestacaoServico P ON T.IDPrestacao = P.IDPrestacao WHERE P.Animal_IDAnimal = ?";
        try (PreparedStatement ps = con.prepareStatement(subSql)) {
          ps.setInt(1, animalId);
          try (ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getInt(1) > 0) {
              System.out.println("   Found " + rs.getInt(1) + " records in table " + type);
            }
          }
        } catch (SQLException e) {
          System.out.println("   Error checking table " + type + ": " + e.getMessage());
        }
      }
    }

    System.out.println("\n[View 'HistoricoClinico' Content]:");
    String viewSql = "SELECT * FROM HistoricoClinico WHERE IDAnimal = ?";
    try (PreparedStatement ps = con.prepareStatement(viewSql)) {
      ps.setInt(1, animalId);
      try (ResultSet rs = ps.executeQuery()) {
        int viewCount = 0;
        ResultSetMetaData meta = rs.getMetaData();
        while (rs.next()) {
          viewCount++;
          System.out.print(" - View Row " + viewCount + ": ");
      
          for (int i = 1; i <= Math.min(5, meta.getColumnCount()); i++) {
            System.out.print(meta.getColumnName(i) + "=" + rs.getObject(i) + " | ");
          }
          System.out.println();
        }
        if (viewCount == 0)
          System.out.println("   No records returned by View HistoricoClinico.");
      } catch (SQLException e) {
        System.out.println("   Error querying View: " + e.getMessage());
      }
    }
  }
}
