package annoying34.mail;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class MailAddress {
    private String localPart;
    private String domain;
    private String mail;

    public MailAddress(String address) throws ImapException {
        try {
            InternetAddress internetAddress = new InternetAddress(address);
            internetAddress.validate();

            mail = internetAddress.getAddress();

            int splitIndex = mail.lastIndexOf("@");
            if (splitIndex == -1) { // Should not happen due to validation
                throw new ImapException("Mail address is invalid");
            }

            localPart = mail.substring(0, splitIndex);
            domain = mail.substring(splitIndex + 1);
        } catch (AddressException e) {
            // Mail address is not valid
            // TODO discuss whether RFC822 is okay
            throw new ImapException("Mail address is invalid", e);
        }
    }

    public MailAddress(Address address) throws ImapException {
        this(address.toString());
    }

    public String getMailAddress() {
        return mail;
    }

    public String getLocalPart() {
        return localPart;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String toString() {
        return mail;
    }
}
