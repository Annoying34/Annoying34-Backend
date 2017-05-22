package annoy34.mail;

public class ServerConfig {
	private String domain;
	private Server imapServer;
	private Server smtpServer;
	
	public ServerConfig(String domain, Server imapServer, Server smtpServer) {
		this.domain = domain;
		this.imapServer = imapServer;
		this.smtpServer = smtpServer;
	}

	public String getDomain() {
		return domain;
	}

	public Server getImapServer() {
		return imapServer;
	}

	public Server getSmtpServer() {
		return smtpServer;
	}
}
