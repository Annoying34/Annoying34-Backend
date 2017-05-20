package annoying34.company;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
public class CompaniesController {

    @RequestMapping(value = "/companies")
    public List<Company> getCompanies(@RequestHeader(value = "email", defaultValue = "") String email,
                                      @RequestHeader(value = "password", defaultValue = "") String password,
                                      @RequestHeader(value = "imapurl", defaultValue = "") String imapURL) {
        if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(password)) {
            return CompanyService.getCompanies(new CompanySearch(email, password, imapURL));
        } else {
            return CompanyService.getCompanies();
        }
    }

    @ExceptionHandler
    void handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
