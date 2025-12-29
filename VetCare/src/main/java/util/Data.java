package util;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Utilitário para operações aritméticas e de formatação de datas.
 * 
 * Esta classe fornece métodos para manipulação de datas, cálculo de idades,
 * diferenças temporais e conversão entre diferentes representações de data.
 * Faz a ponte entre o sistema legado java.util.Date e o moderno
 * java.time.LocalDate,
 * oferecendo métodos expressivos para operações comuns de calendário.
 */
public class Data {

	/**
	 * Data mínima suportada pelo sistema (limite do Java).
	 */
	static LocalDate MIN = LocalDate.MIN;

	/**
	 * Data máxima suportada pelo sistema.
	 */
	static LocalDate MAX = LocalDate.MAX;

	/**
	 * Verifica se um ano é bissexto (possui 366 dias).
	 * 
	 * @param ano Ano a verificar.
	 * @return String indicando se o ano é "bissexto" ou "não bissexto".
	 */
	public static String bissexto(int ano) {
		if (Year.of(ano).isLeap())
			return "bissexto";
		return "não bissexto";
	}

	/**
	 * Calcula a data correspondente a depois de amanhã.
	 * 
	 * @return LocalDate representando depois de amanhã.
	 */
	public static LocalDate depoisAmanha() {
		return LocalDate.now().plusDays(2);
	}

	/**
	 * Calcula a data correspondente a anteontem.
	 * 
	 * @return LocalDate representando anteontem.
	 */
	public static LocalDate anteOntem() {
		return LocalDate.now().minusDays(2);
	}

	/**
	 * Calcula a data correspondente a amanhã.
	 * 
	 * @return LocalDate representando amanhã.
	 */
	public static LocalDate amanha() {
		return diaSeguinte(LocalDate.now());
	}

	/**
	 * Calcula a data correspondente a ontem.
	 * 
	 * @return LocalDate representando ontem.
	 */
	public static LocalDate ontem() {
		return diaAnterior(LocalDate.now());
	}

	/**
	 * Calcula o dia seguinte a uma data fornecida.
	 * 
	 * @param data Data de referência.
	 * @return LocalDate representando o dia seguinte.
	 */
	public static LocalDate diaSeguinte(LocalDate data) {
		return data.plusDays(1);
	}

	/**
	 * Calcula o dia anterior a uma data fornecida.
	 * 
	 * @param data Data de referência.
	 * @return LocalDate representando o dia anterior.
	 */
	public static LocalDate diaAnterior(LocalDate data) {
		return data.minusDays(1);
	}

	/**
	 * Calcula a diferença absoluta em dias entre duas datas.
	 * 
	 * @param antes  Data inicial.
	 * @param depois Data final.
	 * @return Número de dias entre as duas datas.
	 */
	public static long diferencaEmDias(LocalDate antes, LocalDate depois) {
		return antes.until(depois, ChronoUnit.DAYS);
	}

	/**
	 * Calcula a idade civil em anos completos entre duas datas.
	 * 
	 * @param nascimento Data de nascimento.
	 * @param hoje       Data de referência para cálculo da idade.
	 * @return Idade em anos completos.
	 */
	public static long idade(LocalDate nascimento, LocalDate hoje) {
		return nascimento.until(hoje, ChronoUnit.YEARS);
	}

	/**
	 * Subtrai um número de anos a uma data de referência.
	 * 
	 * @param ref   Data de referência.
	 * @param years Número de anos a subtrair.
	 * @return LocalDate resultante da subtração.
	 */
	public static LocalDate subtrairAnos(LocalDate ref, long years) {
		return ref.minusYears(years);
	}

	/**
	 * Adiciona um número de anos a uma data de referência.
	 * 
	 * @param ref   Data de referência.
	 * @param years Número de anos a adicionar.
	 * @return LocalDate resultante da adição.
	 */
	public static LocalDate somarAnos(LocalDate ref, long years) {
		return ref.plusYears(years);
	}

	/**
	 * Retorna o nome do dia da semana em português para uma data fornecida.
	 * 
	 * @param data Data para consulta.
	 * @return Nome do dia da semana em português.
	 */
	public static String diaSemana(LocalDate data) {
		switch (data.getDayOfWeek().getValue()) {
			case 1:
				return "segunda-feira";
			case 2:
				return "terça-feira";
			case 3:
				return "quarta-feira";
			case 4:
				return "quinta-feira";
			case 5:
				return "sexta-feira";
			case 6:
				return "sábado";
			case 7:
				return "domingo";
		}
		return "?";
	}

	/**
	 * Método auxiliar para formatação com padding à esquerda.
	 * 
	 * @param value  Valor a formatar.
	 * @param length Comprimento desejado.
	 * @return Substring com o comprimento especificado.
	 */
	private static String right(String value, int length) {
		return value.substring(value.length() - length);
	}

	/**
	 * Formata uma data no formato clássico DD/MM/AAAA.
	 * 
	 * @param dt Data a formatar.
	 * @return String formatada no padrão DD/MM/AAAA.
	 */
	public static String show(LocalDate dt) {
		return right("0" + dt.getDayOfMonth(), 2) + "/" + right("0" + (dt.getMonthValue()), 2) + "/" + dt.getYear();
	}

	/**
	 * Formata uma data no formato ISO 8601 (AAAA-MM-DD).
	 * Este formato é ideal para armazenamento em bases de dados.
	 * 
	 * @param dt Data a formatar.
	 * @return String formatada no padrão AAAA-MM-DD.
	 */
	public static String showISO(LocalDate dt) {
		return dt.getYear() + "-" + right("0" + (dt.getMonthValue()), 2) + "-" + right("0" + dt.getDayOfMonth(), 2);
	}

	/**
	 * Converte um objeto java.util.Date para java.time.LocalDate.
	 * Faz a ponte entre a API legada e a API moderna de datas.
	 * 
	 * @param dateToConvert Data a converter.
	 * @return LocalDate equivalente.
	 */
	public static LocalDate convertToLocalDate(Date dateToConvert) {
		return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
	}

	/**
	 * Gera um resumo biográfico temporal de uma data.
	 * 
	 * @param data Data a analisar.
	 * @return String com informações detalhadas sobre a data.
	 */
	public static String saber(Date data) {
		return saber(convertToLocalDate(data));
	}

	/**
	 * Gera um resumo biográfico temporal de uma data.
	 * Inclui idade, dias vividos, data anterior e dia da semana de nascimento.
	 * 
	 * @param data Data a analisar.
	 * @return String com informações detalhadas sobre a data.
	 */
	public static String saber(LocalDate data) {
		return "tem " + idade(data, LocalDate.now()) + " anos de idade, já viveu " + diferencaEmDias(data, LocalDate.now())
				+
				" dias, nasceu na véspera de " + show(diaSeguinte(data)) + ", num" +
				((diaSemana(data).compareToIgnoreCase("domingo") == 0 || diaSemana(data).compareToIgnoreCase("sábado") == 0)
						? ""
						: "a")
				+
				" " + diaSemana(data) + " de um ano " + bissexto(data.getYear());
	}

	/**
	 * Retorna o número de dias desde a época Unix (1970-01-01).
	 * 
	 * @return Número de dias desde a época.
	 */
	public static long epochDay() {
		return LocalDate.now().toEpochDay();
	}

	/**
	 * Retorna a era cronológica atual.
	 * 
	 * @return String representando a era (ex: "Anno Domini").
	 */
	public static String era() {
		return LocalDate.now().getEra().getDisplayName(java.time.format.TextStyle.FULL,
				java.util.Locale.forLanguageTag("pt-PT"));
	}

	/**
	 * Descreve as propriedades de uma data para fins de depuração.
	 * Apresenta informações sobre anos, meses e dias adjacentes.
	 * 
	 * @param MyDate Data em formato String (ISO 8601).
	 * @param out    PrintWriter para saída dos dados.
	 */
	public static void descreve(String MyDate, PrintWriter out) {
		LocalDate dt = LocalDate.parse(MyDate);
		out.println("===================================");
		out.println("Referência (" + (dt.isLeapYear() ? "bissexto" : "não bissexto") + "): " + MyDate);
		out.println("-----------------------------------");
		out.println("Ano anterior < " + dt.plusYears(-1));
		out.println("Ano seguinte > " + dt.plusYears(1));
		out.println("Mês anterior < " + dt.minusMonths(1));
		out.println("Mês seguinte > " + dt.plusMonths(1));
		out.println("Dia anterior < " + dt.minusDays(1));
		out.println("Dia seguinte > " + dt.plusDays(1));
		out.println("===================================");
		out.flush();
	}

	/**
	 * Método principal para testes e demonstração.
	 * 
	 * @param args Argumentos de linha de comando (não utilizados).
	 */
	public static void main(String[] args) {
		System.out.println(saber(LocalDate.parse("1965-09-08")));
	}
}
