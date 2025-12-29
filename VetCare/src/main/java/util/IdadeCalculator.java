package util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utilitário para cálculo de idade e escalão etário de animais.
 * 
 * Esta classe é fundamental para o cumprimento do requisito de apresentação de
 * idade
 * e escalão etário dos pacientes na ficha clínica. Implementa cálculos
 * proporcionais
 * relativos à expectativa de vida da espécie, permitindo classificar animais em
 * diferentes fases do ciclo de vida (Bebé, Jovem, Adulto, Idoso).
 * 
 * A classe oferece métodos para formatação de idade em diferentes escalas
 * temporais
 * (dias, semanas, meses, anos) e cálculo de escalão etário baseado na proporção
 * de vida decorrida relativamente à expectativa de vida da espécie.
 */
public class IdadeCalculator {

    /**
     * Calcula e formata a idade de forma legível para apresentação.
     * 
     * Aplica uma hierarquia de escala temporal:
     * - Se tem anos completos, apresenta em anos.
     * - Se tem menos de um ano mas mais de um mês, apresenta em meses.
     * - Se tem menos de um mês mas mais de uma semana, apresenta em semanas.
     * - Caso contrário, apresenta em dias.
     * 
     * @param nascimento Data de nascimento do animal.
     * @return String formatada com a idade (ex: "3 anos", "5 meses", "2 semanas",
     *         "10 dias").
     */
    public static String getIdadeFormatada(Date nascimento) {
        if (nascimento == null)
            return "N/A";
        LocalDate dataNasc = toLocalDate(nascimento);
        LocalDate reference = LocalDate.now();
        Period p = Period.between(dataNasc, reference);

        if (p.getYears() > 0)
            return p.getYears() + " anos";
        if (p.getMonths() > 0)
            return p.getMonths() + " meses";

        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataNasc, reference);
        if (dias >= 7)
            return (dias / 7) + " semanas";
        return dias + " dias";
    }

    /**
     * Calcula e formata a idade com todas as métricas temporais.
     * 
     * Devolve uma representação detalhada incluindo dias, semanas, meses e anos,
     * útil para registos de crescimento e acompanhamento veterinário.
     * 
     * @param nascimento Data de nascimento do animal.
     * @return String formatada com todas as métricas (ex: "100 d | 14 sem | 3 m | 0
     *         a").
     */
    public static String getIdadeDetalhada(Date nascimento) {
        if (nascimento == null)
            return "N/A";
        LocalDate dataNasc = toLocalDate(nascimento);
        LocalDate reference = LocalDate.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataNasc, reference);
        long semanas = dias / 7;
        Period p = Period.between(dataNasc, reference);
        long totalMeses = java.time.temporal.ChronoUnit.MONTHS.between(dataNasc, reference);
        return String.format("%d d | %d sem | %d m | %d a", dias, semanas, totalMeses, p.getYears());
    }

    /**
     * Determina o escalão etário do animal baseado na proporção de vida decorrida.
     * 
     * A classificação baseia-se na percentagem de vida vivida relativamente à
     * expectativa de vida da espécie:
     * - Bebé: Menos de 1 ano de idade.
     * - Jovem: Viveu menos de 25% da expectativa de vida.
     * - Adulto: Entre 25% e 75% da expectativa de vida.
     * - Idoso: Mais de 75% da expectativa de vida.
     * 
     * Este cálculo permite uma classificação etária relativa, onde um cão de 10
     * anos
     * pode ser considerado idoso se a sua raça viver tipicamente 12 anos, ou adulto
     * se a expectativa for de 16 anos.
     * 
     * @param nascimento      Data de nascimento do animal.
     * @param expectativaVida Expectativa de vida da espécie em anos.
     * @return String representando o escalão etário ("Bebé", "Jovem", "Adulto",
     *         "Idoso", "Desconhecido").
     */
    public static String getEscalaoEtario(Date nascimento, int expectativaVida) {
        if (nascimento == null)
            return "Desconhecido";
        LocalDate dataNasc = toLocalDate(nascimento);
        LocalDate reference = LocalDate.now();
        int anos = Period.between(dataNasc, reference).getYears();

        if (anos < 1)
            return "Bebé";

        double proporcao = (double) anos / (double) expectativaVida;

        if (proporcao < 0.25)
            return "Jovem";
        if (proporcao < 0.75)
            return "Adulto";
        return "Idoso";
    }

    /**
     * Converte um objeto java.util.Date para java.time.LocalDate.
     * 
     * Faz a ponte entre a API legada de datas (utilizada pelo JDBC) e a API moderna
     * java.time (mais precisa para cálculos de calendário).
     * 
     * @param date Data a converter.
     * @return LocalDate equivalente.
     */
    private static LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
