package annoying34.company;

import annoying34.mail.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
public class CompanyService {
    private final CompanyDao companyDao;

    @Autowired
    public CompanyService(CompanyDao companyDao) {
        this.companyDao = companyDao;
    }

    public List<Company> getCompanies() {
        List<Company> companyList = new ArrayList<>();
        companyDao.findAll().forEach(companyList::add);

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
        //TODO DomainMap does only contains uncached Mail -> Crawl them
        return new ArrayList<>();
    }

    private List<Company> loadMapFromDBAndCheckFoundDomains(Map<String, MailAddress> domainMap) {
        List<Company> companyList = new ArrayList<>();
        companyDao.findAll().iterator().forEachRemaining(companyList::add);
        companyList.stream().filter(x -> domainMap.containsKey(x.getDomain())).forEach(e -> e.setSelected(true));
        return companyList;
    }

    private Map<String, MailAddress> getMailAddressMap(CompanySearch search, String imapURL) {
        Collection<MailAddress> senderAddresses;
        try {
            ImapQuery query = new ImapQuery(search.getEmail(), search.getPassword(), imapURL);
            senderAddresses = query.getSenderMailAddresses().stream().collect(toMap(MailAddress::getMailAddress, p -> p, (p, q) -> p)).values();
        } catch (ImapException e) {
            return null;
        }
        return senderAddresses.stream().collect(toMap(address -> address.getDomain(), x -> x));
    }

    private String getImapURL(CompanySearch search) {
        String imapURL = search.getImapURL();
        if (StringUtils.isEmpty(imapURL)) {
            ServerConfig serverConfig = ServerListAccessor.getServerConfig(search.getEmail());
            imapURL = serverConfig.getImapServer().getHostname();
        }
        return imapURL;
    }
}
