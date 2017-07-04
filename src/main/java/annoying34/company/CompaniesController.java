package annoying34.company;

import annoying34.communication.User;
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
    private CompanyService companyService;
    private UserService userService;
    private MailService mailService;

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
    public ResponseEntity<String> prepareMailsForUser(@RequestHeader(value = "name", defaultValue = "") String name,
                                                      @RequestHeader(value = "email", defaultValue = "") String email,
                                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                                      @RequestHeader(value = "smtpUrl", defaultValue = "") String smtpURL,
                                                      @RequestBody List<Company> companies) {

        if (!StringUtils.isEmpty(password)) {
            //send mails, if password was given
            try {
                mailService.sendMail(name, email, password, smtpURL, companies);
            } catch (MailException e) {
                log.error("could not send mail", e);
                return new ResponseEntity<>("unable to create or send mails", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        User user = userService.saveUser(name, email, companies);
        return new ResponseEntity(user.getToken(), HttpStatus.OK);
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

    @Autowired
    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
