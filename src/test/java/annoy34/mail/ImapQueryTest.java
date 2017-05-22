package annoy34.mail;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import annoy34.mail.ImapException;
import annoy34.mail.ImapQuery;
import annoy34.mail.MailAddress;

public class ImapQueryTest {
	private static final String EMAIL_ADDRESS = "annoying34@gmx.de";
	private static final String EMAIL_PASSWORD = "annoy34mePLS";
	private static final String IMAP_SERVER = "imap.gmx.de";
	
	@Test
	public void testConnection() throws ImapException {
		new ImapQuery(EMAIL_ADDRESS, EMAIL_PASSWORD, IMAP_SERVER);
	}

	@Test(expected=ImapException.class)
	public void testInvalidConnection1() throws ImapException {
		new ImapQuery("SomeOtherHopefullyNonExistingMailAddress@gmx.de", EMAIL_PASSWORD, IMAP_SERVER);
	}

	@Test(expected=ImapException.class)
	public void testInvalidConnection2() throws ImapException {
		new ImapQuery(EMAIL_ADDRESS, "not a valid password", IMAP_SERVER);
	}
	
	@Test(expected=ImapException.class)
	public void testInvalidConnection3() throws ImapException {
		new ImapQuery(EMAIL_ADDRESS, EMAIL_PASSWORD, "invalid.imap.server");
	}
	
	@Test
	public void testRetrieval() throws ImapException {
		ImapQuery query = new ImapQuery(EMAIL_ADDRESS, EMAIL_PASSWORD, IMAP_SERVER);
		Set<String> senderDomains = query.getSenderDomains();
		assertTrue(senderDomains.contains("produkt.gmx.net"));
		assertTrue(senderDomains.contains("gmx.de"));
		assertTrue(senderDomains.contains("sicher.gmx.net"));
	}
	
	@Test
	public void testDomainRetrieval() throws ImapException {
		Set<MailAddress> addresses = new HashSet<MailAddress>();
		addresses.add(new MailAddress("abc@def.ghi.jkl"));
		addresses.add(new MailAddress("abc@def.ghi"));
		addresses.add(new MailAddress("gurke@def.ghi.jkl"));
		addresses.add(new MailAddress("troll@reddit.com"));
		
		Set<String> domains = ImapQuery.getDomains(addresses);
		assertTrue(domains.contains("def.ghi.jkl"));
		assertTrue(domains.contains("def.ghi"));
		assertTrue(domains.contains("reddit.com"));
	}
}
