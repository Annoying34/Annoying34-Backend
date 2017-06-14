package annoying34.company;

import annoying34.mail.*;
import annoying34.website.CrawlerResult;
import annoying34.website.Spider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class CompanyService {
    private static final Logger log = LogManager.getLogger();

    private final CompanyDao companyDao;

    @Autowired
    public CompanyService(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public List<Company> getCompanies() {
        List<Company> companyList = companyDao.findAll();
        log.info("load companylist({})", companyList.size());
        return companyList;
    }

    public List<Company> getCompanies(CompanySearch search) {
        String imapURL = getImapURL(search);
        Map<String, MailAddress> domainMap = getMailAddressMap(search, imapURL);

        if (domainMap == null) {
            return null;
        }
        List<Company> matchingCompanies = loadMapFromDBAndCheckFoundDomains(domainMap);
        matchingCompanies.addAll(lookingForUncachedCompanies(domainMap, matchingCompanies));

        return matchingCompanies;
    }

    private List<Company> lookingForUncachedCompanies(Map<String, MailAddress> domainMap, List<Company> matchingCompanies) {
        for (Company company : matchingCompanies) {
            domainMap.remove(company.getDomain());
        }
        return crawlNewCompanies(domainMap);
    }

    private List<Company> crawlNewCompanies(Map<String, MailAddress> domainMap) {
        List<Company> resultList = new ArrayList<>();
        Spider crawler = new Spider();
        for (String domain : domainMap.keySet()) {
            try {
                CrawlerResult result = crawler.search(domain);
                Company company = new Company(result.name, result.email, result.favicon, domain, true);
                companyDao.save(company);
                log.info("New Company({}) saved.", company);
            } catch (IOException e) {
                log.error("URL {} could not be parsed", domain, e);
            }
        }
        return resultList;
    }

    private List<Company> loadMapFromDBAndCheckFoundDomains(Map<String, MailAddress> domainMap) {
        List<Company> companyList = companyDao.findAll();
        companyList.stream().filter(x -> domainMap.containsKey(x.getDomain())).forEach(e -> e.setSelected(true));
        return companyList;
    }

    private Map<String, MailAddress> getMailAddressMap(CompanySearch search, String imapURL) {
        Collection<MailAddress> senderAddresses;
        try {
            ImapQuery query = new ImapQuery(search.getEmail(), search.getPassword(), imapURL);
            senderAddresses = query.getSenderMailAddresses().stream().collect(toMap(MailAddress::getMailAddress, p -> p, (p, q) -> p)).values();
        } catch (MailException e) {
            log.error("Could not Query Mail Addresses with {}", search, e);
            return null;
        }
        return senderAddresses.stream().collect(toMap(MailAddress::getDomain, x -> x));
    }

    private String getImapURL(CompanySearch search) {
        String imapURL = search.getImapURL();
        if (StringUtils.isEmpty(imapURL)) {
            log.info("crawl Imap Address");
            ServerConfig serverConfig = ServerListAccessor.getServerConfig(search.getEmail());
            if (serverConfig.getImapServer() != null) {
                imapURL = serverConfig.getImapServer().getHostname();
                log.info("ImapServer {} detected", imapURL);
            }
        }
        return imapURL;
    }

    private String getSmtpURL(CompanySearch search) {
        String smtpURL = search.getImapURL();
        if (StringUtils.isEmpty(smtpURL)) {
            log.info("crawl Smtp Address");
            ServerConfig serverConfig = ServerListAccessor.getServerConfig(search.getEmail());
            if (serverConfig.getImapServer() != null) {
                smtpURL = serverConfig.getSmtpServer().getHostname();
                log.info("SmtpServer {} detected", smtpURL);
            }
        }
        return smtpURL;
    }
}
