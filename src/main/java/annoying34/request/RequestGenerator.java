package annoying34.request;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class RequestGenerator {
	public static String getRequestForInformationSubject() {
		return "Auskunft �ber meine Daten";
	}
	
	public static String getRequestForInformationBody(String name) {
		LocalDate inTwoWeeks = LocalDate.now().plusWeeks(2);
		Date date = Date.from(inTwoWeeks.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return getRequestForInformationBody(name, date);
	}

	public static String getRequestForInformationBody(String name, Date requestDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("Sehr geehrte Damen und Herren,\n\n"
				+ "auf der Grundlage von �� 34 Bundesdatenschutzgesetz (BDSG) "
				+ "bitte ich unentgeltlich um folgende Auskunft:\n"
				+ "Alle die �ber mich gespeicherten personenbezogenen Daten, "
				+ "deren Herkunft und den Zweck der Speicherung. "
				+ "Sollten die zu meiner Person gespeicherten Daten an Dritte �bermittelt worden sein, "
				+ "bitte ich um Auskunft �ber die Empf�nger mit Name und letztbekanner Anschrift.\n\n");
		sb.append("Bitte senden Sie mir die Daten an meine letzte Ihnen bekannte postalische Adresse. "
				+ "Ich setze Ihnen zur Erf�llung meiner Forderungen eine Frist bis zum ");
		sb.append(dateToString(requestDate) + ". ");
		sb.append("Sollten Sie dieses Schreiben ignorieren, "
				+ "werde ich mich an den zust�ndigen Landesdatenschutzbeauftragten wenden. "
				+ "Au�erdem behalte ich mir weitere rechtliche Schritte vor.\n\n"
				+ "Mit freundlichen Gr��en\n");
		sb.append(name);

		return sb.toString();
	}

	private static String dateToString(Date date) {
		SimpleDateFormat sdtF = new SimpleDateFormat("EEEE dd.MM.yyyy", Locale.GERMANY);
		return sdtF.format(date);
	}
}
