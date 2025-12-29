package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Responsável pela configuração centralizada e gestão de ligações à base de
 * dados.
 * Suporta múltiplos sistemas de gestão de bases de dados (SGBD) e gere a
 * deteção automática de ambiente (Console ou Web).
 */
public class Configura {

	/** Enumeração dos SGBDs suportados pela aplicação. */
	public enum SGBD {
		SQLServer, MySQL
	}

	/** Nome oficial da aplicação. */
	private static final String APP_NAME = "VetCare Manager";
	/** Versão atual da aplicação. */
	private static final String APP_VERSION = "1.0.Final";
	/** Abreviação usada em contextos técnicos ou de URL. */
	private static final String APP_ABR = "VetCare";

	/** Localização relativa do ficheiro de configuração de propriedades. */
	private static final String CONFIG_FILE = "WEB-INF/config.properties";

	/** Caminho absoluto em cache para o diretório de ficheiros da aplicação. */
	private static String filePath = null;

	/** Nome da base de dados por omissão. */
	private static String database_ = "VetCare";
	/** Nome da base de dados da instância. */
	private String database = database_;
	/** Endereço do servidor da base de dados. */
	private String server = "localhost";
	/** Identificador do utilizador para acesso. */
	private String usr = "root";
	/** Credencial de acesso do utilizador. */
	private String pwd = "Danone2005$";

	/** Classe do driver JDBC selecionado. */
	private String drv = null;
	/** URL de ligação formatada para o driver selecionado. */
	private String url = null;
	/** Tipo de SGBD em uso pela instância. */
	private SGBD sgbd = null;

	/**
	 * Construtor por omissão, inicializando a configuração para MySQL.
	 */
	public Configura() {
		this(SGBD.MySQL);
	}

	/**
	 * Construtor especializado que configura a instância para um SGBD específico.
	 * 
	 * @param sgbd Tipo de base de dados a utilizar.
	 */
	public Configura(SGBD sgbd) {
		this.sgbd = sgbd;
		build();
		loadProperties();
		loadDriver();
	}

	/**
	 * Constrói as strings de ligação e define os drivers com base no tipo de SGBD.
	 */
	private synchronized void build() {
		this.database = Configura.database_;
		if (this.isSQLServer()) {
			this.drv = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			this.url = "jdbc:sqlserver://" + this.server + ":1433;databaseName="
					+ this.database + ";encrypt=true;trustServerCertificate=true;";
		} else if (this.isMySQL()) {
			this.drv = "com.mysql.cj.jdbc.Driver";
			this.url = "jdbc:mysql://" + this.server + ":3306/" + this.database
					+ "?useLegacyDatetimeCode=false&serverTimezone=Europe/Lisbon";
		}
	}

	/**
	 * Identifica e devolve o caminho real no sistema de ficheiros para a raiz da
	 * aplicação.
	 * 
	 * @return Caminho absoluto para os recursos da aplicação.
	 */
	public String getRealPath() {
		if (filePath == null) {
			filePath = "src/main/webapp/";
			if (!new File(filePath).exists())
				filePath = getWebRootPath(server);
		}
		return filePath;
	}

	/**
	 * Carrega os parâmetros de conexão definidos no ficheiro de propriedades
	 * externo.
	 */
	private void loadProperties() {
		String filePath = getRealPath() + CONFIG_FILE;
		Properties properties = new Properties();
		try (FileInputStream fis = new FileInputStream(filePath)) {
			properties.load(fis);
			setSRV(properties.getProperty("db.server"));
			setUSR(properties.getProperty("db.user"));
			setPWD(properties.getProperty("db.password"));
			setDTB(properties.getProperty("db.database"));
		} catch (IOException e) {
			System.err.println("Falha ao carregar o ficheiro de configuração.");
		}
	}

	/**
	 * Efetua o carregamento dinâmico da classe do driver JDBC na memória.
	 * 
	 * @return Verdadeiro se o carregamento for bem-sucedido.
	 */
	public boolean loadDriver() {
		try {
			Class.forName(this.drv);
			return true;
		} catch (ClassNotFoundException e) {
			System.err.println("Driver JDBC não encontrado.");
		} catch (Exception e) {
			System.err.println("Erro inesperado no carregamento do driver.");
		}
		return false;
	}

	/**
	 * Estabelece uma ligação ativa com a base de dados com auto-confirmação
	 * ativada.
	 * 
	 * @return Ligação estabelecida ou nulo em caso de erro.
	 */
	public Connection getConnection() {
		return getConnection(true);
	}

	/**
	 * Estabelece uma ligação ativa permitindo o controlo manual da confirmação de
	 * transações.
	 * 
	 * @param autocommit Define se as operações devem ser confirmadas
	 *                   automaticamente.
	 * @return Ligação estabelecida.
	 */
	public Connection getConnection(boolean autocommit) {
		Connection con = null;
		try {
			con = DriverManager.getConnection(this.url, this.usr, this.pwd);
			if (!autocommit)
				con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			con.setAutoCommit(autocommit);
		} catch (SQLException e) {
			System.err.println("Impossível estabelecer ligação ao servidor SQL.");
		}
		return con;
	}

	/**
	 * Determina se a aplicação está a ser executada num contentor web.
	 * 
	 * @return Verdadeiro se detetar propriedades típicas de servidores de
	 *         aplicações.
	 */
	public static boolean isWebEnvironment() {
		if (System.getProperty("catalina.base") != null)
			return true;
		if (System.getProperty("jetty.home") != null || System.getProperty("jetty.base") != null)
			return true;
		if (System.getProperty("jboss.server.base.dir") != null)
			return true;
		return false;
	}

	/**
	 * Obtém o caminho da raiz web através de um pedido direto ao servlet de
	 * mapeamento.
	 * 
	 * @param server Endereço do servidor onde a aplicação está alojada.
	 * @return Caminho do sistema de ficheiros para a raiz da aplicação.
	 */
	public static String getWebRootPath(String server) {
		String port = ":8080";
		if (server.contains(":"))
			port = "";
		String servletURL = "http://" + server + port + "/" + APP_ABR + "/WebRootPath";
		try {
			URI uri = new URI(servletURL);
			URL url = uri.toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				StringBuilder response = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					String inputLine;
					while ((inputLine = in.readLine()) != null)
						response.append(inputLine);
					return response.toString();
				}
			}
		} catch (Exception e) {
			System.err.println("Erro na obtenção do caminho web real.");
		}
		return null;
	}

	/**
	 * Verifica a validade e estado de uma ligação ativa.
	 * 
	 * @param con Ligação a testar.
	 * @return Verdadeiro se a ligação estiver funcional.
	 */
	public static boolean isConnectionValid(Connection con) {
		if (con == null)
			return false;
		try {
			return con.isValid(5);
		} catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Encerra uma ligação à base de dados de forma segura.
	 * 
	 * @param con Ligação a encerrar.
	 */
	public static void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				System.err.println("Erro ao encerrar ligação.");
			}
		}
	}

	/** @return Tipo de SGBD configurado. */
	public SGBD getSGBD() {
		return sgbd;
	}

	/** @return Nome da base de dados ativa. */
	public String getDTB() {
		return database;
	}

	/** @return Classe do driver JDBC. */
	public String getDRV() {
		return drv;
	}

	/** @return Nome do utilizador. */
	public String getUSR() {
		return usr;
	}

	/** @return URL de ligação completo. */
	public String getURL() {
		return url;
	}

	/** @return Verdadeiro se o SGBD for MySQL. */
	public boolean isMySQL() {
		return sgbd == SGBD.MySQL;
	}

	/** @return Verdadeiro se o SGBD for SQL Server. */
	public boolean isSQLServer() {
		return sgbd == SGBD.SQLServer;
	}

	/** @param str Define globalmente o nome da base de dados. */
	public synchronized static void setDTB_(final String str) {
		database_ = str.trim();
	}

	/**
	 * @param str Define o nome da base de dados para a instância e reconstrói o
	 *            URL.
	 */
	public synchronized void setDTB(final String str) {
		if (str != null) {
			database = str.trim();
			database_ = database;
			build();
		}
	}

	/** @param str Define o endereço do servidor e reconstrói o URL. */
	public synchronized void setSRV(final String str) {
		if (str != null) {
			server = str.trim();
			build();
		}
	}

	/** @param str Define a credencial de acesso. */
	public synchronized void setPWD(final String str) {
		if (str != null)
			pwd = str.trim();
	}

	/** @param str Define o nome do utilizador. */
	public synchronized void setUSR(final String str) {
		if (str != null)
			usr = str.trim();
	}

	/** @return Data atual obtida diretamente do servidor de base de dados. */
	public synchronized LocalDate today() {
		LocalDate hoje = null;
		String dtb = getDTB();
		setDTB("");
		String func = sgbd == SGBD.SQLServer ? "GETDATE()" : "CURDATE()";
		try (Connection con = getConnection();
				Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT " + func + " AS Today")) {
			if (rs.next())
				hoje = rs.getDate(1).toLocalDate();
		} catch (SQLException e) {
			System.err.println("Erro na obtenção da data do servidor.");
		}
		setDTB(dtb);
		return hoje;
	}
}