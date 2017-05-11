package annoy34.imap;

import java.util.Set;

public class Test {
	private static final String EMAIL_ADDRESS = "annoying34@gmx.de";
	private static final String EMAIL_PASSWORD = "annoy34mePLS";
	private static final String IMAP_SERVER = "imap.gmx.de";
	private static final String SMTP_SERVER = "mail.gmx.com";
	
	private static final String RECIPIENT = "annoying34@gmx.de";

	public static void main(String[] args) {
		retrieveMails();
		System.out.println();
		sendMail();
	}
	
	public static void retrieveMails() {
		try {
			long start = System.currentTimeMillis();
			
			ImapQuery query = new ImapQuery(EMAIL_ADDRESS, EMAIL_PASSWORD, IMAP_SERVER);
			Set<MailAddress> senderAddresses = query.getSenderMailAddresses();
			
			System.out.println("Retrieval took " + new Long(System.currentTimeMillis() - start) + "ms");
			
			System.out.println(senderAddresses);
			System.out.println(ImapQuery.getDomains(senderAddresses));

		} catch (ImapException e) {
			e.printStackTrace();
		}
	}
	
	public static void sendMail() {
		try {
			long start = System.currentTimeMillis();
			
			ImapSender sender = new ImapSender(EMAIL_ADDRESS, EMAIL_PASSWORD, SMTP_SERVER);
			sender.sendMail(RECIPIENT, "Anfrage bzgl. meiner Daten", "Gimme all my data pls");
			
			System.out.println("Sending took " + new Long(System.currentTimeMillis() - start) + "ms");
		} catch (ImapException e) {
			e.printStackTrace();
		}
	}
}
