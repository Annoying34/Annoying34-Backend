package annoying34.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompaniesController {

    private CompanyService service;

    @Autowired
    public CompaniesController(CompanyService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/companies")
    public List<Company> getCompanies(@RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL) {

        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
            return service.getCompanies(new CompanySearch(email, password, imapURL));
        } else {
            return service.getCompanies();
        }
    }

    @PostMapping(value = "/companies", consumes = "application/json")
    public void postCompaniesSettings(@RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL,
                                      @RequestBody List<Company> companies) {
        //TODO stuff
        System.out.println("mail: " + email);
        System.out.println("pw  : " + password);
        System.out.println("url : " + imapURL);
        System.out.println("comp: " + companies);
    }

}
