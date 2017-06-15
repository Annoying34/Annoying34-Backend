package annoying34.request;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class RequestGenerator {
	public static String getRequestForInformationSubject() {
		return "Auskunft über meine Daten";
	}
	
	public static String getRequestForInformationBody(String name) {
		LocalDate inTwoWeeks = LocalDate.now().plusWeeks(2);
		Date date = Date.from(inTwoWeeks.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return getRequestForInformationBody(name, date);
	}

	public static String getRequestForInformationBody(String name, Date requestDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("Sehr geehrte Damen und Herren,\n\n"
				+ "auf der Grundlage von §§ 34 Bundesdatenschutzgesetz (BDSG) "
				+ "bitte ich unentgeltlich um folgende Auskunft:\n"
				+ "Alle die über mich gespeicherten personenbezogenen Daten, "
				+ "deren Herkunft und den Zweck der Speicherung. "
				+ "Sollten die zu meiner Person gespeicherten Daten an Dritte übermittelt worden sein, "
				+ "bitte ich um Auskunft über die Empfänger mit Name und letztbekanner Anschrift.\n\n");
		sb.append("Bitte senden Sie mir die Daten an meine letzte Ihnen bekannte postalische Adresse. "
				+ "Ich setze Ihnen zur Erfüllung meiner Forderungen eine Frist bis zum ");
		sb.append(dateToString(requestDate) + ". ");
		sb.append("Sollten Sie dieses Schreiben ignorieren, "
				+ "werde ich mich an den zuständigen Landesdatenschutzbeauftragten wenden. "
				+ "Außerdem behalte ich mir weitere rechtliche Schritte vor.\n\n"
				+ "Mit freundlichen Grüßen\n");
		sb.append(name);

		return sb.toString();
	}

	private static String dateToString(Date date) {
		SimpleDateFormat sdtF = new SimpleDateFormat("EEEE dd.MM.yyyy", Locale.GERMANY);
		return sdtF.format(date);
	}
}
