package annoying34.company;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import annoying34.mail.MailException;
import annoying34.mail.SmtpSender;
import annoying34.request.RequestGenerator;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class CompaniesController {

    private CompanyService service;
    private static final Logger log = LogManager.getLogger();

    @Autowired
    public CompaniesController(CompanyService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/companies")
    public List<Company> getCompanies(@RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL,
                                      @RequestHeader(value = "smtpurl", defaultValue = "") String smtpURL) {

        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
            return service.getCompanies(new CompanySearch(email, password, imapURL, smtpURL));
        } else {
            return service.getCompanies();
        }
    }

    @PostMapping(value = "/companies", consumes = "application/json")
    public void postCompaniesSettings(@RequestHeader(value = "name", defaultValue = "") String name,
                                      @RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "smtppurl", defaultValue = "") String smtpURL,
                                      @RequestBody List<Company> companies) {
    	try {
	        sendMail(name, email, password, smtpURL, companies);
    	} catch (MailException e) {
    		log.error(e);
    	}
    }

    private void sendMail(String senderName, String senderMail, String senderPassword,
    		String smtpURL, List<Company> companies) throws MailException {

	        SmtpSender smtp = new SmtpSender(senderMail, senderPassword, smtpURL);
	        String subject = RequestGenerator.getRequestForInformationSubject();
	        String message = RequestGenerator.getRequestForInformationBody(senderName);
	        List<String> addresses = companies.stream().map(c -> c.getEmail()).collect(Collectors.toList());

	        smtp.sendMail(addresses, subject, message);
    }
}
