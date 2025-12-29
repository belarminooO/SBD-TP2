package util;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Classe utilitária para leitura e escrita de dados na consola (Standard I/O).
 * 
 * Esta classe foi desenvolvida para ser robusta, tratando erros de formato e
 * garantindo
 * que o programa não termina inesperadamente devido a inputs inválidos. Fornece
 * métodos
 * seguros para leitura de diferentes tipos de dados (String, int, float,
 * double, datas)
 * com validação e tratamento de exceções integrados.
 * 
 * A classe utiliza um Scanner centralizado para evitar problemas de múltiplos
 * recursos
 * abertos e implementa conversões seguras com valores padrão em caso de erro.
 */
final public class IOx {

    /**
     * Stream de saída opcional para integração com ambientes Web/Servlets.
     */
    private static PrintWriter streamOut = null;

    /**
     * Formatador de data padrão seguindo o padrão Europeu (dia/mês/ano).
     */
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Formatador de data e hora padrão seguindo o padrão Europeu.
     */
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Scanner centralizado para evitar múltiplos recursos abertos.
     */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Fecha o Scanner para libertar os recursos de sistema.
     */
    public static void close() {
        if (sc != null) {
            sc.close();
        }
    }

    /**
     * Define um stream de saída alternativo para captura de logs.
     * 
     * @param p PrintWriter para saída alternativa.
     */
    public static void setOutStream(PrintWriter p) {
        streamOut = p;
    }

    /**
     * Solicita entrada de texto ao utilizador com validação de presença.
     * 
     * Se o campo for obrigatório, entra num ciclo até que algo válido seja escrito.
     * 
     * @param prompt      Mensagem a apresentar ao utilizador.
     * @param obrigatorio Define se o campo é obrigatório (não pode estar vazio).
     * @return String introduzida pelo utilizador.
     */
    public static String input(String prompt, boolean obrigatorio) {
        String res = "";
        do {
            System.out.print(prompt + (obrigatorio ? " (obrigatório): " : ": "));
            res = sc.nextLine().trim();
            if (obrigatorio && res.isEmpty()) {
                System.out.println("Este campo é obrigatório. Por favor, preencha-o.");
            }
        } while (obrigatorio && res.isEmpty());
        return res;
    }

    /**
     * Solicita entrada de texto obrigatória ao utilizador.
     * 
     * @param prompt Mensagem a apresentar ao utilizador.
     * @return String introduzida pelo utilizador (não vazia).
     */
    public static String input(String prompt) {
        return input(prompt, true);
    }

    /**
     * Lê um número inteiro da consola com conversão segura.
     * 
     * O método lê a linha inteira como texto e tenta converter para inteiro.
     * Se a conversão falhar, apresenta um aviso e retorna 0.
     * 
     * @param prompt Mensagem a apresentar ao utilizador.
     * @return Valor inteiro introduzido, ou 0 em caso de erro.
     */
    public static int inputInt(String prompt) {
        try {
            return Integer.parseInt(input(prompt, false));
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Assumindo o valor: 0");
            return 0;
        }
    }

    /**
     * Apresenta uma mensagem formatada ao utilizador.
     * 
     * @param msg Mensagem a apresentar.
     */
    public static void prompt(String msg) {
        System.out.println("\n[VETCARE]: " + msg);
    }

    /**
     * Lê um número decimal (float) da consola.
     * Suporta tanto o ponto (.) como a vírgula (,) como separador decimal.
     * 
     * @return Valor float convertido ou 0.0f se houver erro.
     */
    public static float inFloat() {
        String str = input("Introduza um número decimal", false).trim().replace(',', '.');
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            System.err.println("Aviso: '" + str + "' não é um número decimal válido. Retornando 0.0.");
            return 0.0f;
        }
    }

    /**
     * Lê um número de dupla precisão (double) da consola.
     * Suporta tanto o ponto (.) como a vírgula (,) como separador decimal.
     * 
     * @return Valor double convertido ou 0.0 se houver erro.
     */
    public static double inDouble() {
        String str = input("Introduza um número double", false).trim().replace(',', '.');
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            System.err.println("Aviso: '" + str + "' não é um double válido. Retornando 0.0.");
            return 0.0;
        }
    }

    /**
     * Lê uma data (LocalDate) no formato dd/mm/aaaa.
     * 
     * @return Objeto LocalDate lido ou a data atual (hoje) se o formato estiver
     *         errado.
     */
    public static LocalDate inDate() {
        String str = input("Introduza uma data (dd/mm/aaaa)", false).trim();
        try {
            return LocalDate.parse(str, DATE_FMT);
        } catch (DateTimeParseException e) {
            System.err.println("Erro: Data inválida. Use dd/mm/aaaa. Retornando hoje.");
            return LocalDate.now();
        }
    }

    /**
     * Lê data e hora (LocalDateTime) no formato dd/mm/aaaa hh:mm.
     * 
     * @return Objeto LocalDateTime lido ou o momento atual se o formato estiver
     *         errado.
     */
    public static LocalDateTime inDateTime() {
        String str = input("Introduza uma data e hora (dd/mm/aaaa hh:mm)", false).trim();
        try {
            return LocalDateTime.parse(str, DATETIME_FMT);
        } catch (DateTimeParseException e) {
            System.err.println("Erro: Data/Hora inválida. Use dd/mm/aaaa hh:mm. Retornando agora.");
            return LocalDateTime.now();
        }
    }

    /**
     * Lê o primeiro caracter de uma linha (útil para menus S/N).
     * 
     * @return Caracter lido em minúscula ou espaço se vazio.
     */
    public static char inChar() {
        String str = input("Introduza um caracter", false).trim().toLowerCase();
        if (str.length() > 0) {
            return str.charAt(0);
        }
        return ' ';
    }

    /**
     * Escreve uma linha de texto no Standard Output e no stream opcional, se
     * existir.
     * 
     * @param line Texto a exibir.
     */
    public static void out(String line) {
        if (line != null) {
            System.out.println(line);
            if (streamOut != null) {
                streamOut.println("<pre>" + line + "</pre>");
            }
        }
    }
}