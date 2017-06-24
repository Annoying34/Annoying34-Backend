package annoying34.company;

import annoying34.mail.MailAddress;
import annoying34.mail.MailService;
import annoying34.website.CrawlerResult;
import annoying34.website.Spider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CompanyService {
    private static final Logger log = LogManager.getLogger();

    private CompanyRepository companyRepository;
    private MailService mailService;
    private Spider webcrawler;

    @Autowired
    public void setWebcrawler(Spider webcrawler) {
        this.webcrawler = webcrawler;
    }

    @Autowired
    public void setCompanyRepository(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    public List<Company> getCompanies() {
        List<Company> companyList = companyRepository.findAll();
        log.info("load companylist({})", companyList.size());
        return companyList;
    }

    public List<Company> getCompanies(CompanySearch search) {
        String imapURL = mailService.getImapURL(search);
        Map<String, MailAddress> domainMap = mailService.getMailAddressMap(search, imapURL);
        List<Company> matchingCompanies = loadMapFromDBAndCheckFoundDomains(domainMap);
        matchingCompanies.addAll(lookingForUncachedCompanies(domainMap, new ArrayList<>(matchingCompanies)));

        return matchingCompanies;
    }

    private List<Company> lookingForUncachedCompanies(Map<String, MailAddress> domainMap, List<Company> matchingCompanies) {
        //remove all already listed companies
        for (Company company : matchingCompanies) {
            domainMap.remove(company.getDomain());
        }
        return crawlNewCompanies(domainMap);
    }

    private List<Company> crawlNewCompanies(Map<String, MailAddress> domainMap) {
        List<Company> resultList = new ArrayList<>();
        for (String domain : domainMap.keySet()) {
            try {
                CrawlerResult result = webcrawler.search(domain);

                if (!StringUtils.isEmpty(result.email)) {
                    Company company = new Company(result.name, result.email, result.favicon, domain, true);
                    companyRepository.save(company);
                    log.info("New Company({}) saved.", company);
                    resultList.add(company);
                } else {
                    log.error("Crawler does not found emails");
                }

            } catch (Exception e) {
                log.error("URL {} could not be parsed", domain, e);
            }
        }
        return resultList;
    }

    public void put(Company company) {
        companyRepository.save(company);
    }

    private List<Company> loadMapFromDBAndCheckFoundDomains(Map<String, MailAddress> domainMap) {
        List<Company> companyList = getCompanies();
        companyList.stream().filter(x -> domainMap.containsKey(x.getDomain())).forEach(e -> e.setSelected(true));
        return companyList;
    }
}
