package annoying34.mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SmtpSender extends MailAccessor {
    private static final Logger log = LogManager.getLogger();
    private InternetAddress senderAddress;

    public SmtpSender(String address, String password, String smtpServer) throws MailException {
        super(address, password);
        session.getProperties().setProperty("mail.smtp.host", smtpServer);
        try {
            senderAddress = new InternetAddress(address);
        } catch (AddressException e) {
            throw new MailException("Invalid sender address", e);
        }
    }

    public void sendMail(List<String> recipients, String subject, String content) throws MailException {
        List<InternetAddress> recipientAddresses = new ArrayList<InternetAddress>();
        for (String recipient : recipients) {
        	try {
                recipientAddresses.add(new InternetAddress(recipient));
            } catch (AddressException e) {
                log.error("Invalid recipient address", e);
            }
		}

        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(senderAddress);
            message.setSender(senderAddress);
            message.addRecipient(Message.RecipientType.TO, senderAddress);
            InternetAddress[] addresses = new InternetAddress[recipientAddresses.size()];
            addresses = recipientAddresses.toArray(addresses);
            message.addRecipients(Message.RecipientType.BCC, addresses);
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new MailException("Error while sending message", e);
        }
    }
}
