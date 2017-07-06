package annoying34.mail;


import annoying34.company.Company;
import annoying34.company.CompanySearch;
import annoying34.request.RequestGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class MailService {

    Logger log = LogManager.getLogger();

    public String getImapURL(CompanySearch search) {
        String imapURL = search.getImapURL();
        if (StringUtils.isEmpty(imapURL)) {
            log.info("crawl Imap Address");
            try {
                String domain = new MailAddress(search.getEmail()).getDomain();

                ServerConfig serverConfig = ServerListAccessor.getServerConfig(domain);
                if (serverConfig != null && serverConfig.getImapServer() != null) {
                    imapURL = serverConfig.getImapServer().getHostname();
                    log.info("ImapServer {} detected", imapURL);
                }
            } catch (MailException e) {
                log.error("imap address could not detected");
            }
        }
        return imapURL;
    }

    public Map<String, MailAddress> getMailAddressMap(CompanySearch search, String imapURL) {
        Collection<MailAddress> senderAddresses;
        try {
            ImapQuery query = new ImapQuery(search.getEmail(), search.getPassword(), imapURL);
            senderAddresses = query.getSenderMailAddresses().stream().collect(toMap(MailAddress::getMailAddress, p -> p, (p, q) -> p)).values();
        } catch (Exception e) {
            log.error("Could not Query Mail Addresses with {}", search, e);
            return new HashMap<>();
        }

        Map<String, MailAddress> map = new HashMap<>();
        for (MailAddress mailAddress : senderAddresses) {
            map.put(mailAddress.getDomain(), mailAddress);
        }

        return map;
    }


    public void sendMail(String senderName, String senderMail, String senderPassword,
                         String smtpURL, List<Company> companies) throws MailException {

        SmtpSender smtp = new SmtpSender(senderMail, senderPassword, smtpURL);
        String subject = RequestGenerator.getRequestForInformationSubject();
        String message = RequestGenerator.getRequestForInformationBody(senderName);
        List<String> addresses = companies.stream().filter(Company::isSelected).map(Company::getEmail).collect(Collectors.toList());

        smtp.sendMail(addresses, subject, message);
    }
}
