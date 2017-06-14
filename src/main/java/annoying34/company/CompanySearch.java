package annoying34.company;

public class CompanySearch {

    private final String email;
    private final String password;
    private String imapURL;
    private String smtpURL;

    public CompanySearch(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public CompanySearch(String email, String password, String imapURL, String smtpUrl) {
        this(email, password);
        this.imapURL = imapURL;
        this.setSmtpURL(smtpUrl);
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImapURL() {
        return imapURL;
    }

    public void setImapURL(String imapURL) {
        this.imapURL = imapURL;
    }

	public String getSmtpURL() {
		return smtpURL;
	}

	public void setSmtpURL(String smtpURL) {
		this.smtpURL = smtpURL;
	}
}
