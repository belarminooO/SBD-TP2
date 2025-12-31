package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

/**
 * Motor de processamento e normalização de nomes.
 * 
 * Implementa funcionalidades para:
 * - Normalização de capitalização e tratamento de partículas.
 * - Redução progressiva de nomes para ajuste a limites de caracteres.
 * - Deteção de género baseada no primeiro nome.
 * - Correção ortográfica de nomes comuns.
 * 
 * Suporta múltiplos idiomas (Português, Espanhol, Francês, Inglês)
 * e o tratamento de partículas, apóstrofos e hífens.
 */
public class Name {

    // --- COMPONENTE DE LOGGING ---

    /**
     * Utilitário interno para registo de mensagens (logs).
     */
    private static final class Log {
        public static void info(String message) {
            if (Configura.isWebEnvironment())
                System.out.println(message);
        }

        public static void warn(String message) {
            if (Configura.isWebEnvironment())
                System.err.println("AVISO: " + message);
        }

        public static void error(String message) {
            System.err.println("ERRO: " + message);
        }
    }

    // --- CONFIGURAÇÃO DE RECURSOS ---

    /** Caminho padrao para ficheiros de recursos (ambiente de desenvolvimento). */
    private static final String CORR_FILE = "corrector.txt";
    private static final String GENERO_FILE = "gender.txt";
    private static final String FALLBACK_PATH = "src/main/webapp/WEB-INF/resources/";

    private static boolean resourcesLoaded = false;
    private static volatile boolean isInitialized = false;

    private static ConcurrentHashMap<String, String> MAPA_CORR;
    private static ConcurrentHashMap<String, String> MAPA_GENERO;

    /**
     * Lista de conectores e artigos que devem permanecer em minúsculas
     * (Português, Espanhol, Francês, Inglês/Alemão).
     */
    private static final List<String> CONNECTORS = Arrays.asList(
            // PT
            "da", "de", "do", "das", "dos", "e", "os", "a", "o", "as", "por", "que", "para", "com", "sem", "ou",
            "em", "no", "na", "nos", "nas",
            // ES
            "del", "la", "las", "los", "el", "y",
            // FR
            "du", "le", "les",
            // EN/DE/Outros
            "of", "and", "the", "von", "van", "zu");

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    // --- MÉTODOS DE FALLBACK ---

    /**
     * Devolve um mapa base de correções para situações de falha no carregamento.
     */
    private static Map<String, String> getHardcodedCorrecoes() {
        return Map.of(
                "profirio", "Porfírio",
                "acacia", "Acácia",
                "goncalves", "Gonçalves",
                "luis", "Luís");
    }

    // Inicialização estática (tentativa de carregamento em ambiente local/IDE)
    static {
        if (!resourcesLoaded) {
            MAPA_CORR = new ConcurrentHashMap<>();
            MAPA_CORR.putAll(getHardcodedCorrecoes());
            MAPA_GENERO = new ConcurrentHashMap<>();

            // Tenta carregar recursos locais se disponiveis
            resourcesLoaded = loadResources(FALLBACK_PATH + CORR_FILE, MAPA_CORR) &&
                    loadResources(FALLBACK_PATH + GENERO_FILE, MAPA_GENERO);

            Log.info("Inicialização estática de Name.java concluída.");
        }
    }

    // --- INICIALIZAÇÃO WEB ---

    /**
     * Inicializa o motor com o caminho real dos recursos no servidor.
     * Deve ser invocado no arranque da aplicação Web.
     * 
     * @param path caminho absoluto para a pasta de recursos da aplicação
     */
    public static synchronized void initialize(String path) {
        if (isInitialized) {
            Log.warn("Name.java já se encontra inicializado.");
            return;
        }

        if (path != null && !path.isEmpty()) {
            path = path.endsWith("/") || path.endsWith("\\") ? path : path + "/";
            Log.info("Name.java inicializado com caminho: " + path);

            MAPA_CORR.clear();
            MAPA_CORR.putAll(getHardcodedCorrecoes());
            MAPA_GENERO.clear();

            isInitialized = loadResources(path + CORR_FILE, MAPA_CORR) &&
                    loadResources(path + GENERO_FILE, MAPA_GENERO);

            MAPA_CORR = (ConcurrentHashMap<String, String>) Collections.unmodifiableMap(MAPA_CORR);
            MAPA_GENERO = (ConcurrentHashMap<String, String>) Collections.unmodifiableMap(MAPA_GENERO);
        }
    }

    // --- MÉTODOS DE LEITURA DE RECURSOS ---

    /**
     * Carrega pares chave-valor de um ficheiro de texto para um mapa.
     * 
     * @param filePath  caminho do ficheiro
     * @param targetMap mapa de destino
     * @return true se carregado com sucesso
     */
    private static boolean loadResources(String filePath, Map<String, String> targetMap) {
        if (filePath == null) {
            return false;
        }

        Path path = Paths.get(filePath);

        if (Files.exists(path) && Files.isReadable(path)) {
            try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                readFromReader(br, targetMap);
                Log.info("Recursos carregados: " + targetMap.size() + " itens (" + filePath + ").");
            } catch (IOException e) {
                Log.error("Falha ao ler o ficheiro '" + filePath + "': " + e.getMessage());
                return false;
            }
        } else {
            Log.warn("Ficheiro de recursos não encontrado ou inacessível: " + filePath);
            return false;
        }
        return true;
    }

    /**
     * Processa o conteúdo do buffer e preenche o mapa.
     * Ignora linhas vazias ou comentários (#).
     * 
     * @param br  leitor buffered
     * @param map mapa de destino
     * @throws IOException erro de leitura
     */
    private static void readFromReader(BufferedReader br, Map<String, String> map) throws IOException {
        String linha;
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty() || linha.startsWith("#"))
                continue;
            String[] partes = linha.split("=", 2);
            if (partes.length == 2) {
                String chave = partes[0].trim().toLowerCase();
                String valor = partes[1].trim();
                map.put(chave, valor);
            }
        }
    }

    // ---LÓGICA DE PROCESSAMENTO DE NOMES---

    /**
     * Aplica correções ortográficas conhecidas a um nome.
     * 
     * @param name nome original
     * @return nome corrigido
     */
    private static String correct(String name) {
        String[] words = SPACE_PATTERN.split(name);
        StringBuilder correctedName = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            correctedName.append(MAPA_CORR.getOrDefault(word.toLowerCase(), word));
            if (i < words.length - 1)
                correctedName.append(" ");
        }
        return correctedName.toString();
    }

    /**
     * Abrevia a palavra numa posição específica.
     * 
     * @param name  nome completo
     * @param index índice da palavra a abreviar
     * @return nome com a alteração aplicada
     */
    private static String doAbbreviate(String name, int index) {
        String[] words = SPACE_PATTERN.split(name);
        int n = words.length;

        if (index < 0 || index >= n || words[index].endsWith(".")) {
            return name;
        }

        List<String> newWords = new ArrayList<>(Arrays.asList(words));
        String wordToAbbreviate = newWords.get(index);
        String abbreviatedWord;

        if (wordToAbbreviate.contains("'")) {
            int apostropheIndex = wordToAbbreviate.indexOf("'");
            String prefix = wordToAbbreviate.substring(0, apostropheIndex + 1);
            String initial = wordToAbbreviate.substring(apostropheIndex + 1, apostropheIndex + 2);
            abbreviatedWord = prefix + initial + ".";
        } else {
            abbreviatedWord = wordToAbbreviate.charAt(0) + ".";
        }

        newWords.set(index, abbreviatedWord);

        return String.join(" ", newWords);
    }

    /**
     * Estima o género com base no primeiro nome.
     * 
     * @param full_name nome completo
     * @return 'M' (Masculino), 'F' (Feminino) ou 'X' (Indeterminado)
     */
    public static String getGender(String full_name) {
        if (full_name == null || full_name.trim().isEmpty()) {
            return "X";
        }
        String[] words = SPACE_PATTERN.split(full_name.trim());
        if (words.length == 0) {
            return "X";
        }
        String gender = MAPA_GENERO.getOrDefault(words[0].toLowerCase(), "X");
        if (gender.equals("X"))
            gender = MAPA_GENERO.getOrDefault(correct(words[0]).toLowerCase(), "X");
        return gender;
    }

    /**
     * Normaliza a capitalização do nome.
     * 
     * @param name nome a normalizar
     * @return nome normalizado (Capitalização Title Case correta)
     */
    public static String normalize(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }

        // Limpeza inicial: trim, minusculas, espacos duplos, apostrofos
        String normalizedName = name.trim().toLowerCase().replaceAll("\\s+", " ");
        normalizedName = normalizedName.replaceAll("’", "'");
        String correctedName = correct(normalizedName);

        String[] words = SPACE_PATTERN.split(correctedName);
        StringBuilder finalName = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) {
                continue;
            }

            boolean isPureConnector = (i > 0) && CONNECTORS.contains(word.toLowerCase());
            boolean hasApostrophe = word.contains("'");
            boolean hasHifen = word.contains("-");

            String processedWord;

            if (isPureConnector) {
                processedWord = word.toLowerCase();
            } else if (hasApostrophe) {
                // Capitalização especial para d'Almeida, O'Connor, etc.
                int apostropheIndex = word.indexOf("'");
                String prefix = word.substring(0, apostropheIndex + 1).toLowerCase();
                String suffix = word.substring(apostropheIndex + 1);
                String capitalizedSuffix = (suffix.length() > 0)
                        ? Character.toUpperCase(suffix.charAt(0)) + suffix.substring(1).toLowerCase()
                        : "";
                processedWord = prefix + capitalizedSuffix;
            } else {
                // Capitalização normal
                processedWord = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();

                // Tratamento de nomes compostos com hífen
                if (hasHifen) {
                    processedWord = processedWord.replaceAll("-", " ");
                    processedWord = normalize(processedWord); // Recursividade simples para os componentes
                    processedWord = processedWord.replaceAll(" ", "-");
                }
            }

            finalName.append(processedWord);

            if (i < words.length - 1)
                finalName.append(" ");
        }
        return finalName.toString();
    }

    /**
     * Redução Fase 1: Abrevia a próxima palavra do meio elegivel.
     * 
     * @param name nome atual
     * @return nome com uma palavra intermédia abreviada
     */
    public static String abbreviateMiddle(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        String[] words = SPACE_PATTERN.split(name);
        int n = words.length;

        if (n <= 2) {
            return name;
        }

        int wordToAbbreviateIndex = -1;

        for (int i = 1; i <= n - 2; i++) {
            String word = words[i];
            if (CONNECTORS.contains(word.toLowerCase()))
                continue;
            if (word.endsWith("."))
                continue;
            wordToAbbreviateIndex = i;
            break;
        }

        if (wordToAbbreviateIndex == -1) {
            return name;
        }

        return doAbbreviate(name, wordToAbbreviateIndex);
    }

    /**
     * Redução Fase 2: Remove segmentos já abreviados.
     * 
     * @param name nome atual
     * @return nome sem o segmento abreviado
     */
    private static String stripSegment(String name) {
        String[] words = SPACE_PATTERN.split(name);
        List<String> newWords = new ArrayList<>(Arrays.asList(words));
        int n = newWords.size();
        if (n <= 2) {
            return name;
        }
        int indexToRemove = -1;

        for (int i = 1; i <= n - 2; i++) {
            if (newWords.get(i).endsWith(".")) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return name;
        }

        newWords.remove(indexToRemove);

        if (indexToRemove > 0) {
            String previousWord = newWords.get(indexToRemove - 1);
            if (CONNECTORS.contains(previousWord.toLowerCase())) {
                newWords.remove(indexToRemove - 1);
            }
        }
        return String.join(" ", newWords);
    }

    /**
     * Redução Fase 3: Remoção de conectores e palavras intermédias inteiras.
     * 
     * @param name nome atual
     * @return nome reduzido
     */
    private static String stripImmutable(String name) {
        String[] words = SPACE_PATTERN.split(name);
        List<String> newWords = new ArrayList<>(Arrays.asList(words));
        int n = newWords.size();

        if (n <= 2) {
            return name;
        }

        // 1. Remover conectores intermédios (ordem inversa)
        for (int i = newWords.size() - 1; i > 0; i--) {
            String word = newWords.get(i);
            if (CONNECTORS.contains(word.toLowerCase())) {
                newWords.remove(i);
                return String.join(" ", newWords);
            }
        }

        // 2. Remover palavras intermédias inteiras
        for (int i = 1; i < newWords.size() - 1; i++) {
            String word = newWords.get(i);
            if (!CONNECTORS.contains(word.toLowerCase()) && !word.endsWith(".")) {
                newWords.remove(i);
                return String.join(" ", newWords);
            }
        }

        // 3. Remover palavras nas extremidades (último recurso)
        int lastIndex = newWords.size() - 1;
        String lastWord = newWords.get(lastIndex);

        if (newWords.size() > 1 && !CONNECTORS.contains(lastWord.toLowerCase()) && !lastWord.endsWith(".")) {
            newWords.remove(lastIndex);
            return String.join(" ", newWords);
        }

        if (newWords.size() > 1) {
            String firstWord = newWords.get(0);
            if (!firstWord.endsWith(".") && !CONNECTORS.contains(firstWord.toLowerCase())) {
                newWords.remove(0);
                return String.join(" ", newWords);
            }
        }

        return name;
    }

    /**
     * Redução Fase 4: Abrevia o primeiro nome.
     */
    private static String abbreviateFirst(String name) {
        return doAbbreviate(name, 0);
    }

    /**
     * Redução Fase 5: Abrevia o último nome (apelido).
     */
    public static String abbreviateLast(String name) {
        return doAbbreviate(name, name.split(" ").length - 1);
    }

    /**
     * Reduz o nome para caber num tamanho máximo, aplicando várias estratégias em
     * sequência.
     * 
     * @param name    nome original
     * @param maxSize tamanho máximo permitido
     * @return nome reduzido
     */
    public static String shorten(String name, int maxSize) {
        String currentName = normalize(name);
        if (currentName == null || currentName.isEmpty() || currentName.length() <= maxSize) {
            return currentName;
        }

        // Fase 1: Abreviação intermédia
        String previousName = "";
        while (currentName.length() > maxSize && !currentName.equals(previousName)) {
            previousName = currentName;
            currentName = abbreviateMiddle(currentName);
        }

        // Fase 2: Remoção de segmentos abreviados
        if (currentName.length() > maxSize) {
            previousName = "";
            while (currentName.length() > maxSize && !currentName.equals(previousName)) {
                previousName = currentName;
                currentName = stripSegment(currentName);
            }
        }
        // Fase 3: Remoção de conectores
        if (currentName.length() > maxSize) {
            previousName = "";
            while (currentName.length() > maxSize && !currentName.equals(previousName)) {
                previousName = currentName;
                String nextName = stripImmutable(currentName);
                if (currentName.equals(nextName)) {
                    break;
                }
                currentName = nextName;
            }
        }

        // Fase 4: Abreviação do primeiro nome
        if (currentName.length() > maxSize) {
            currentName = abbreviateFirst(currentName);
        }

        // Fase 5: Abreviação do último nome
        if (currentName.length() > maxSize) {
            currentName = abbreviateLast(currentName);
        }

        // Fase 6: Redução drástica para iniciais
        if (currentName.length() > maxSize) {
            currentName = currentName.replaceAll("[ .]", "");
        }

        return currentName;
    }

    /**
     * Método de teste para execução local (Consola/IDE).
     */
    public static void main(String[] args) {

        Log.info("--- Teste Local Name.java ---");

        // Casos de teste
        String nomeOriginal = "Maria-Do-ceu Benedita Frôscolo Jovino D'Almeida MILITÃO De Sousa Baruel Dos Itaparica Boré SALVE-rainha Das abelhas";
        String nomeF = "Capitulina andrioleta da Conceicao do Corte-geral";
        String nomeM = "Joao-de-Deus acacio Techeremunga texugeiro";
        String nomeX = "Manarimba Bupatcha Medronheira";

        System.out.println("\n--- Normalização ---");
        String nomeNormalizado = normalize(nomeOriginal);
        System.out.println("Original: " + nomeOriginal);
        System.out.println("Normalizado: " + nomeNormalizado);
        System.out.println("Comprimento: " + nomeNormalizado.length());

        System.out.println("\n--- Testes de Redução ---");
        for (int i = 99; i > 1; i = i - 5) {
            String resultado = shorten(nomeOriginal, i);
            System.out.println("Max " + i + ": '" + resultado + "' (Len: " + resultado.length() + ")");
        }

        System.out.println("\n--- Género ---");
        System.out.println("1: " + getGender(nomeOriginal));
        System.out.println("2: " + getGender(nomeF));
        System.out.println("3: " + getGender(nomeM));
        System.out.println("4: " + getGender(nomeX));
    }
}