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
	        SmtpSender smtp = new SmtpSender(email, password, smtpURL);
	        String subject = RequestGenerator.getRequestForInformationSubject();
	        String message = RequestGenerator.getRequestForInformationBody(name);

	        for (Company company : companies) {
	            smtp.sendMail(company.getEmail(), subject, message); //TODO change to one mail with companies in BCC
			}
    	} catch (MailException e) {
    		log.error(e);
    	}
    }

}
