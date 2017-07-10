package annoying34.mail;

import annoying34.company.Company;
import annoying34.company.CompanyService;
import annoying34.request.RequestGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class MailController {


    private CompanyService companyService;

    @Autowired
    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping(value = "/mail/header")
    public ResponseEntity<String> getMailHeader() {
        return new ResponseEntity<>(RequestGenerator.getRequestForInformationSubject(), HttpStatus.OK);
    }

    @GetMapping(value = "/mail/body")
    public ResponseEntity<String> getMailText(@RequestHeader(value = "name", defaultValue = "") String name) {
        ResponseEntity entity;
        if (StringUtils.isEmpty(name)) {
            entity = new ResponseEntity("name must be set", HttpStatus.BAD_REQUEST);
        } else {
            entity = new ResponseEntity(RequestGenerator.getRequestForInformationBody(name), HttpStatus.OK);
        }

        return entity;
    }

    @PostMapping(value = "/mail/addresses")
    public ResponseEntity<List<String>> findSUpportMailAddresses(@RequestBody List<String> mailAddressesStrings) {
        List<String> resultList = new ArrayList<>();

        List<Company> companyList = companyService.getCompanies();
        Map<Boolean, List<MailAddress>> databaseMatch = mailAddressesStrings
                .stream()
                .map(str -> convertToMailAddress(str))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.partitioningBy(address -> isInList(companyList, address), Collectors.toList()));
        resultList.addAll(findAndMap(databaseMatch.get(true), companyList));
        resultList.addAll(crawlCompany(databaseMatch.get(false)));

        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

    private Optional<MailAddress> convertToMailAddress(String addressString) {
        try {
            return Optional.of(new MailAddress(addressString));
        } catch (MailException e) {
            return Optional.empty();
        }
    }

    private boolean isInList(List<Company> companyList, MailAddress address) {
        String domain = address.getDomain();
        Optional<Company> result = companyList.stream().filter(e -> domain.equals(e.getDomain())).findAny();
        return result.isPresent();
    }

    private List<String> findAndMap(List<MailAddress> mailAddresses, List<Company> companyList) {
        List<String> companyMailAddresses = new ArrayList<>();
        for (MailAddress mailAddress : mailAddresses) {
            Optional<Company> result = companyList.stream().filter(e -> e.getDomain().equals(mailAddress.getDomain())).findAny();
            if (result.isPresent()) {
                companyMailAddresses.add(result.get().getEmail());
            }
        }
        return companyMailAddresses;
    }

    private List<String> crawlCompany(List<MailAddress> addresses) {
        return addresses.stream()
                .map(MailAddress::getDomain)
                .map(domain -> companyService.crawlCompany(domain))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Company::getEmail)
                .collect(Collectors.toList());
    }
}
