package annoying34.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpSender extends MailAccessor {
    private InternetAddress senderAddress;

    public SmtpSender(String address, String password, String smtpServer) throws ImapException {
        super(address, password);
        session.getProperties().setProperty("mail.smtp.host", smtpServer);
        try {
            senderAddress = new InternetAddress(address);
        } catch (AddressException e) {
            throw new ImapException("Invalid sender address", e);
        }
    }

    public void sendMail(String recipient, String subject, String content) throws ImapException {
        InternetAddress recipientAddress;
        try {
            recipientAddress = new InternetAddress(recipient);
        } catch (AddressException e) {
            throw new ImapException("Invalid recipient address", e);
        }

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(senderAddress);
            message.setSender(senderAddress);
            message.addRecipient(Message.RecipientType.TO, recipientAddress);
            message.setSubject(subject);
            message.setText(content);

            Transport transport = session.getTransport();
            transport.connect();
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
            transport.close();
        } catch (MessagingException e) {
            throw new ImapException("Error while sending message", e);
        }
    }
}
