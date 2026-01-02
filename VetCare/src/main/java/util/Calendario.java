package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilitário para gestão de calendário, feriados e geração de visualizações
 * temáticas.
 * Suporta o cálculo de feriados nacionais (fixos e móveis) e municipais em
 * Portugal,
 * permitindo ainda a exportação de calendários em formato HTML com diversos
 * temas.
 */
public class Calendario {

    /** Configuração regional para Portugal. */
    private static final Locale LOCALE_PT = Locale.forLanguageTag("pt-PT");

    /** Formatador para exibição de data por extenso. */
    private static final DateTimeFormatter FORMATO_EXTENSO = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' uuuu",
            LOCALE_PT);

    /** Formatador para leitura de datas em formato de entrada simples. */
    private static final DateTimeFormatter FORMATO_INPUT = DateTimeFormatter.ofPattern("d/M/yyyy");

    /** Formatador curto para dia e mês. */
    private static final DateTimeFormatter FORMATO_CURTO = DateTimeFormatter.ofPattern("dd/MM");

    /** Regiões suportadas para feriados municipais. */

    /**
     * Identifica se uma determinada data corresponde a um fim de semana.
     * 
     * @param data A data a analisar.
     * @return Verdadeiro se for sábado ou domingo.
     */
    public static boolean fimDeSemana(LocalDate data) {
        return data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * Verifica a ocorrência de feriados municipais para as regiões suportadas.
     * 
     * @param data   A data a analisar.
     * @param regiao A região administrativa pretendida.
     * @return Verdadeiro se coincidir com um feriado municipal da região.
     */
    public static boolean feriadoMunicipal(LocalDate data, Regiao regiao) {
        if (regiao == null)
            return false;
        if (regiao == Regiao.LISBOA)
            return data.getMonth() == Month.JUNE && data.getDayOfMonth() == 13;
        if (regiao == Regiao.PORTO || regiao == Regiao.BRAGA)
            return data.getMonth() == Month.JUNE && data.getDayOfMonth() == 24;
        if (regiao == Regiao.SINTRA || regiao == Regiao.EVORA)
            return data.getMonth() == Month.JUNE && data.getDayOfMonth() == 29;
        return false;
    }

    /**
     * Obtém a descrição completa do feriado nacional ou municipal associado à data.
     * 
     * @param data A data a analisar.
     * @return Descrição do feriado ou uma string vazia se não for feriado.
     */
    public static String getDescricaoCompleta(LocalDate data) {
        if (data.getMonth() == Month.JANUARY && data.getDayOfMonth() == 1)
            return "Ano Novo";
        if (data.getMonth() == Month.APRIL && data.getDayOfMonth() == 25)
            return "Dia da Liberdade";
        if (data.getMonth() == Month.MAY && data.getDayOfMonth() == 1)
            return "Dia do Trabalhador";
        if (data.getMonth() == Month.JUNE && data.getDayOfMonth() == 10)
            return "Dia de Portugal";
        if (data.getMonth() == Month.AUGUST && data.getDayOfMonth() == 15)
            return "Assunção de Nossa Senhora";
        if (data.getMonth() == Month.OCTOBER && data.getDayOfMonth() == 5)
            return "Implantação da República";
        if (data.getMonth() == Month.NOVEMBER && data.getDayOfMonth() == 1)
            return "Todos os Santos";
        if (data.getMonth() == Month.DECEMBER && data.getDayOfMonth() == 1)
            return "Restauração da Independência";
        if (data.getMonth() == Month.DECEMBER && data.getDayOfMonth() == 8)
            return "Imaculada Conceição";
        if (data.getMonth() == Month.DECEMBER && data.getDayOfMonth() == 25)
            return "Natal";

        LocalDate pascoa = getDomingoPascoa(data.getYear());
        if (data.equals(pascoa.minusDays(47)))
            return "Carnaval";
        if (data.equals(pascoa.minusDays(2)))
            return "Sexta-feira Santa";
        if (data.equals(pascoa))
            return "Páscoa";
        if (data.equals(pascoa.plusDays(60)))
            return "Corpo de Deus";

        List<String> locais = new ArrayList<>();
        for (Regiao r : Regiao.values()) {
            if (feriadoMunicipal(data, r)) {
                locais.add(r.name());
            }
        }
        return locais.isEmpty() ? "" : "Feriado Local: " + String.join(", ", locais);
    }

    /**
     * Calcula a data do Domingo de Páscoa utilizando o algoritmo de Butcher-Meeus.
     * 
     * @param ano O ano para o qual se pretende calcular.
     * @return Data do Domingo de Páscoa.
     */
    public static LocalDate getDomingoPascoa(int ano) {
        int a = ano % 19, b = ano / 100, c = ano % 100, d = b / 4, e = b % 4, f = (b + 8) / 25,
                g = (b - f + 1) / 3, h = (19 * a + b - d - g + 15) % 30, i = c / 4, k = c % 4,
                l = (32 + 2 * e + 2 * i - h - k) % 7, m = (a + 11 * h + 22 * l) / 451,
                n = (h + l - 7 * m + 114) / 31, p = (h + l - 7 * m + 114) % 31;
        return LocalDate.of(ano, n, p + 1);
    }

    /**
     * Insere o conteúdo HTML do calendário num gerador de texto.
     * 
     * @param w           Instância de PrintWriter para escrita.
     * @param ano         Ano do calendário.
     * @param opcaoModelo Identificador do modelo visual.
     */
    private static void appendCalendarContent(PrintWriter w, int ano, int opcaoModelo) {
        String[] nomes = { "", "luxo", "premium", "purpura", "impressao", "terminal", "gotico", "celta", "ascii",
                "culinaria", "hotelaria", "veterinaria" };

        String[] iconesGotico = { "", "🌑", "🐺", "🌑", "🕯️", "⚔️", "🏰", "🍷", "🦇", "📜", "🏹", "🗡️", "⛓️" };
        String[] iconesCelta = { "", "🌑", "⚔️", "🐺", "🏰", "🛡️", "🏹", "📜", "🦅", "🕯️", "🌲", "🏔️", "🔥" };
        String[] iconesCulinaria = { "", "🍊", "🍫", "🥦", "🥗", "🍓", "🍒", "🍦", "🍉", "🍇", "🎃", "🌰", "🍪" };
        String[] iconesHotelaria = { "", "🔑", "🛌", "🧖", "☂️", "🍹", "🏊", "🚿", "🌅", "🛎️", "🍷", "🛋️", "🎁" };
        String[] iconesVeterinaria = { "", "🐱", "🐶", "🐰", "🐥", "🐣", "🦋", "🐠", "🦎", "🦜", "🐕", "🐈", "🦌" };

        String modelo = (opcaoModelo > 0 && opcaoModelo < nomes.length) ? nomes[opcaoModelo] : "impressao";
        List<LocalDate> feriadosEncontrados = new ArrayList<>();

        w.println("<!DOCTYPE html><html lang='pt-PT'><head><meta charset='UTF-8'/>");

        if (modelo.equals("gotico")) {
            w.println(
                    "<link href='https://fonts.googleapis.com/css2?family=UnifrakturMaguntia&display=swap' rel='stylesheet'>");
        } else if (Arrays.asList("celta", "purpura", "luxo").contains(modelo)) {
            w.println(
                    "<link href='https://fonts.googleapis.com/css2?family=Cinzel+Decorative:wght@700&family=Eagle+Lake&family=Orbitron&display=swap' rel='stylesheet'>");
        }

        w.println("<style>");
        w.println(
                ".grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)); gap: 20px; padding: 20px; text-align: center; }");
        w.println(
                ".mes { padding: 15px; border-radius: 12px; } table { width: 100%; border-collapse: collapse; } td { padding: 10px; border: 1px solid rgba(128,128,128,0.1); }");
        w.println(
                ".feriado-mun { opacity: 0.6; filter: brightness(0.85); border: 1px dashed rgba(0,0,0,0.3) !important; }");
        w.println("body { margin: 0; padding: 0; background: #fff; color: #000; font-family: sans-serif; }");

        switch (modelo) {
            case "luxo":
                w.println("body { background: #0f0f0f; color: #fff; font-family: 'Cinzel Decorative', serif; }");
                w.println(
                        "h1 { color: #D4AF37; } .mes { background: #1a1a1a; border: 1px solid #D4AF37; } h2 { color: #D4AF37; } .feriado-nac { background: #D4AF37; color: #000; }");
                break;
            case "terminal":
                w.println("body { background: #1a1a1a; color: #33ff33; font-family: 'Courier New', monospace; }");
                w.println(".mes { border: 2px solid #33ff33; } .feriado-nac { color: #000; background: #33ff33; }");
                break;
            case "gotico":
                w.println(
                        "body { background: #0a0a0a; color: #b0b0b0; font-family: 'UnifrakturMaguntia', serif; } h1 { color: #8b0000; } .mes { border: 2px solid #333; background: #1a1a1a; } .feriado-nac { background: #4a0000; color: #ff0000; }");
                break;
            case "celta":
                w.println(
                        "body { background: #001a33; color: #D4AF37; font-family: 'Eagle Lake', cursive; } .mes { background: #002b55; border: 3px double #D4AF37; } .feriado-nac { background: #D4AF37; color: #001a33; }");
                break;
            case "purpura":
                w.println(
                        "body { background: #1a0033; color: #fff; font-family: 'Orbitron', sans-serif; } .mes { background: #2d0059; border: 1px solid #bc13fe; } .feriado-nac { background: #bc13fe; color: #fff; }");
                break;
            case "culinaria":
                w.println(
                        "body { background: #fffcf5; color: #5d4037; } .mes { background: #fff; border: 2px solid #ff7043; } .feriado-nac { background: #ffccbc; color: #bf360c; }");
                break;
            case "veterinaria":
                w.println(
                        "body { background: #e0f2f1; color: #004d40; } .mes { background: #fff; border: 3px dashed #4db6ac; border-radius: 40px; } .feriado-nac { background: #b2dfdb; }");
                break;
            case "hotelaria":
                w.println(
                        "body { background: #f4f4f4; color: #2c3e50; } .mes { background: #fff; border-top: 5px solid #c5a059; } .feriado-nac { background: #c5a059; color: white; }");
                break;
            default:
                w.println(".mes { border: 1px solid #000; } .feriado-nac { background: #eee; }");
        }

        w.println(
                ".fo  oter-legend { margin-top: 40px; border-top: 1px solid gray; padding: 20px; display: flex; flex-wrap: wrap; justify-content: center; gap: 8px; }");
        w.println(".tag {    border: 1px solid gray; padding: 5px 10px; border-radius: 15px; font-size: 0.8em; }");
        w.println("</style></head><body>");

        String tituloPrincipal = modelo.equals("gotico") ? "ANNO DOMINI " + ano
                : "CALENDÁRIO " + modelo.toUpperCase() + " " + ano;
        w.println("<h1 style='text-align:center;'>" + tituloPrincipal + "</h1><div class='grid'>");

        for (int m = 1; m <= 12; m++) {
            LocalDate dataMes = LocalDate.of(ano, m, 1);
            String prefixo = "";
            if (modelo.equals("gotico"))
                prefixo = iconesGotico[m] + " ";
            else if (modelo.equals("celta"))
                prefixo = iconesCelta[m] + " ";
            else if (modelo.equals("culinaria"))
                prefixo = iconesCulinaria[m] + " ";
            else if (modelo.equals("hotelaria"))
                prefixo = iconesHotelaria[m] + " ";
            else if (modelo.equals("veterinaria"))
                prefixo = iconesVeterinaria[m] + " ";

            w.println("<div class='mes'><h2>" + prefixo
                    + dataMes.getMonth().getDisplayName(TextStyle.FULL, LOCALE_PT).toUpperCase() + "</h2>");
            w.println("<table><tr><th>D</th><th>S</th><th>T</th><th>Q</th><th>Q</th><th>S</th><th>S</th></tr><tr>");

            int espacos = dataMes.getDayOfWeek().getValue() % 7;
            for (int i = 0; i < espacos; i++)
                w.println("<td></td>");

            while (dataMes.getMonthValue() == m) {
                String desc = getDescricaoCompleta(dataMes);
                String cl = "";

                if (!desc.isEmpty()) {
                    cl = desc.contains("Local") ? "feriado-mun" : "feriado-nac";
                    if (!feriadosEncontrados.contains(dataMes)) {
                        feriadosEncontrados.add(dataMes);
                    }
                }

                w.println("<td class='" + cl + "' title='" + desc + "'>" + dataMes.getDayOfMonth() + "</td>");
                if (dataMes.getDayOfWeek() == DayOfWeek.SATURDAY)
                    w.println("</tr><tr>");
                dataMes = dataMes.plusDays(1);
            }
            w.println("</tr></table></div>");
        }
        w.println("</div><div class='footer-legend'>");

        for (LocalDate f : feriadosEncontrados) {
            w.println("<div class='tag'><b>" + f.format(FORMATO_CURTO) + "</b>: " + getDescricaoCompleta(f) + "</div>");
        }
        w.println("</div></body></html>");
    }

    /**
     * Gera um ficheiro HTML com o calendário para o ano e modelo especificados.
     * 
     * @param ano         O ano do calendário.
     * @param opcaoModelo O índice do modelo visual pretendido.
     */
    public static void gerarCalendarioHTML(int ano, int opcaoModelo) {
        String[] nomes = { "", "luxo", "premium", "purpura", "impressao", "terminal", "gotico", "celta", "ascii",
                "culinaria", "hotelaria", "veterinaria" };
        String modelo = (opcaoModelo > 0 && opcaoModelo < nomes.length) ? nomes[opcaoModelo] : "impressao";
        String nomeFicheiro = "Calendario_" + modelo + "_" + ano + ".html";

        try (PrintWriter w = new PrintWriter(new FileWriter(nomeFicheiro))) {
            appendCalendarContent(w, ano, opcaoModelo);
        } catch (IOException e) {
            System.err.println("Erro na geração do ficheiro de calendário: " + e.getMessage());
        }
    }

    /**
     * Gera e retorna o conteúdo HTML do calendário como uma string.
     * 
     * @param ano         O ano do calendário.
     * @param opcaoModelo O índice do modelo visual pretendido.
     * @return String contendo o código HTML gerado.
     */
    public static String gerarCalendarioHTMLtoString(int ano, int opcaoModelo) {
        StringWriter sw = new StringWriter();
        PrintWriter w = new PrintWriter(sw);
        try {
            appendCalendarContent(w, ano, opcaoModelo);
            return sw.toString();
        } finally {
            w.close();
        }
    }

    /**
     * Determina o estado operacional de um dia específico para fins informativos.
     * 
     * @param data A data a analisar.
     * @return Descrição do estado (Feriado, Fim de semana ou Dia útil).
     */
    public static String getEstadoDia(LocalDate data) {
        String descricaoFeriado = getDescricaoCompleta(data);
        if (!descricaoFeriado.isEmpty()) {
            return descricaoFeriado;
        }
        if (fimDeSemana(data)) {
            return "Fim de semana";
        }
        return "Dia útil";
    }
}
