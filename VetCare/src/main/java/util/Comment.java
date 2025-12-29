package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utilitário para gestão de metadados e comentários de vistas de base de dados.
 * 
 * Esta classe implementa uma arquitetura de persistência híbrida que combina:
 * - Armazenamento em ficheiro de propriedades para garantir persistência dos
 * descritivos.
 * - Cache em memória para otimização de desempenho.
 * - Reflexão computacional para suporte multi-motor de base de dados.
 * 
 * A classe resolve o problema de volatilidade de comentários em vistas SQL,
 * especialmente
 * em motores como MySQL, onde os comentários podem ser perdidos após
 * reinicializações.
 */
public final class Comment {

    /**
     * Cache em memória para acesso rápido aos comentários sem latência de disco.
     */
    private static Map<String, String> COMENTARIOS;

    /**
     * Caminho do ficheiro de propriedades onde os comentários são persistidos.
     * Localizado em WEB-INF para segurança (inacessível via browser).
     */
    private static final String COMMENT_FILE = new Configura().getRealPath() + "WEB-INF/comments.properties";

    /**
     * Bloco de inicialização estática.
     * Carrega os comentários do ficheiro de propriedades para a cache em memória.
     * Se o ficheiro não existir, inicializa com valores de demonstração.
     */
    static {
        COMENTARIOS = loadCommentsFromFile();
        if (COMENTARIOS.isEmpty()) {
            COMENTARIOS = new HashMap<>();
            COMENTARIOS.put("ALUNOS", "Vista principal que agrega dados de alunos e notas.");
            saveCommentsToFile(COMENTARIOS);
        }
    }

    /**
     * Aplica um comentário a uma vista de base de dados utilizando reflexão para
     * determinar o motor de base de dados ativo e invocar o método específico
     * correspondente.
     * 
     * Este método implementa um padrão de invocação dinâmica que permite
     * extensibilidade
     * sem modificação do código central. Para suportar um novo motor de base de
     * dados,
     * basta adicionar o método específico (ex: viewOracle) sem alterar esta lógica.
     * 
     * @param nomeDaVista    Nome da vista de base de dados.
     * @param novoComentario Comentário a aplicar à vista.
     * @return true se o comentário foi aplicado com sucesso, false caso contrário.
     */
    static public boolean view(String nomeDaVista, String novoComentario) {
        final String[] DRIVERS = { "MySQL", "SQLServer" };
        Configura cfg = new Configura();

        for (String driver : DRIVERS) {
            try {
                Method checkDriver = cfg.getClass().getMethod("is" + driver);
                if ((Boolean) checkDriver.invoke(cfg)) {
                    Method actionMethod = Comment.class.getDeclaredMethod("view" + driver, String.class, String.class);
                    actionMethod.setAccessible(true);
                    return (boolean) actionMethod.invoke(null, nomeDaVista, novoComentario);
                }
            } catch (Exception e) {
                System.err.println("Erro ao aplicar comentário via reflexão: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Recupera um comentário da cache em memória.
     * 
     * @param chave Identificador do comentário (nome da vista ou tabela).
     * @return Comentário associado à chave, ou "?" se não encontrado.
     */
    public static String get(String chave) {
        return COMENTARIOS.getOrDefault(chave.toUpperCase(), "?");
    }

    /**
     * Carrega os comentários do ficheiro de propriedades para um mapa em memória.
     * 
     * @return Mapa contendo os comentários carregados, ou mapa vazio em caso de
     *         erro.
     */
    private static Map<String, String> loadCommentsFromFile() {
        Map<String, String> comments = new HashMap<>();
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(COMMENT_FILE)) {
            props.load(fis);
            for (String key : props.stringPropertyNames()) {
                comments.put(key, props.getProperty(key));
            }
        } catch (IOException e) {
            System.err.println("Aviso: Ficheiro de comentários não encontrado. Será criado automaticamente.");
        }

        return comments;
    }

    /**
     * Persiste os comentários do mapa em memória para o ficheiro de propriedades.
     * 
     * @param comments Mapa contendo os comentários a persistir.
     */
    private static void saveCommentsToFile(Map<String, String> comments) {
        Properties props = new Properties();

        for (Map.Entry<String, String> entry : comments.entrySet()) {
            props.setProperty(entry.getKey(), entry.getValue());
        }

        try (FileOutputStream fos = new FileOutputStream(COMMENT_FILE)) {
            props.store(fos, "Comentários de vistas de base de dados - VetCare Manager");
        } catch (IOException e) {
            System.err.println("Erro ao gravar ficheiro de comentários: " + e.getMessage());
        }
    }
}
