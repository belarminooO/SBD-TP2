package util;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class IdadeCalculator {

    public static String getIdadeFormatada(Date nascimento) {
        if (nascimento == null) return "N/A";
        
        LocalDate dataNasc;
        if (nascimento instanceof java.sql.Date) {
            dataNasc = ((java.sql.Date) nascimento).toLocalDate();
        } else {
            dataNasc = nascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        LocalDate agora = LocalDate.now();
        
        Period p = Period.between(dataNasc, agora);
        
        // Req 2.2: dias ou semanas ou meses ou anos
        if (p.getYears() > 0) {
            return p.getYears() + " anos";
        } else if (p.getMonths() > 0) {
            return p.getMonths() + " meses";
        } else {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(dataNasc, agora);
            if (dias > 7) {
                return (dias / 7) + " semanas";
            } else {
                return dias + " dias";
            }
        }
    }

    public static String getEscalaoEtario(Date nascimento, int expectativaVida) {
        if (nascimento == null) return "Desconhecido";
        
        LocalDate dataNasc;
        if (nascimento instanceof java.sql.Date) {
            dataNasc = ((java.sql.Date) nascimento).toLocalDate();
        } else {
            dataNasc = nascimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        LocalDate agora = LocalDate.now();
        int anos = Period.between(dataNasc, agora).getYears();
        
        // Generic approximation for Baby/Young/Adult/Senior
        // Assuming expectctancy is roughly "Senior" threshold or close to it.
        // Let's standardise: 
        // < 1 year: Bebé
        // 1 - 25% life: Jovem
        // 25% - 75%: Adulto
        // > 75%: Idoso
        
        if (anos < 1) return "Bebé";
        
        double ratio = (double) anos / (double) expectativaVida;
        
        if (ratio < 0.25) return "Jovem";
        if (ratio < 0.75) return "Adulto";
        return "Idoso";
    }
}
