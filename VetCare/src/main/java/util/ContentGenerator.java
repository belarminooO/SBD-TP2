package util;

import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Interface funcional para geração de conteúdo a partir de um ResultSet.
 */
@FunctionalInterface
public interface ContentGenerator {

  /**
   * Gera o conteúdo formatado.
   * 
   * @param rs        conjunto de resultados da consulta
   * @param writer    objeto de escrita
   * @param tableName nome da tabela processada
   * @throws SQLException em caso de erro no acesso aos dados
   */
  void generate(ResultSet rs, PrintWriter writer, String tableName) throws SQLException;
}
