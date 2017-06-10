package annoying34.mail;

@SuppressWarnings("serial")
public class ImapException extends Exception {

    public ImapException(String message) {
        super(message);
    }

    public ImapException(String message, Throwable e) {
        super(message, e);
    }
}
