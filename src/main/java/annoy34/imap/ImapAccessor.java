package annoy34.imap;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public abstract class ImapAccessor {
	protected Session session;
	
	public ImapAccessor(String address, String password) throws ImapException {
		this.session = getSession(address, password);
	}
	
	private Session getSession(String address, String password) {
		Properties props = System.getProperties();
		props.setProperty("mail.smtp.starttls.enable","true");
		props.setProperty("mail.store.protocol", "imaps");
		props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, new DefaultAuthenticator(address, password) {
		});
		return session;
	}
	
	private class DefaultAuthenticator extends Authenticator {
		private String username;
		private String password;
		
		public DefaultAuthenticator(String address, String password) {
			this.username = address;
			this.password = password;
		}
        public PasswordAuthentication getPasswordAuthentication() {
           return new PasswordAuthentication(username, password);
        }
    }
}
