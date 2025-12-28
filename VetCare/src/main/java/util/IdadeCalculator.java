package util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class IdadeCalculator {

    public static String getIdadeFormatada(Date nascimento) {
        if (nascimento == null) return "N/A";
        LocalDate dataNasc = toLocalDate(nascimento);
        LocalDate reference = LocalDate.now();
        Period p = Period.between(dataNasc, reference);
        if (p.getYears() > 0) return p.getYears() + " anos";
        if (p.getMonths() > 0) return p.getMonths() + " meses";
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataNasc, reference);
        if (dias >= 7) return (dias / 7) + " semanas";
        return dias + " dias";
    }

    // Compatibilidade
    public static String getIdadeFormatada(Date nascimento, Date falecimento) {
        return getIdadeFormatada(nascimento);
    }

    public static String getIdadeDetalhada(Date nascimento) {
        if (nascimento == null) return "N/A";
        LocalDate dataNasc = toLocalDate(nascimento);
        LocalDate reference = LocalDate.now();
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataNasc, reference);
        long semanas = dias / 7;
        Period p = Period.between(dataNasc, reference);
        long totalMeses = java.time.temporal.ChronoUnit.MONTHS.between(dataNasc, reference);
        return String.format("%d d | %d sem | %d m | %d a", dias, semanas, totalMeses, p.getYears());
    }

    // Compatibilidade
    public static String getIdadeDetalhada(Date nascimento, Date falecimento) {
        return getIdadeDetalhada(nascimento);
    }

    public static String getEscalaoEtario(Date nascimento, int expectativaVida) {
        if (nascimento == null) return "Desconhecido";
        LocalDate dataNasc = toLocalDate(nascimento);
        LocalDate reference = LocalDate.now();
        int anos = Period.between(dataNasc, reference).getYears();
        if (anos < 1) return "Bebé";
        double ratio = (double) anos / (double) expectativaVida;
        if (ratio < 0.25) return "Jovem";
        if (ratio < 0.75) return "Adulto";
        return "Idoso";
    }

    // Compatibilidade
    public static String getEscalaoEtario(Date nascimento, Date falecimento, int expectativaVida) {
        return getEscalaoEtario(nascimento, expectativaVida);
    }

    private static LocalDate toLocalDate(Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
