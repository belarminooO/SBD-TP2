package util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Locale;
import java.util.Objects;

/**
 * Utilitário responsável pela formatação e conversão de dados para
 * apresentação.
 * Providencia métodos para tratamento de datas, números decimais, alinhamento
 * de
 * texto e processamento de metadados de resultados de base de dados.
 */
final public class DataFormatter {

    /** Formato de data utilizado para entradas de dados. */
    final static private String IN_FORMAT_STRING = "d/M/yyyy";

    /** Formato de data utilizado para exibição ao utilizador. */
    final static private String OUT_FORMAT_STRING = "EEE dd MMM yyyy";

    /** Formatador para leitura de datas. */
    final static private DateTimeFormatter IN_FORMATTER = DateTimeFormatter.ofPattern(IN_FORMAT_STRING);

    /** Formatador para exibição de datas configurado para o local de Portugal. */
    final static private DateTimeFormatter OUT_FORMATTER = DateTimeFormatter.ofPattern(OUT_FORMAT_STRING,
            Locale.forLanguageTag("pt-PT"));

    /**
     * Obtém o padrão de formato de entrada de datas.
     * 
     * @return String com o formato de entrada.
     */
    public static String getInFormato() {
        return IN_FORMAT_STRING;
    }

    /**
     * Obtém o padrão de formato de saída de datas.
     * 
     * @return String com o formato de exibição.
     */
    public static String getDateFormat() {
        return OUT_FORMAT_STRING;
    }

    /**
     * Formata um valor monetário ou nota para representação textual.
     * 
     * @param nota O valor decimal a formatar.
     * @return String formatada com duas casas decimais ou indicação de ausência.
     */
    public static String NotaToString(BigDecimal nota) {
        if (Objects.isNull(nota))
            return "  -  ";
        NumberFormat formatter = new DecimalFormat("00.00");
        return formatter.format(nota);
    }

    /**
     * Converte uma data proveniente da base de dados para representação textual.
     * 
     * @param data Objeto de data SQL.
     * @return String formatada para exibição.
     */
    public static String DateToString(java.sql.Date data) {
        if (Objects.isNull(data))
            return "";
        return data.toLocalDate().format(OUT_FORMATTER);
    }

    /**
     * Converte um objeto LocalDate para representação textual.
     * 
     * @param data A data a formatar.
     * @return String formatada para exibição.
     */
    public static String LocalDateToString(LocalDate data) {
        if (Objects.isNull(data))
            return "";
        return data.format(OUT_FORMATTER);
    }

    /**
     * Converte uma representação textual de data para um objeto de data SQL.
     * 
     * @param data String contendo a data no formato de entrada.
     * @return Objeto java.sql.Date ou nulo se a conversão falhar.
     */
    public static java.sql.Date StringToSqlDate(String data) {
        if (data == null || data.isEmpty())
            return null;
        try {
            // Tentar formato padrão (d/M/yyyy)
            LocalDate localDate = LocalDate.parse(data, IN_FORMATTER);
            return java.sql.Date.valueOf(localDate);
        } catch (DateTimeParseException e1) {
            try {
                // Tentar formato ISO (yyyy-MM-dd) que é o padrão dos inputs HTML5
                LocalDate localDate = LocalDate.parse(data);
                return java.sql.Date.valueOf(localDate);
            } catch (DateTimeParseException e2) {
                System.err.println("Erro na conversão de data (formato inválido: " + data + ")");
            }
        }
        return null;
    }

    /**
     * Converte uma data SQL para o formato compacto YYYYMMDD.
     * 
     * @param sqlDate A data a converter.
     * @return String formatada ou vazia se a entrada for nula.
     */
    public static String sqlDateToString(Date sqlDate) {
        if (Objects.isNull(sqlDate)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(sqlDate);
    }

    /**
     * Ajusta o comprimento de uma string preenchendo-a com um determinado caráter.
     * 
     * @param str String original.
     * @param dim Comprimento final pretendido.
     * @param ch  Caráter de preenchimento.
     * @return String ajustada ao comprimento especificado.
     */
    public static String fill(String str, int dim, String ch) {
        if (str != null) {
            while (dim > str.length()) {
                str = str + ch;
            }
        }
        return str;
    }

    /**
     * Traduz uma sigla de género para a respetiva designação por extenso.
     * 
     * @param letra Sigla do género (M, F, X).
     * @return Designação por extenso em português.
     */
    public static String obterGenero(String letra) {
        if (letra == null || letra.trim().isEmpty()) {
            return "Indefinido";
        }
        String inputNormalizado = letra.trim().toUpperCase();
        char primeiraLetra = inputNormalizado.charAt(0);

        switch (primeiraLetra) {
            case 'M':
                return "Masculino";
            case 'F':
                return "Feminino";
            case 'X':
                return "Desconhecido";
            default:
                return "Não Especificado";
        }
    }

    /**
     * Normaliza e processa um nome para fins de exibição padronizada.
     * 
     * @param nome O nome original.
     * @return Nome processado e limitado em comprimento.
     */
    public static String obterNome(String nome) {
        if (nome == null || nome.isEmpty()) {
            return "Sem Nome";
        }
        return util.Name.shorten(Name.normalize(nome), 60);
    }

    /**
     * Alinha o texto dentro de um espaço definido com preenchimento lateral.
     * 
     * @param texto         Texto original.
     * @param larguraMaxima Comprimento total do espaço de saída.
     * @param alinhamento   Direção do alinhamento (ESQUERDA, DIREITA, CENTRO).
     * @return String alinhada ou truncada se exceder a largura.
     */
    private static String padAll(String texto, int larguraMaxima, String alinhamento) {
        if (texto == null) {
            texto = "";
        }
        texto = texto.trim();

        if (texto.length() > larguraMaxima) {
            return texto.substring(0, larguraMaxima);
        }

        int espacosTotais = larguraMaxima - texto.length();
        int espacosEsquerda = 0;
        int espacosDireita = 0;
        String tipo = alinhamento.toUpperCase();

        if (tipo.equals("ESQUERDA")) {
            espacosDireita = espacosTotais;
        } else if (tipo.equals("DIREITA")) {
            espacosEsquerda = espacosTotais;
        } else if (tipo.equals("CENTRO")) {
            espacosEsquerda = espacosTotais / 2;
            espacosDireita = espacosTotais - espacosEsquerda;
        } else {
            espacosDireita = espacosTotais;
        }

        return " ".repeat(espacosEsquerda) + texto + " ".repeat(espacosDireita);
    }

    /**
     * Alinha o texto ao centro dentro de uma largura definida.
     * 
     * @param texto         Texto original.
     * @param larguraMaxima Comprimento total pretendido.
     * @return Texto alinhado ao centro.
     */
    public static String padCenter(String texto, int larguraMaxima) {
        return padAll(texto, larguraMaxima, "CENTRO");
    }

    /**
     * Alinha o texto à esquerda dentro de uma largura definida.
     * 
     * @param texto         Texto original.
     * @param larguraMaxima Comprimento total pretendido.
     * @return Texto alinhado à esquerda.
     */
    public static String padLeft(String texto, int larguraMaxima) {
        return padAll(texto, larguraMaxima, "ESQUERDA");
    }

    /**
     * Alinha o texto à direita dentro de uma largura definida.
     * 
     * @param texto         Texto original.
     * @param larguraMaxima Comprimento total pretendido.
     * @return Texto alinhado à direita.
     */
    public static String padRight(String texto, int larguraMaxima) {
        return padAll(texto, larguraMaxima, "DIREITA");
    }

    /**
     * Calcula a largura ideal para exibição de uma coluna com base nos metadados.
     * 
     * @param metaData Metadados do conjunto de resultados.
     * @param indice   Índice da coluna.
     * @return Comprimento em caracteres para a representação visual.
     */
    public static int formatSize(ResultSetMetaData metaData, int indice) {
        final int COL_DIM_MIN = 11;
        final int COL_DIM_MAX = 200;
        int size = COL_DIM_MIN;

        try {
            String coluna = metaData.getColumnName(indice).trim().toLowerCase();
            if (coluna.equals("genero") || coluna.equals("sexo")) {
                size = 10;
            } else {
                size = metaData.getColumnDisplaySize(indice) + 2;
                size = size > COL_DIM_MAX ? COL_DIM_MAX : size;
                size = size < COL_DIM_MIN ? COL_DIM_MIN : size;
                if (coluna.length() > size) {
                    size = coluna.length();
                }
            }
        } catch (SQLException ignore) {
        }
        return size;
    }

    /**
     * Formata o valor de uma coluna para representação tabular.
     * Analisa o tipo de dado e aplica a formatação adequada (data, número, blob,
     * etc).
     * 
     * @param rs       Conjunto de resultados posicionado.
     * @param metaData Metadados do conjunto de resultados.
     * @param indice   Índice da coluna.
     * @return Valor formatado com alinhamento adequado.
     * @throws SQLException Em caso de erro no acesso aos dados.
     */
    public static String formatColumn(ResultSet rs, ResultSetMetaData metaData, int indice) throws SQLException {
        final int COL_DIM_MAX = 200;
        String coluna = metaData.getColumnName(indice);
        int tipo = metaData.getColumnType(indice);
        int size = formatSize(metaData, indice);
        size = (indice == metaData.getColumnCount()) ? size : size - 1;

        if (isBlob(tipo)) {
            String displayValue = getBlobHex(rs, indice);
            if (displayValue.length() > COL_DIM_MAX) {
                return displayValue.substring(0, COL_DIM_MAX - 3) + "...";
            }
            return displayValue;
        } else {
            String value = rs.getString(indice);
            if (value == null) {
                return DataFormatter.padCenter("-", size);
            }
            if (isNumeric(tipo)) {
                return DataFormatter.padRight(DataFormatter.formatDecimal(value), size - 1) + " ";
            }
            if (isDateOrTime(tipo)) {
                return DataFormatter.padCenter(DataFormatter.formatDate(value), size);
            }
            if (coluna.equalsIgnoreCase("genero") || coluna.equalsIgnoreCase("sexo")) {
                return DataFormatter.padCenter(DataFormatter.obterGenero(value), size);
            }
            if (coluna.equalsIgnoreCase("nome")) {
                return DataFormatter.padLeft(DataFormatter.obterNome(value), size);
            }
            return DataFormatter.padLeft(value, size);
        }
    }

    /**
     * Formata um valor decimal SQL para o padrão de representação europeu
     * (vírgula).
     * 
     * @param sqlDecimalString String original com ponto decimal.
     * @return String formatada com vírgula e alinhamento básico.
     */
    public static String formatDecimal(String sqlDecimalString) {
        if (sqlDecimalString == null || sqlDecimalString.trim().isEmpty()) {
            return "";
        }
        String numero = "00000" + sqlDecimalString.replace(".", ",");
        return numero.substring(numero.length() - 5);
    }

    /**
     * Converte uma data ou timestamp ISO SQL para o formato português.
     * 
     * @param sqlDateTimeString Representação textual da data SQL.
     * @return Data formatada ou valor original em caso de erro.
     */
    public static String formatDate(String sqlDateTimeString) {
        if (sqlDateTimeString == null || sqlDateTimeString.trim().isEmpty()) {
            return "";
        }
        DateTimeFormatter formatoPortugues = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(sqlDateTimeString,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.S][.SS][.SSS]"));
            return dateTime.format(formatoPortugues);
        } catch (DateTimeParseException e1) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(sqlDateTimeString);
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException e2) {
                return sqlDateTimeString;
            }
        }
    }

    /**
     * Verifica se um tipo SQL é numérico.
     * 
     * @param sqlType Código do tipo java.sql.Types.
     * @return Verdadeiro se for numérico.
     */
    public static boolean isNumeric(int sqlType) {
        return sqlType == Types.TINYINT || sqlType == Types.SMALLINT ||
                sqlType == Types.INTEGER || sqlType == Types.BIGINT ||
                sqlType == Types.FLOAT || sqlType == Types.DOUBLE ||
                sqlType == Types.REAL || sqlType == Types.NUMERIC ||
                sqlType == Types.DECIMAL || sqlType == Types.BIT || sqlType == Types.BOOLEAN;
    }

    /**
     * Verifica se um tipo SQL é compatível com BLOB.
     * 
     * @param sqlType Código do tipo java.sql.Types.
     * @return Verdadeiro se for binário longo.
     */
    public static boolean isBlob(int sqlType) {
        return sqlType == Types.VARBINARY || sqlType == Types.LONGVARBINARY || sqlType == Types.BLOB;
    }

    /**
     * Verifica se um tipo SQL representa data ou tempo.
     * 
     * @param sqlType Código do tipo java.sql.Types.
     * @return Verdadeiro se for temporal.
     */
    public static boolean isDateOrTime(int sqlType) {
        return sqlType == Types.DATE || sqlType == Types.TIME ||
                sqlType == Types.TIMESTAMP || sqlType == Types.TIME_WITH_TIMEZONE ||
                sqlType == Types.TIMESTAMP_WITH_TIMEZONE;
    }

    /**
     * Converte um campo BLOB para uma representação Base64.
     * 
     * @param rs          Conjunto de resultados posicionado.
     * @param columnIndex Índice da coluna.
     * @return String codificada ou nula.
     * @throws SQLException Em caso de erro de acesso.
     */
    public static String blobToBase64(ResultSet rs, int columnIndex) throws SQLException {
        try (InputStream is = rs.getBinaryStream(columnIndex)) {
            if (is == null || rs.wasNull()) {
                return null;
            }
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            System.err.println("Erro de E/S ao processar BLOB: " + e.getMessage());
        }
        return "";
    }

    /**
     * Converte um campo BLOB para uma representação hexadecimal.
     * 
     * @param rs          Conjunto de resultados posicionado.
     * @param columnIndex Índice da coluna.
     * @return String hexadecimal ou nula.
     * @throws SQLException Em caso de erro de acesso.
     */
    public static String blobToHexString(ResultSet rs, int columnIndex) throws SQLException {
        try (InputStream is = rs.getBinaryStream(columnIndex)) {
            if (is == null || rs.wasNull()) {
                return null;
            }
            StringBuilder hexString = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    hexString.append(String.format("%02X", buffer[i]));
                }
            }
            return hexString.toString();
        } catch (IOException e) {
            System.err.println("Erro de E/S ao processar BLOB: " + e.getMessage());
        }
        return "";
    }

    /**
     * Obtém a representação hexadecimal formatada para comandos SQL.
     * Converte o binário para o formato específico do SGBD (MySQL ou SQL Server).
     * 
     * @param rs          Conjunto de resultados posicionado.
     * @param columnIndex Índice da coluna.
     * @return Comando SQL formatado (ex: UNHEX ou 0x) ou NULL.
     */
    public static String getBlobHex(ResultSet rs, int columnIndex) {
        Configura cfg = new Configura();
        try {
            String hexValue = blobToHexString(rs, columnIndex);
            if (hexValue != null) {
                if (cfg.isMySQL()) {
                    return "UNHEX('" + hexValue + "')";
                } else if (cfg.isSQLServer()) {
                    return "0x" + hexValue;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro SQL ao processar BLOB: " + e.getMessage());
        }
        return "NULL";
    }

    /**
     * Gera uma cláusula SQL LIKE com tratamento de nulos.
     * 
     * @param atributo Nome do campo.
     * @param valor    Valor de pesquisa.
     * @return Fragmento de cláusula SQL WHERE.
     */
    public static String like(String atributo, String valor) {
        if (valor == null || valor.equals("null")) {
            return " " + atributo + " IS NULL";
        } else if (!valor.isEmpty()) {
            return " " + atributo + " like '" + valor + "'";
        }
        return "1 = 1";
    }

    /**
     * Gera uma cláusula SQL de igualdade com tratamento de nulos.
     * 
     * @param atributo Nome do campo.
     * @param valor    Valor de comparação.
     * @return Fragmento de cláusula SQL WHERE.
     */
    public static String igual(String atributo, String valor) {
        if (valor == null || valor.equals("null") || valor.isEmpty()) {
            return " " + atributo + " IS NULL";
        }
        return " " + atributo + " = '" + valor + "'";
    }

    /**
     * Substitui a última ocorrência de um termo numa string.
     * 
     * @param original    Texto base.
     * @param target      Termo a localizar.
     * @param replacement Texto de substituição.
     * @return String resultante.
     */
    public static String replaceLast(String original, String target, String replacement) {
        int lastIndex = original.lastIndexOf(target);
        if (lastIndex == -1) {
            return original;
        }
        return original.substring(0, lastIndex) + replacement + original.substring(lastIndex + target.length());
    }
}