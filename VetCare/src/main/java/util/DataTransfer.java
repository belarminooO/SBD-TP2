package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import animal.Animal;
import animal.AnimalDAO;
import agendamento.AgendamentoDAO;
import clinica.TipoServico;
import historico.HistoricoDAO;
import historico.PrestacaoServico;
import cliente.Cliente;
import cliente.ClienteDAO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

/**
 * Componente responsável pela transferência e interoperabilidade de dados entre
 * o sistema VetCare e formatos externos.
 *
 * Providencia funcionalidades de exportação e importação em diversos formatos,
 * incluindo SQL, CSV, JSON, XML, HTML, TXT e PDF.
 *
 * Esta classe utiliza streams para processamento eficiente de grandes volumes
 * de dados,
 * suportando tanto ficheiros locais como streams de entrada/saída arbitrários.
 *
 * @author VetCare Development Team
 * @version 2.0
 */
public final class DataTransfer {

    /** Caminho base para os recursos da aplicação. */
    private static final String path = new Configura().getRealPath();

    /** Caminho absoluto para o diretório de importação. */
    public static final String pathImport = path + "import/";

    /** Caminho absoluto para o diretório de exportação. */
    public static final String pathExport = path + "export/";

    /** Caminho absoluto para diretório de fontes. */
    private static final String pathFonts = path + "fonts/";

    /** Delimitador padrão para ficheiros CSV. */
    private static final String CSV_DELIMITER = ";";

    /**
     * Construtor privado para impedir a instanciação da classe utilitária.
     * 
     * @throws UnsupportedOperationException sempre que invocado
     */
    private DataTransfer() {
        throw new UnsupportedOperationException("Classe utilitária não instanciável.");
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro SQL.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */
    public static boolean exportToSql(String tableName) {
        return processExport(tableName, "sql", DataTransfer::gerarSql);
    }

    /**
     * Exporta os dados de uma tabela para SQL utilizando um PrintWriter fornecido.
     * 
     * @param tableName nome da tabela a exportar
     * @param writer    objeto PrintWriter para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     */
    public static boolean exportToSql(String tableName, PrintWriter writer) {
        return processExport(tableName, "sql", DataTransfer::gerarSql, writer);
    }

    /**
     * Exporta os dados de uma tabela para SQL utilizando um OutputStream.
     * 
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     * @throws Exception em caso de erro de I/O
     */
    public static boolean exportToSql(String tableName, OutputStream os) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            return processExport(tableName, "sql", DataTransfer::gerarSql, writer);
        }
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro CSV.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */
    public static boolean exportToCsv(String tableName) {
        return processExport(tableName, "csv", DataTransfer::gerarCsv);
    }

    /**
     * Exporta os dados de uma tabela para CSV utilizando um PrintWriter fornecido.
     *
     * @param tableName nome da tabela a exportar
     * @param writer    objeto PrintWriter para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     */
    public static boolean exportToCsv(String tableName, PrintWriter writer) {
        return processExport(tableName, "csv", DataTransfer::gerarCsv, writer);
    }

    /**
     * Exporta os dados de uma tabela para CSV utilizando um OutputStream.
     *
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     * @throws Exception em caso de erro de I/O
     */
    public static boolean exportToCsv(String tableName, OutputStream os) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            return processExport(tableName, "csv", DataTransfer::gerarCsv, writer);
        }
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro XML.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToXml(String tableName) {
        return processExport(tableName, "xml", DataTransfer::gerarXml);
    }

    /**
     * Exporta os dados de uma tabela para XML utilizando um PrintWriter fornecido.
     * 
     * @param tableName nome da tabela a exporta
     * @param writer    objeto PrintWriter para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToXml(String tableName, PrintWriter writer) {
        return processExport(tableName, "xml", DataTransfer::gerarXml, writer);
    }

    /**
     * Exporta os dados de uma tabela para XML utilizando um OutputStream.
     * 
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     * @throws Exception em caso de erro de I/O
     */

    public static boolean exportToXml(String tableName, OutputStream os) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            return processExport(tableName, "xml", DataTransfer::gerarXml, writer);
        }
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro JSON.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToJson(String tableName) {
        return processExport(tableName, "json", DataTransfer::gerarJson);
    }

    /**
     * Exporta os dados de uma tabela para JSON utilizando um PrintWriter fornecido.
     * 
     * @param tableName nome da tabela a exportar
     * @param writer    objeto PrintWriter para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToJson(String tableName, PrintWriter writer) {
        return processExport(tableName, "json", DataTransfer::gerarJson, writer);
    }

    /**
     * Exporta os dados de uma tabela para JSON utilizando um OutputStream.
     * 
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     * @throws Exception em caso de erro de I/O
     */

    public static boolean exportToJson(String tableName, OutputStream os) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            return processExport(tableName, "json", DataTransfer::gerarJson, writer);
        }
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro HTML.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToHtml(String tableName) {
        return processExport(tableName, "html", DataTransfer::gerarHtml);
    }

    /**
     * Exporta os dados de uma tabela para HTML utilizando um PrintWriter fornecido.
     * 
     * @param tableName nome da tabela a exportar
     * @param writer    objeto PrintWriter para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToHtml(String tableName, PrintWriter writer) {
        return processExport(tableName, "html", DataTransfer::gerarHtml, writer);
    }

    /**
     * Exporta os dados de uma tabela para HTML utilizando um OutputStream.
     * 
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     * @throws Exception em caso de erro de I/O
     */

    public static boolean exportToHtml(String tableName, OutputStream os) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            return processExport(tableName, "html", DataTransfer::gerarHtml, writer);
        }
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro de texto formatado.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToTxt(String tableName) {
        return processExport(tableName, "txt", DataTransfer::gerarTxt);
    }

    /**
     * Exporta os dados de uma tabela para texto formatado utilizando um PrintWriter
     * fornecido.
     * 
     * @param tableName nome da tabela a exportar
     * @param writer    objeto PrintWriter para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToTxt(String tableName, PrintWriter writer) {
        return processExport(tableName, "txt", DataTransfer::gerarTxt, writer);
    }

    /**
     * Exporta os dados de uma tabela para texto formatado utilizando um
     * OutputStream.
     * 
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita
     * @return true se a exportação for bem-sucedida, false caso contrário
     * @throws Exception em caso de erro de I/O
     */

    public static boolean exportToTxt(String tableName, OutputStream os) throws Exception {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            return processExport(tableName, "txt", DataTransfer::gerarTxt, writer);
        }
    }

    /**
     * Apresenta os dados de uma tabela na consola do sistema.
     * 
     * @param tableName nome da tabela a apresentar
     * @return true se a apresentação for bem-sucedida, false caso contrário
     */

    public static boolean apresentar(String tableName) {
        return processDisplay(tableName, DataTransfer::gerarTxt);
    }

    /**
     * Obtém os dados de uma tabela formatados em HTML.
     * 
     * @param tableName nome da tabela
     * @return string contendo o HTML gerado
     */

    public static String obterHtml(String tableName) {
        if (tableName == null || tableName.isEmpty())
            return "<p style='" +
                    "display: flex; " +
                    "align-items: center; " +
                    "padding: 15px 20px; " +
                    "margin-top: 25px; " +
                    "margin-bottom: 25px; " +
                    "border: 2px solid #ffcc00; " +
                    "border-left: 8px solid #ffcc00; " +
                    "border-radius: 6px; " +
                    "background-color: #fffde7; " +
                    "color: #cc0000; " +
                    "font-size: 1.1em; " +
                    "font-weight: bold;'>" +
                    "<span style='font-size: 1.8em; margin-right: 15px; color: #ffaa00; line-height: 1;'></span>" +
                    "Tem de indicar o nome da tabela/vista no parâmetro **'nome_tabela'**!" +
                    "</p>";
        return processGenerate(tableName, DataTransfer::gerarHtml);
    }

    /**
     * Obtém os dados de uma tabela formatados em texto.
     * 
     * @param tableName nome da tabela
     * @return string contendo o texto formatado
     */

    public static String obterTxt(String tableName) {
        return processGenerate(tableName, DataTransfer::gerarTxt);
    }

    /**
     * Importa dados SQL de um ficheiro.
     * 
     * @param tableName nome da tabela de destino
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromSql(String tableName) {

        String fileName = tableName + ".sql";
        String filePath = DataTransfer.pathImport + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            System.err.println(" ERRO: Ficheiro SQL não encontrado no caminho: " + filePath);
            return false;
        }

        try (InputStream stream = new FileInputStream(file)) {
            return importFromSql(tableName, stream);

        } catch (FileNotFoundException e) {
            System.err.println(" ERRO de Ficheiro (FNF) ao importar SQL: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println(" ERRO de I/O ao fechar o stream para SQL: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println(" ERRO geral durante a importação SQL (por ficheiro): " + e.getMessage());
            return false;
        }
    }

    /**
     * Importa dados SQL a partir de um InputStream.
     * 
     * @param tableName nome da tabela de destino
     * @param stream    fluxo de dados SQL
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromSql(String tableName, InputStream stream) {
        System.out.println(" Iniciando importação SQL para a tabela " + tableName + ".");
        Connection con = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            con = new Configura().getConnection();
            con.setAutoCommit(false);

            try (Statement stmt = con.createStatement()) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty() && !line.trim().startsWith("--")) {
                        stmt.addBatch(line);
                    }
                }

                stmt.executeBatch();
                con.commit();
                System.out.println(" Importação de SQL concluída.");
                return true;
            }

        } catch (SQLException e) {
            System.err.println(" ERRO SQL na importação SQL: " + e.getMessage());
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao fazer rollback: " + ex.getMessage());
                }
            }
            return false;
        } catch (IOException e) {
            System.err.println(" ERRO IO na importação SQL: " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Importa dados CSV a partir de um InputStream.
     * 
     * @param tableName nome da tabela de destino
     * @param stream    fluxo de dados CSV
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromCsv(String tableName, InputStream stream) {
        System.out.println(" Iniciando importação CSV para a tabela " + tableName + " via STREAM.");

        List<String[]> dataRows = new ArrayList<>();
        String[] columns = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine != null) {
                columns = headerLine.split(CSV_DELIMITER);
            } else {
                System.out.println(" Aviso: Stream CSV está vazio ou sem cabeçalho válido.");
                return false;
            }

            String dataLine;
            while ((dataLine = reader.readLine()) != null) {

                if (dataLine.trim().isEmpty()) {
                    continue;
                }

                String[] rawRow = dataLine.split(CSV_DELIMITER, -1);
                String[] processedRow = new String[columns.length];

                for (int i = 0; i < columns.length; i++) {
                    String cellValue = (i < rawRow.length) ? rawRow[i] : "";

                    if (cellValue.trim().isEmpty()) {
                        processedRow[i] = null;
                    } else {
                        processedRow[i] = cellValue.trim();
                    }
                }

                dataRows.add(processedRow);
            }

        } catch (IOException e) {
            System.out.println(" Erro de I/O ao ler o stream CSV.");
            System.err.println("Detalhes: " + e.getMessage());
            return false;
        }

        if (columns != null && !dataRows.isEmpty()) {
            if (executeBatchInserts(tableName, columns, dataRows)) {
                System.out.println(" Sucesso: Foram importadas " + dataRows.size() + " linhas para a tabela '"
                        + tableName + "' via stream.");
                return true;
            }
        } else {
            System.out.println(" Aviso: Stream CSV processado, mas não continha dados válidos.");
        }
        return false;
    }

    /**
     * Importa dados de um ficheiro CSV.
     * 
     * @param tableName nome da tabela de destino
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromCsv(String tableName) {
        String inputFileName = pathImport + tableName + ".csv";
        File inputFile = new File(inputFileName);

        if (!inputFile.exists()) {
            System.out.println(" Aviso: Ficheiro não encontrado: '" + inputFileName + "'.");
            return false;
        }

        try (InputStream stream = new FileInputStream(inputFile)) {
            System.out.println(" Chamando o método stream para importar o ficheiro CSV: " + inputFileName);
            return importFromCsv(tableName, stream);

        } catch (FileNotFoundException e) {
            System.err.println(" Erro de Ficheiro (FNF) ao abrir CSV: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println(" Erro de I/O ao fechar o stream para CSV: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean importFromJson(String tableName, InputStream stream) {
        System.out.println(" Iniciando importação JSON para a tabela " + tableName + " via STREAM.");

        String jsonString = "";
        try (Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name()).useDelimiter("\\A")) {
            jsonString = scanner.hasNext() ? scanner.next().trim() : "";
        } catch (Exception e) {
            System.out.println(" Erro de I/O ao ler o stream JSON.");
            System.err.println("Exception: " + e.getMessage());
            return false;
        }

        if (jsonString.isEmpty()) {
            System.out.println(" Aviso: O stream JSON está vazio.");
            return false;
        }

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException e) {
            System.out.println(
                    " Erro ao processar o stream JSON. Verifique a sintaxe (deve ser um array de objetos JSON).");
            System.err.println("JSONException: " + e.getMessage());
            return false;
        }

        if (jsonArray.length() == 0) {
            System.out.println(" Aviso: Não foram encontrados objetos (registos) válidos no stream JSON.");
            return false;
        }

        JSONObject firstObject = null;
        try {
            firstObject = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            System.out.println(" Erro ao processar o stream JSON na dedução das colunas.");
            System.err.println("JSONException: " + e.getMessage());
            return false;
        }

        Iterator<String> keys = firstObject.keys();

        List<String> columnList = new ArrayList<>();
        while (keys.hasNext()) {
            columnList.add(keys.next());
        }

        String[] columnNames = columnList.toArray(new String[0]);

        if (columnNames.length == 0) {
            System.out.println(" Aviso: O primeiro objeto JSON está vazio. Não foi possível deduzir as colunas.");
            return false;
        }

        List<String[]> dataRows = new ArrayList<>();
        System.out.println(" Colunas deduzidas (" + columnNames.length + "): " + String.join(", ", columnNames));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] row = new String[columnNames.length];

            for (int j = 0; j < columnNames.length; j++) {
                String key = columnNames[j];
                Object value = obj.opt(key);

                if (value == null || value.equals(JSONObject.NULL)) {
                    row[j] = null;
                } else {
                    row[j] = value.toString();
                }
            }
            dataRows.add(row);
        }

        System.out
                .println(" Preparando " + dataRows.size() + " registos para inserção na tabela '" + tableName + "'...");
        if (executeBatchInserts(tableName, columnNames, dataRows)) {
            System.out.println(
                    " Sucesso: Foram importadas " + dataRows.size() + " linhas para a tabela '" + tableName + "'.");
            return true;
        }

        return false;
    }

    /**
     * Importa dados de um ficheiro JSON.
     * 
     * @param tableName nome da tabela de destino
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromJson(String tableName) {
        String fileName = tableName + ".json";
        String filePath = DataTransfer.pathImport + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(" Aviso: Ficheiro não encontrado: '" + filePath + "'.");
            return false;
        }

        try (InputStream stream = new FileInputStream(file)) {
            System.out.println(" Chamando o método stream para importar o ficheiro: " + fileName);
            return importFromJson(tableName, stream);

        } catch (FileNotFoundException e) {
            System.err.println(" ERRO de Ficheiro (FNF) ao importar JSON: " + e.getMessage());
            return false;
        } catch (IOException e) {
            System.err.println(" ERRO de I/O ao fechar o stream para JSON: " + e.getMessage());
            return false;
        }
    }

    /**
     * Importa dados XML a partir de um InputStream.
     * 
     * @param tableName nome da tabela de destino
     * @param stream    fluxo de dados XML
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromXml(String tableName, InputStream stream) {
        System.out.println(" Iniciando importação XML para a tabela " + tableName + " via STREAM.");

        String rowTag = tableName.substring(0, 1).toUpperCase() + tableName.substring(1).toLowerCase();

        List<String> columnNames = new ArrayList<>();
        List<String[]> dataRows = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList rowNodes = doc.getElementsByTagName(rowTag);

            if (rowNodes.getLength() == 0) {
                System.out.println(" Aviso: Não foram encontrados elementos de dados ('<" + rowTag
                        + ">') no ficheiro XML (Stream).");
                return false;
            }

            Element firstRow = (Element) rowNodes.item(0);
            NodeList columnNodes = firstRow.getChildNodes();

            for (int i = 0; i < columnNodes.getLength(); i++) {
                Node node = columnNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    columnNames.add(node.getNodeName());
                }
            }

            String[] columns = columnNames.toArray(new String[0]);

            for (int i = 0; i < rowNodes.getLength(); i++) {
                Element rowElement = (Element) rowNodes.item(i);
                String[] row = new String[columns.length];

                for (int j = 0; j < columns.length; j++) {
                    String colName = columns[j];
                    NodeList valueNodeList = rowElement.getElementsByTagName(colName);

                    if (valueNodeList.getLength() > 0) {
                        Element valueElement = (Element) valueNodeList.item(0);
                        String nilAttribute = valueElement.getAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                                "nil");

                        if (nilAttribute != null && nilAttribute.equalsIgnoreCase("true")) {
                            row[j] = null;
                        } else {
                            String textContent = valueElement.getTextContent();

                            if (textContent.trim().isEmpty() && valueElement.getChildNodes().getLength() == 0) {
                                row[j] = "";
                            } else {
                                row[j] = textContent;
                            }
                        }
                    } else {
                        row[j] = null;
                    }
                }
                dataRows.add(row);
            }

            if (!dataRows.isEmpty()) {
                if (executeBatchInserts(tableName, columns, dataRows)) {
                    System.out.println(" Sucesso: Foram importadas " + dataRows.size() + " linhas para a tabela '"
                            + tableName + "' (via Stream).");
                    return true;
                }
            } else {
                System.out.println(" Aviso: XML lido via Stream, mas não foram extraídas linhas de dados válidas.");
            }
        } catch (ParserConfigurationException | SAXException e) {
            System.out.println(" Erro de Configuração/Parsing XML: O ficheiro pode estar malformado.");
            System.err.println("Detalhes: " + e.getMessage());
        } catch (IOException e) {
            System.out.println(" Erro de I/O no Stream: Ocorreu um erro durante a leitura do fluxo de dados.");
            System.err.println("Detalhes: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(" Erro grave e inesperado durante a importação XML por stream.");
            System.err.println("Detalhes: " + e.getMessage());
        }
        return false;
    }

    /**
     * Importa dados de um ficheiro XML.
     * 
     * @param tableName nome da tabela de destino
     * @return true se a importação for bem-sucedida, false caso contrário
     */

    public static boolean importFromXml(String tableName) {
        String fileName = tableName + ".xml";
        String filePath = DataTransfer.pathImport + fileName;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println(" Aviso: Ficheiro XML não encontrado: '" + filePath + "'.");
            return false;
        }

        try (InputStream stream = new FileInputStream(file)) {
            System.out.println(" Chamando o método stream para importar o ficheiro: " + fileName);
            return importFromXml(tableName, stream);

        } catch (IOException e) {
            System.out.println(
                    " Erro de I/O de Ficheiro ao abrir o stream: Não foi possível ler o ficheiro '" + filePath + "'.");
            System.err.println("Detalhes: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gere a exportação de dados para um ficheiro físico no formato especificado.
     * 
     * @param tableName nome da tabela
     * @param format    extensão do ficheiro
     * @param generator lógica de geração de conteúdo
     * @return true se a exportação for concluída sem erros
     */

    private static boolean processExport(String tableName, String format, ContentGenerator generator) {
        final String outputFileName = pathExport + tableName + "." + format;
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)))) {
            return processExport(tableName, format, generator, writer);
        } catch (IOException e) {
            System.err.println("Erro de E/S na exportação da tabela '" + tableName + "'.");
        }
        return false;
    }

    /**
     * Processa a exportação utilizando um PrintWriter fornecido.
     * 
     * @param tableName nome da tabela
     * @param format    formato de saída
     * @param generator lógica de geração
     * @param writer    objeto de escrita destino
     * @return true se a operação for concluída
     */

    private static boolean processExport(String tableName, String format, ContentGenerator generator,
            PrintWriter writer) {
        Configura configurador = new Configura();
        String word = configurador.isSQLServer() ? "TOP" : "LIMIT";
        String limite = (format.equals("pdf") || format.equals("txt") ? " " + word + " 900" : "");
        try (Connection con = configurador.getConnection();
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery("SELECT * FROM " + tableName + limite)) {
            generator.generate(rs, writer, tableName);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro na exportação da tabela '" + tableName + "'.");
        }
        return false;
    }

    /**
     * Direciona a visualização dos dados diretamente para a consola do sistema.
     * 
     * @param tableName nome da tabela
     * @param generator lógica de geração
     * @return true se a visualização for processada
     */

    private static boolean processDisplay(String tableName, ContentGenerator generator) {
        Configura configurador = new Configura();
        PrintWriter writer = new PrintWriter(System.out, true);
        try (Connection con = configurador.getConnection();
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery("SELECT * FROM " + tableName)) {
            generator.generate(rs, writer, tableName);
            return true;
        } catch (SQLException e) {
            writer.println("Erro ao consultar a tabela '" + tableName + "'.");
        }
        return false;
    }

    /**
     * Gera o conteúdo formatado e devolve-o numa String.
     * 
     * @param tableName nome da tabela
     * @param generator lógica de geração
     * @return cadeia de caracteres com o conteúdo formatado
     */

    private static String processGenerate(String tableName, ContentGenerator generator) {
        Configura configurador = new Configura();
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw, true);
                Connection con = configurador.getConnection();
                Statement stm = con.createStatement();
                ResultSet rs = stm.executeQuery("SELECT * FROM " + tableName)) {
            generator.generate(rs, pw, tableName);
            return sw.toString();
        } catch (SQLException e) {
            return "Erro ao obter dados da tabela '" + tableName + "'.";
        }
    }

    /**
     * Executa a inserção em lote dos dados na base de dados.
     * 
     * @param tableName tabela de destino
     * @param columns   conjunto de nomes das colunas
     * @param dataRows  lista com os dados a inserir
     * @return true se a importação em lote for concluída com sucesso
     */

    private static boolean executeBatchInserts(String tableName, String[] columns, List<String[]> dataRows) {
        if (columns.length == 0 || dataRows.isEmpty())
            return false;

        Configura configurador = new Configura();
        int rowsAffected = 0;

        StringBuilder sqlPrefixBuilder = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < columns.length; i++) {
            sqlPrefixBuilder.append(columns[i].trim());
            if (i < columns.length - 1)
                sqlPrefixBuilder.append(", ");
        }
        sqlPrefixBuilder.append(") VALUES (");
        final String sqlPrefix = sqlPrefixBuilder.toString();

        try (Connection con = configurador.getConnection(false);
                Statement stm = con.createStatement()) {
            for (String[] row : dataRows) {
                if (row.length != columns.length)
                    continue;
                StringBuilder rowValues = new StringBuilder();
                for (int i = 0; i < columns.length; i++) {
                    String value = row[i];
                    if (value == null || value.equalsIgnoreCase("NULL")) {
                        rowValues.append("NULL");
                    } else if (value.toUpperCase().startsWith("UNHEX('") || value.toUpperCase().startsWith("0X")) {
                        rowValues.append(value);
                    } else {
                        String safeValue = value.trim().replace("'", "''");
                        rowValues.append("'").append(safeValue).append("'");
                    }
                    if (i < columns.length - 1)
                        rowValues.append(", ");
                }
                stm.addBatch(sqlPrefix + rowValues.toString() + ")");
            }
            int[] results = stm.executeBatch();
            for (int r : results)
                if (r > 0)
                    rowsAffected += r;
            con.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro na inserção em lote: " + e.getMessage());
        }
        return false;
    }

    /**
     * Gera statements INSERT em formato SQL.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarSql(ResultSet rs, PrintWriter writer, String tableName) throws SQLException {
        gerarSql(rs, writer, tableName, 10);
    }

    /**
     * Gera statements INSERT em formato SQL com tamanho de lote configurável.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @param batchSize número de linhas por comando INSERT
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarSql(ResultSet rs, PrintWriter writer, String tableName, int batchSize)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder columnNames = new StringBuilder();

        Configura cfg = new Configura();
        final String targetDB = cfg.isMySQL() ? "MYSQL" : (cfg.isSQLServer() ? "SQLSERVER" : "DESCONHECIDO");
        final int actualBatchSize = (batchSize > 0) ? batchSize : 50;

        columnNames.append("(");
        for (int i = 1; i <= columnCount; i++) {
            columnNames.append(metaData.getColumnName(i));
            if (i < columnCount)
                columnNames.append(", ");
        }
        columnNames.append(")");

        writer.println("-- Dados exportados da tabela: " + tableName);
        writer.println("-- Data de exportação: " + java.time.LocalDateTime.now());
        writer.println("-- Target DB: " + targetDB);
        writer.println("-- Tamanho do Batch: " + actualBatchSize);
        writer.println();

        StringBuilder batchStatement = new StringBuilder();
        int rowCount = 0;

        while (rs.next()) {

            if (rowCount == 0) {
                batchStatement.setLength(0);
                batchStatement.append("INSERT INTO ").append(tableName).append(" ").append(columnNames)
                        .append(" VALUES ");
            } else {
                batchStatement.append(", ");
            }

            batchStatement.append("(");

            for (int i = 1; i <= columnCount; i++) {
                appendColumnValue(rs, metaData, i, batchStatement, targetDB);
                if (i < columnCount)
                    batchStatement.append(", ");
            }
            batchStatement.append(")");

            rowCount++;

            if (rowCount >= actualBatchSize) {
                batchStatement.append(";");
                writer.println(batchStatement.toString());
                rowCount = 0;
            }
        }

        if (rowCount > 0) {
            batchStatement.append(";");
            writer.println(batchStatement.toString());
        }
    }

    /**
     * Formata e adiciona o valor de uma coluna ao StringBuilder.
     * 
     * @param rs          ResultSet com os dados
     * @param metaData    metadados do ResultSet
     * @param columnIndex índice da coluna
     * @param sb          StringBuilder destino
     * @param targetDB    base de dados alvo
     * @throws SQLException em caso de erro SQL
     */

    private static void appendColumnValue(ResultSet rs, ResultSetMetaData metaData, int columnIndex, StringBuilder sb,
            String targetDB)
            throws SQLException {

        int columnType = metaData.getColumnType(columnIndex);

        if (rs.getObject(columnIndex) == null || rs.wasNull()) {
            sb.append("NULL");
        } else if (DataFormatter.isBlob(columnType)) {
            sb.append(DataFormatter.getBlobHex(rs, columnIndex));
        } else if (DataFormatter.isNumeric(columnType)) {
            sb.append(rs.getObject(columnIndex).toString());
            sb.append(rs.getObject(columnIndex).toString());
        } else {
            String value = rs.getString(columnIndex);
            if (value != null) {
                String escapedValue = value.replace("'", "''");
                sb.append("'").append(escapedValue).append("'");
            } else {
                sb.append("NULL");
            }
        }
    }

    /**
     * Gera dados em formato CSV.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarCsv(ResultSet rs, PrintWriter writer, String tableName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        StringBuilder header = new StringBuilder();

        for (int i = 1; i <= columnCount; i++) {
            header.append(metaData.getColumnName(i));
            if (i < columnCount)
                header.append(CSV_DELIMITER);
        }
        writer.println(header.toString());

        while (rs.next()) {
            StringBuilder rowData = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                if (value == null)
                    value = "";
                else {
                    if (DataFormatter.isBlob(metaData.getColumnType(i)))
                        value = DataFormatter.getBlobHex(rs, i);
                    value = value.replace("\"", "\"\"").replace("\n", " ").trim();
                    if (value.contains(CSV_DELIMITER))
                        value = "\"" + value + "\"";
                }
                rowData.append(value);
                if (i < columnCount)
                    rowData.append(CSV_DELIMITER);
            }
            writer.println(rowData.toString());
        }
    }

    /**
     * Exporta os dados de uma tabela para PDF utilizando um OutputStream.
     * 
     * @param tableName nome da tabela a exportar
     * @param os        stream de saída para escrita do PDF
     * @throws Exception em caso de erro durante a geração do PDF
     */

    public static void exportToPdf(String tableName, OutputStream os) throws Exception {

        final String fontName = "LiberationMono-Regular.ttf";
        final String FONT_PATH = pathFonts + fontName;

        String text = obterTxt(tableName);

        try (
                PdfWriter writer = new PdfWriter(os);
                PdfDocument pdf = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, PageSize.A3.rotate())) {

            PdfFont monospaceFont = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.IDENTITY_H);

            document.add(new Paragraph(text)
                    .setFont(monospaceFont)
                    .setFontSize(8)
                    .setFixedLeading(8f)
                    .setTextAlignment(TextAlignment.LEFT));
            document.close();
        } catch (IOException e) {
            System.err.println(" Erro de I/O ao gerar o PDF para stream: " + tableName);
            throw e;
        }
    }

    /**
     * Exporta os dados de uma tabela para um ficheiro PDF.
     * 
     * @param tableName nome da tabela a exportar
     * @return true se a exportação for bem-sucedida, false caso contrário
     */

    public static boolean exportToPdf(String tableName) {
        final String outputFileName = pathExport + tableName + "." + "pdf";

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(outputFileName)) {
            exportToPdf(tableName, fos);
            System.out.println(" Os dados da tabela '" + tableName
                    + "' foram exportados, no formato PDF, para o ficheiro '" + outputFileName + "'.");
            return true;
        } catch (Exception e) {
            System.err.println(" Erro ao exportar os dados da tabela '" + tableName
                    + "', no formato PDF, para o ficheiro '" + outputFileName + "':" + e.getMessage());
            return false;
        }
    }

    /**
     * Gera dados em formato JSON.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarJson(ResultSet rs, PrintWriter writer, String tableName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        writer.println("[");
        boolean firstRow = true;

        while (rs.next()) {
            if (!firstRow)
                writer.println(",");
            writer.println("  {");

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i).toLowerCase();
                Object value = rs.getObject(i);
                String jsonLine = String.format("    \"%s\": ", columnName);

                if (value == null)
                    jsonLine += "null";
                else if (DataFormatter.isNumeric(metaData.getColumnType(i)))
                    jsonLine += value.toString();
                else if (DataFormatter.isBlob(metaData.getColumnType(i)))
                    jsonLine += getBlobHex(rs, i);
                else {
                    String stringValue = value.toString().replace("\"", "\\\"").replace("\n", "\\n");
                    jsonLine += "\"" + stringValue.trim() + "\"";
                }

                if (i < columnCount)
                    jsonLine += ",";
                writer.println(jsonLine);
            }
            writer.print("  }");
            firstRow = false;
        }
        writer.println();
        writer.println("]");
    }

    /**
     * Gera uma página HTML com tabela de dados.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarHtml(ResultSet rs, PrintWriter writer, String tableName) throws SQLException {
        final int COL_DIM_MIN = 11;
        final int COL_DIM_MAX = 1000;
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        List<String> columnNames = new ArrayList<>();
        List<Integer> columnWidths = new ArrayList<>();
        AtomicInteger totalWidth = new AtomicInteger(0);

        for (int i = 1; i <= columnCount; i++) {
            String coluna = metaData.getColumnName(i).trim().toUpperCase();
            columnNames.add(coluna);
            int size = COL_DIM_MIN;

            if (coluna.compareToIgnoreCase("genero") != 0) {
                size = metaData.getColumnDisplaySize(i) + 2;
                size = size > COL_DIM_MAX ? COL_DIM_MAX : size;
                size = size < COL_DIM_MIN ? COL_DIM_MIN : size;
                if (coluna.length() > size)
                    size = coluna.length();
            }
            columnWidths.add(size);
            totalWidth.addAndGet(size);
        }

        StringBuilder css = new StringBuilder();
        css.append("<style>\n");
        css.append("  /* Estilos Básicos da Tabela */\n");
        css.append("  body { font-family: Arial, sans-serif; margin: 20px; background-color: #f4f4f9; }\n");
        css.append(
                "  .table-container { overflow-x: auto; margin-top: 20px; background-color: white; border: 1px solid #ccc; border-radius: 8px; padding: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }\n");

        css.append("  .data-table { width: 100%; border-collapse: collapse; min-width: 600px; }\n");
        css.append(
                "  .data-table th, .data-table td { border: 1px solid #ddd; padding: 8px; text-align: left; vertical-align: middle; }\n");

        css.append(
                "  .data-table th { background-color: #007bff; color: white; font-weight: bold; text-align: center; position: sticky; top: 0; z-index: 10; }\n");

        css.append("  .data-table tr:nth-child(even) { background-color: #f9f9f9; }\n");
        css.append("  .data-table tr:hover { background-color: #e0f7fa; }\n");

        css.append(
                "  .data-table caption { font-size: 1.5em; margin: 10px 0; font-weight: bold; color: #333; caption-side: top; }\n");

        css.append("  .data-table .align-right { text-align: right; }\n");
        css.append("  .data-table .align-center { text-align: center; }\n");

        css.append("  .data-table .align-right { text-align: right; }\n");
        css.append("  .data-table .align-center { text-align: center; }\n");

        css.append("  .data-table .blob-img { \n");
        css.append("    width: 70px; \n");
        css.append("    height: 90px; \n");
        css.append("    object-fit: cover; \n");
        css.append("    border: 3px solid #E0D3C9;\n");
        css.append("    box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.3);\n");
        css.append("    border-radius: 2px;\n");
        css.append("    display: block; \n");
        css.append("    margin: 0 auto; \n");
        css.append("  }\n");

        css.append("  .data-table td { vertical-align: middle; } \n");
        css.append("\n  /* Larguras das Colunas Baseadas no Cálculo Dinâmico */\n");

        for (int i = 0; i < columnCount; i++) {
            int width = columnWidths.get(i);
            double percentage = (totalWidth.get() > 0) ? (double) width / totalWidth.get() * 100 : 0;
            css.append(String.format("  .data-table col:nth-child(%d) { width: %.2f%%; }\n", (i + 1), percentage));
        }
        css.append("</style>\n");

        writer.println("<!DOCTYPE html>");
        writer.println("<html lang=\"pt\">");
        writer.println("<head>");
        writer.println("<meta charset=\"UTF-8\">");
        writer.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.println(String.format("<title>Dados: %s</title>", tableName.toUpperCase()));
        writer.println(css.toString());
        writer.println("</head>");
        writer.println("<body>");

        writer.println(String.format("<h1>Relatório de Dados: %s</h1>", tableName.toUpperCase()));

        writer.println("<div class='table-container'>");
        writer.println(String.format("<table class='data-table'>"));
        writer.println(String.format("<caption>Visualização: %s</caption>", tableName.toUpperCase()));

        writer.println("<colgroup>");
        writer.println("<colgroup>");
        for (int i = 0; i < columnCount; i++) {
            writer.println(String.format("<col id='col-%d'>", i));
        }
        writer.println("</colgroup>");

        writer.println("<thead><tr>");
        for (String name : columnNames) {
            writer.println(String.format("<th>%s</th>", name));
        }
        writer.println("</tr></thead>");

        writer.println("<tbody>");

        while (rs.next()) {
            writer.println("<tr>");
            for (int i = 1; i <= columnCount; i++) {
                String alignClass = "";
                int type = metaData.getColumnType(i);
                String displayValue = "";

                if (DataFormatter.isBlob(type)) {
                    String base64Data = DataFormatter.blobToBase64(rs, i);

                    if (base64Data != null && !base64Data.isEmpty()) {
                        displayValue = "<img class='blob-img' src='data:image/jpeg;base64," + base64Data + "'" +
                                " title='Imagem guardada originalmente na BD em .jpg' alt='Imagem BLOB' >";
                    } else {
                        displayValue = "[BLOB VAZIO]";
                    }
                    alignClass = " class='align-center'";

                } else {
                    if (DataFormatter.isDateOrTime(type)) {
                        alignClass = " class='align-center'";
                    } else if (DataFormatter.isNumeric(type)) {
                        alignClass = " class='align-right'";
                    }

                    displayValue = DataFormatter.formatColumn(rs, metaData, i);
                }

                writer.println(String.format("<td%s>%s</td>", alignClass, displayValue.trim()));
            }
            writer.println("</tr>");
        }

        writer.println("</tbody>");
        writer.println("</table>");
        writer.println("</div>");

        writer.println("<footer>");
        writer.println(String.format("<p><small>-- Exportado em: %s</small></p>", LocalDateTime.now()));
        writer.println("</footer>");

        writer.println("</body>");
        writer.println("</html>");
    }

    /**
     * Gera uma tabela de dados formatada em modo texto.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarTxt(ResultSet rs, PrintWriter writer, String tableName) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<String> columnNames = new ArrayList<>();
        List<Integer> columnSizes = new ArrayList<>();

        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i).trim().toUpperCase());
            columnSizes.add(DataFormatter.formatSize(metaData, i));
        }

        String headerSeparator = "";
        String header = "";

        for (int j = 0; j < columnSizes.size(); j++) {
            int currentSize = columnSizes.get(j);

            if (j < columnSizes.size() - 1) {
                headerSeparator += "".repeat(currentSize - 1) + "";
                header += DataFormatter.padCenter(columnNames.get(j), currentSize - 1) + "";
            } else {
                headerSeparator += "".repeat(currentSize);
                header += DataFormatter.padCenter(columnNames.get(j), currentSize);
            }
        }

        String borderLine = headerSeparator.replace("", "");
        final int W_INNER = borderLine.length();

        String title = "Conteudo de: " + tableName.toUpperCase();
        writer.println("" + borderLine + "");
        writer.println("" + DataFormatter.padCenter(title, W_INNER) + "");
        writer.println("" + headerSeparator + "");
        writer.println("" + header + "");
        writer.println("" + headerSeparator.replace("", "") + "");

        boolean ok = false;
        String lineSeparator = headerSeparator.replace("", "");
        while (rs.next()) {
            if (ok)
                writer.println("" + lineSeparator.replace("", "") + "");
            ok = true;
            StringBuilder row = new StringBuilder("");

            for (int i = 1; i <= columnCount; i++) {
                row.append(DataFormatter.formatColumn(rs, metaData, i));
                if (i < columnCount)
                    row.append("");
            }
            writer.println(row.toString() + "");
        }
        writer.println("" + headerSeparator.replace("", "") + "");
        writer.println("\n-- Processado em: " + LocalDateTime.now());
    }

    /**
     * Gera dados em formato XML.
     * 
     * @param rs        ResultSet com os dados
     * @param writer    PrintWriter para escrita
     * @param tableName nome da tabela
     * @throws SQLException em caso de erro SQL
     */

    private static void gerarXml(ResultSet rs, PrintWriter writer, String tableName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println(
                "<data exported_table=\"" + tableName + "\" timestamp=\"" + java.time.LocalDateTime.now() + "\">");
        String rowTag = tableName.substring(0, 1).toUpperCase() + tableName.substring(1).toLowerCase();

        while (rs.next()) {
            writer.println("  <" + rowTag + ">");
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i).toLowerCase();
                String value = rs.getString(i);
                if (value == null)
                    writer.println("    <" + columnName
                            + " xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
                else {
                    if (DataFormatter.isBlob(metaData.getColumnType(i)))
                        value = getBlobHex(rs, i);
                    value = value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").trim();
                    writer.println("    <" + columnName + ">" + value + "</" + columnName + ">");
                }
            }
            writer.println("  </" + rowTag + ">");
        }
        writer.println("</data>");
    }

    /**
     * Converte o conteúdo BLOB para string hexadecimal formatada.
     * 
     * @param rs          ResultSet do qual os dados estão a ser lidos
     * @param columnIndex índice da coluna que contém o BLOB
     * @return string formatada para SQL ou NULL em caso de erro
     */

    private static String getBlobHex(ResultSet rs, int columnIndex) {
        Configura cfg = new Configura();
        String hexValue = null;

        try {
            hexValue = DataFormatter.blobToHexString(rs, columnIndex);
            if (hexValue != null) {
                if (cfg.isMySQL()) {
                    return "UNHEX('" + hexValue + "')";
                } else if (cfg.isSQLServer()) {
                    return "0x" + hexValue;
                } else {
                    System.err.println("ERRO: SGBD desconhecido ao tentar formatar BLOB para SQL.");
                    return "NULL /* ERRO: SGBD Desconhecido ou não suportado para BLOB */";
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO SQL ao processar BLOB na coluna " + columnIndex + ": " + e.getMessage());
        }

        return "NULL";
    }

    /**
     * Exporta o perfil completo de um animal para XML.
     * 
     * @param animalId identificador do animal
     * @param writer   PrintWriter para escrita
     */

    public static void exportAnimalFullProfileXml(int animalId, PrintWriter writer) {
        Animal a = AnimalDAO.getById(animalId);
        if (a == null) {
            writer.println("<error>Animal not found</error>");
            return;
        }

        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println(
                "<animal_profile id=\"" + a.getIdAnimal() + "\" timestamp=\"" + java.time.LocalDateTime.now() + "\">");
        writer.println("  <info>");
        writer.println("    <nome>" + escapeXml(a.getNome()) + "</nome>");
        writer.println("    <raca>" + escapeXml(a.getRaca()) + "</raca>");
        writer.println("    <sexo>" + escapeXml(a.getSexo()) + "</sexo>");
        writer.println("    <nascimento>" + a.getDataNascimento() + "</nascimento>");
        writer.println("    <filiacao>" + escapeXml(a.getFiliacao()) + "</filiacao>");
        writer.println("    <transponder>" + escapeXml(a.getNumeroTransponder()) + "</transponder>");
        writer.println("    <alergias>" + escapeXml(a.getAlergias()) + "</alergias>");
        writer.println("    <cores>" + escapeXml(a.getCores()) + "</cores>");
        writer.println("    <peso>" + a.getPesoAtual() + "</peso>");
        writer.println("    <estado_reprodutivo>" + escapeXml(a.getEstadoReprodutivo()) + "</estado_reprodutivo>");
        writer.println("    <caracteristicas>" + escapeXml(a.getCaracteristicasDistintivas()) + "</caracteristicas>");
        writer.println("    <catalogo>" + escapeXml(a.getCatalogoNomeComum()) + "</catalogo>");
        writer.println("    <tutor_nif>" + escapeXml(a.getClienteNif()) + "</tutor_nif>");
        writer.println("  </info>");
        writer.println("  <history>");

        Configura configuradorBD = new Configura();
        String sql = "SELECT * FROM HistoricoClinico WHERE IDAnimal = ? ORDER BY DataHora DESC";
        try (Connection con = configuradorBD.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, animalId);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                while (rs.next()) {
                    writer.println("    <record>");
                    for (int i = 1; i <= cols; i++) {
                        String colName = meta.getColumnName(i).toLowerCase();
                        String val = rs.getString(i);
                        if (val != null) {
                            writer.println("      <" + colName + ">" + escapeXml(val) + "</" + colName + ">");
                        }
                    }
                    writer.println("    </record>");
                }
            }
        } catch (SQLException e) {
            writer.println("    <error>Failed to fetch history: " + escapeXml(e.getMessage()) + "</error>");
        }

        writer.println("  </history>");
        writer.println("</animal_profile>");
    }

    /**
     * Converte caracteres especiais para entidades XML.
     * 
     * @param s cadeia de caracteres original
     * @return cadeia de caracteres com escape para XML
     */

    private static String escapeXml(String s) {
        if (s == null)
            return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    /**
     * Exporta o perfil completo de um animal para JSON.
     * 
     * @param animalId identificador do animal
     * @param writer   PrintWriter para escrita
     */

    public static void exportAnimalFullProfileJson(int animalId, PrintWriter writer) {
        Animal a = AnimalDAO.getById(animalId);
        if (a == null) {
            writer.println("{\"erro\": \"Animal não encontrado\"}");
            return;
        }

        JSONObject root = new JSONObject();
        try {
            JSONObject info = new JSONObject();
            info.put("id", a.getIdAnimal());
            info.put("nome", a.getNome());
            info.put("raca", a.getRaca());
            info.put("sexo", a.getSexo());
            info.put("nascimento", a.getDataNascimento() != null ? a.getDataNascimento().toString() : "");
            info.put("filiacao", a.getFiliacao());
            info.put("transponder", a.getNumeroTransponder());
            info.put("alergias", a.getAlergias());
            info.put("cores", a.getCores());
            info.put("peso", a.getPesoAtual());
            info.put("estado_reprodutivo", a.getEstadoReprodutivo());
            info.put("caracteristicas", a.getCaracteristicasDistintivas());
            info.put("catalogo", a.getCatalogoNomeComum());
            info.put("tutor_nif", a.getClienteNif());
            root.put("info", info);

            JSONArray history = new JSONArray();
            Configura configuradorBD = new Configura();
            String sql = "SELECT * FROM HistoricoClinico WHERE IDAnimal = ? ORDER BY DataHora DESC";
            try (Connection con = configuradorBD.getConnection();
                    PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, animalId);
                try (ResultSet rs = ps.executeQuery()) {
                    ResultSetMetaData meta = rs.getMetaData();
                    int cols = meta.getColumnCount();
                    while (rs.next()) {
                        JSONObject record = new JSONObject();
                        for (int i = 1; i <= cols; i++) {
                            String colName = meta.getColumnName(i).toLowerCase();
                            Object val = rs.getObject(i);
                            record.put(colName, val == null ? JSONObject.NULL : val);
                        }
                        history.put(record);
                    }
                }
            }
            root.put("history", history);
            writer.println(root.toString(2));
        } catch (Exception e) {
            writer.println("{\"erro\": \"Falha na geração do JSON: " + e.getMessage() + "\"}");
        }
    }

    /**
     * Importa um perfil completo de animal a partir de XML.
     * 
     * @param is fluxo de dados XML
     * @return true se a importação for bem-sucedida
     */

    public static boolean importAnimalFullProfileXml(InputStream xmlStream) {
        logDebug("Starting XML Import...");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlStream);
            doc.getDocumentElement().normalize();

            NodeList infoList = doc.getElementsByTagName("info");
            Element infoElem = (Element) infoList.item(0);

            String tutorNif = getTagValue("tutor_nif", infoElem);

            Cliente c = ClienteDAO.getByNif(tutorNif);
            if (c == null) {
                logDebug("Error: Tutor NIF " + tutorNif + " not found.");
                return false;
            }

            Animal a = new Animal();
            a.setClienteNif(tutorNif);
            a.setNome(getTagValue("nome", infoElem));
            a.setCatalogoNomeComum(getTagValue("catalogo", infoElem));
            a.setRaca(getTagValue("raca", infoElem));
            a.setSexo(getTagValue("sexo", infoElem));
            a.setNumeroTransponder(getTagValue("transponder", infoElem));
            a.setDataNascimento(DataFormatter.StringToSqlDate(getTagValue("nascimento", infoElem)));

            try {
                String pesoStr = getTagValue("peso", infoElem);
                if (pesoStr != null)
                    a.setPesoAtual(new java.math.BigDecimal(pesoStr));
                else
                    a.setPesoAtual(java.math.BigDecimal.ZERO);
            } catch (Exception e) {
                a.setPesoAtual(java.math.BigDecimal.ZERO);
            }

            a.setEstadoReprodutivo(getTagValue("estado_reprodutivo", infoElem));
            a.setFiliacao(getTagValue("filiacao", infoElem));
            a.setAlergias(getTagValue("alergias", infoElem));
            a.setCores(getTagValue("cores", infoElem));
            a.setCaracteristicasDistintivas(getTagValue("caracteristicas", infoElem));

            String transponder = getTagValue("transponder", infoElem);
            Animal existing = AnimalDAO.getByTransponder(transponder);
            if (existing != null) {
                logDebug("Animal with transponder " + transponder + " exists. Updating (ID: " + existing.getIdAnimal()
                        + ")...");
                a.setIdAnimal(existing.getIdAnimal());
            }

            int animalId;
            if (a.getIdAnimal() != null && a.getIdAnimal() > 0) {
                AnimalDAO.update(a);
                animalId = a.getIdAnimal();
                logDebug("Animal updated with ID: " + animalId);
            } else {
                animalId = AnimalDAO.save(a);
                logDebug("Animal created with ID: " + animalId);
            }

            if (animalId <= 0) {
                logDebug("Error: Failed to save/update Animal. Reason: " + AnimalDAO.getLastError());
                return false;
            }

            NodeList historyList = doc.getElementsByTagName("record");
            logDebug("Found history records: " + historyList.getLength());

            for (int i = 0; i < historyList.getLength(); i++) {
                Node recordNode = historyList.item(i);
                if (recordNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element recordElem = (Element) recordNode;
                    historico.PrestacaoServico ps;
                    String tipo = getTagValue("tipo", recordElem);
                    if (tipo == null)
                        tipo = "Consulta";
                    String detalhes = getTagValue("detalhes", recordElem);
                    if (detalhes == null)
                        detalhes = "";

                    logDebug("Processing XML record " + i + ": Type=" + tipo);

                    if (tipo.equalsIgnoreCase("Cirurgia")) {
                        historico.Cirurgia x = new historico.Cirurgia();
                        x.setTipoCirurgia("Geral");
                        x.setNotasPosOperatorias(detalhes);
                        ps = x;
                    } else if (tipo.equalsIgnoreCase("Vacinacao")) {
                        historico.Vacinacao v = new historico.Vacinacao();
                        v.setTipoVacina(detalhes);
                        v.setFabricante("Desconhecido");
                        ps = v;
                    } else if (tipo.equalsIgnoreCase("Desparasitacao")) {
                        historico.Desparasitacao d = new historico.Desparasitacao();
                        d.setTipo("Geral");
                        d.setProdutosUtilizados(detalhes);
                        ps = d;
                    } else if (tipo.equalsIgnoreCase("ResultadoExame")) {
                        historico.ResultadoExame re = new historico.ResultadoExame();
                        re.setTipoExame("Geral");
                        re.setResultadoDetalhado(detalhes);
                        ps = re;
                    } else if (tipo.equalsIgnoreCase("TratamentoTerapeutico")) {
                        historico.TratamentoTerapeutico tt = new historico.TratamentoTerapeutico();
                        tt.setDescricao(detalhes);
                        ps = tt;
                    } else if (tipo.equalsIgnoreCase("ExameFisico")) {
                        historico.ExameFisico ef = new historico.ExameFisico();
                        ps = ef;
                    } else {
                        historico.Consulta cx = new historico.Consulta();
                        cx.setMotivo(detalhes);
                        cx.setSintomas("");
                        cx.setDiagnostico("");
                        cx.setMedicacaoPrescrita("");
                        ps = cx;
                    }

                    ps.setAnimalId(animalId);
                    ps.setTipoDiscriminador(tipo);

                    String valDh = getTagValue("datahora", recordElem);
                    if (valDh != null && !valDh.isEmpty()) {
                        try {

                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy HH:mm");
                            java.util.Date parsedDate = sdf.parse(valDh);
                            ps.setDataHora(new java.sql.Timestamp(parsedDate.getTime()));
                        } catch (Exception e) {

                            ps.setDataHora(new java.sql.Timestamp(System.currentTimeMillis()));
                        }
                    } else {
                        ps.setDataHora(new java.sql.Timestamp(System.currentTimeMillis()));
                    }

                    ps.setDetalhesGerais(detalhes);
                    int srvId = resolveServiceId(tipo);
                    ps.setTipoServicoId(srvId);
                    logDebug("Resolved Service ID: " + srvId);

                    int res = HistoricoDAO.save(ps);
                    logDebug("Save Result: " + res);
                }
            }
            return true;
        } catch (Exception e) {
            logDebug("Exception XML: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Importa um perfil completo de animal a partir de JSON.
     * 
     * @param jsonStr cadeia JSON contendo o perfil
     * @return true se a importação for bem-sucedida
     */

    public static boolean importAnimalFullProfileJson(String jsonStr) {
        logDebug("Starting JSON Import...");
        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONObject info = root.getJSONObject("info");
            String transponder = info.optString("transponder");

            String tutorNif = info.optString("tutor_nif");
            Cliente c = ClienteDAO.getByNif(tutorNif);
            if (c == null) {
                logDebug("Error: Tutor NIF " + tutorNif + " not found.");
                return false;
            }

            Animal a = new Animal();
            if (transponder != null && !transponder.isEmpty()) {
                Animal existing = AnimalDAO.getByTransponder(transponder);
                if (existing != null) {
                    logDebug("Animal (JSON) with transponder " + transponder + " exists. Updating (ID: "
                            + existing.getIdAnimal() + ")...");
                    a.setIdAnimal(existing.getIdAnimal());
                } else {
                    a.setNumeroTransponder(transponder);
                }
            }

            a.setNome(info.optString("nome"));
            a.setRaca(info.optString("raca"));
            a.setSexo(info.optString("sexo"));
            a.setDataNascimento(DataFormatter.StringToSqlDate(info.optString("nascimento")));
            a.setCatalogoNomeComum(info.optString("catalogo"));
            a.setClienteNif(tutorNif);

            if (info.has("peso")) {
                a.setPesoAtual(java.math.BigDecimal.valueOf(info.optDouble("peso")));
            } else {
                a.setPesoAtual(java.math.BigDecimal.ZERO);
            }
            a.setAlergias(info.optString("alergias"));
            a.setCores(info.optString("cores"));
            a.setEstadoReprodutivo(info.optString("estado_reprodutivo"));
            a.setCaracteristicasDistintivas(info.optString("caracteristicas"));

            int animalId;
            if (a.getIdAnimal() != null && a.getIdAnimal() > 0) {
                AnimalDAO.update(a);
                animalId = a.getIdAnimal();
                logDebug("Animal (JSON) updated with ID: " + animalId);
            } else {
                animalId = AnimalDAO.save(a);
                logDebug("Animal (JSON) created with ID: " + animalId);
            }

            if (animalId <= 0) {
                logDebug("Error: Failed to save/update Animal (JSON). Reason: " + AnimalDAO.getLastError());
                return false;
            }

            JSONArray history = root.getJSONArray("history");
            logDebug("Found JSON history records: " + history.length());

            for (int i = 0; i < history.length(); i++) {
                JSONObject rec = history.getJSONObject(i);
                historico.PrestacaoServico ps;
                String tipo = rec.optString("tipo");
                String detalhes = rec.optString("detalhes");

                logDebug("Processing JSON record " + i + ": Type=" + tipo);

                if (tipo.equalsIgnoreCase("Cirurgia")) {
                    historico.Cirurgia x = new historico.Cirurgia();
                    x.setTipoCirurgia("Geral");
                    x.setNotasPosOperatorias(detalhes);
                    ps = x;
                } else if (tipo.equalsIgnoreCase("Vacinacao")) {
                    historico.Vacinacao v = new historico.Vacinacao();
                    v.setTipoVacina(detalhes);
                    v.setFabricante("Desconhecido");
                    ps = v;
                } else if (tipo.equalsIgnoreCase("Desparasitacao")) {
                    historico.Desparasitacao d = new historico.Desparasitacao();
                    d.setTipo("Geral");
                    d.setProdutosUtilizados(detalhes);
                    ps = d;
                } else if (tipo.equalsIgnoreCase("ResultadoExame")) {
                    historico.ResultadoExame re = new historico.ResultadoExame();
                    re.setTipoExame("Geral");
                    re.setResultadoDetalhado(detalhes);
                    ps = re;
                } else if (tipo.equalsIgnoreCase("TratamentoTerapeutico")) {
                    historico.TratamentoTerapeutico tt = new historico.TratamentoTerapeutico();
                    tt.setDescricao(detalhes);
                    ps = tt;
                } else if (tipo.equalsIgnoreCase("ExameFisico")) {
                    historico.ExameFisico ef = new historico.ExameFisico();
                    ps = ef;
                } else {
                    historico.Consulta cx = new historico.Consulta();
                    cx.setMotivo(detalhes);
                    cx.setSintomas("");
                    cx.setDiagnostico("");
                    cx.setMedicacaoPrescrita("");
                    ps = cx;
                }

                ps.setAnimalId(animalId);
                ps.setTipoDiscriminador(tipo);

                String dh = rec.optString("datahora");
                if (dh != null && !dh.isEmpty()) {
                    String safeDate = dh.replace("T", " ");
                    if (safeDate.length() == 16)
                        safeDate += ":00";
                    try {
                        ps.setDataHora(java.sql.Timestamp.valueOf(safeDate));
                    } catch (IllegalArgumentException ex) {
                        logDebug("Ignorando data inválida JSON: " + safeDate);
                    }
                }

                ps.setDetalhesGerais(detalhes);
                int srvId = resolveServiceId(tipo);
                ps.setTipoServicoId(srvId);
                logDebug("Resolved Service ID (JSON): " + srvId);

                int res = HistoricoDAO.save(ps);
                logDebug("Save Result (JSON): " + res);
            }
            return true;
        } catch (Exception e) {
            logDebug("Exception JSON: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera o valor de uma tag XML específica.
     * 
     * @param tag  nome da tag
     * @param elem elemento XML contentor
     * @return valor textual da tag ou null se não encontrada
     */
    private static String getTagValue(String tag, Element elem) {
        NodeList nl = elem.getElementsByTagName(tag);
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Tenta resolver um ID de TipoServico válido baseado no nome do tipo.
     * Se não encontrar correspondência, devolve o primeiro disponível na BD.
     * Se a BD estiver vazia, devolve 1 (fallback).
     */
    private static int resolveServiceId(String typeName) {

        List<TipoServico> servicos = AgendamentoDAO.getTiposServico();
        if (servicos == null || servicos.isEmpty()) {
            return 1;
        }

        for (TipoServico ts : servicos) {
            if (ts.getNome().equalsIgnoreCase(typeName) ||
                    ts.getNome().toLowerCase().contains(typeName.toLowerCase()) ||
                    typeName.toLowerCase().contains(ts.getNome().toLowerCase())) {
                return ts.getIdServico();
            }
        }

        if ("Consulta".equalsIgnoreCase(typeName)) {
            for (TipoServico ts : servicos) {
                if (ts.getNome().toLowerCase().contains("geral") || ts.getNome().toLowerCase().contains("rotina")) {
                    return ts.getIdServico();
                }
            }
        }

        return servicos.get(0).getIdServico();
    }

    /**
     * Regista mensagens de depuração na consola.
     * Substitui a antiga implementação baseada em ficheiro.
     * 
     * @param msg mensagem a registar
     */
    public static void logDebug(String msg) {
        System.out.println("[DataTransfer] " + msg);
    }
}
