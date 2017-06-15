package annoying34.mail;

@SuppressWarnings("serial")
public class MailException extends Exception {

    public MailException(String message) {
        super(message);
    }

    public MailException(String message, Throwable e) {
        super(message, e);
    }
}
