package annoying34.company;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompaniesController {

    @RequestMapping(method = RequestMethod.GET, value = "/companies")
    public List<Company> getCompanies(@RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL) {

        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
            return CompanyService.getCompanies(new CompanySearch(email, password, imapURL));
        } else {
            return CompanyService.getCompanies();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/companies", consumes = "application/json")
    @ResponseBody
    public void postCompaniesSettings(@RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL,
                                      @RequestBody List<Company> companies) {
        //TODO stuff
    }

}
