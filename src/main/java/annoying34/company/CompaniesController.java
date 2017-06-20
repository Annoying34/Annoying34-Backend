package annoying34.company;

import annoying34.communication.User;
import annoying34.communication.UserRepository;
import annoying34.communication.UserService;
import annoying34.mail.MailException;
import annoying34.mail.MailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CompaniesController {

    private static final Logger log = LogManager.getLogger();
    private final CompanyService companyService;
    private final MailService mailService = new MailService();
    private final UserRepository userRepository;

    @Autowired
    public CompaniesController(CompanyService companyService, UserRepository userRepository) {
        this.companyService = companyService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/companies")
    public ResponseEntity<List<Company>> getCompanies(@RequestHeader(value = "email", defaultValue = "") String email,
                                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL,
                                                      @RequestHeader(value = "smtpurl", defaultValue = "") String smtpURL) {

        List<Company> list = new ArrayList<>();
        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
            try {
                list.addAll(companyService.getCompanies(new CompanySearch(email, password, imapURL, smtpURL)));
            } catch (Exception e) {
                log.error("could not proceed request, email:{}, imapurl:{}", email, imapURL);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            list.addAll(companyService.getCompanies());
            if (list.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping(value = "/companies", consumes = "application/json")
    public ResponseEntity<String> createMailsFromCompanyList(@RequestHeader(value = "name", defaultValue = "") String name,
                                                             @RequestHeader(value = "email", defaultValue = "") String email,
                                                             @RequestHeader(value = "password", defaultValue = "") String password,
                                                             @RequestHeader(value = "smtpUrl", defaultValue = "") String smtpURL,
                                                             @RequestBody List<Company> companies) {
        try {
            mailService.sendMail(name, email, password, smtpURL, companies);
        } catch (MailException e) {
            log.error(e);
            return new ResponseEntity<>("mails could not bei created and send", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        String token = UserService.generateToken(user);
        userRepository.save(user);

        return new ResponseEntity(token, HttpStatus.OK);
    }

    @PutMapping(value = "/companies", consumes = "application/json")
    public ResponseEntity<String> put(@RequestBody Company company) {
        if (company == null) {
            return new ResponseEntity<>("no company transmitted", HttpStatus.BAD_REQUEST);
        } else {
            companyService.put(company);
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }
}
