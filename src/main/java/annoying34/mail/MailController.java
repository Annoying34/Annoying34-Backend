package annoying34.mail;

import annoying34.request.RequestGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    @GetMapping(value = "/mail/header")
    public ResponseEntity<String> getMailHeader() {
        return new ResponseEntity<String>(RequestGenerator.getRequestForInformationSubject(), HttpStatus.OK);
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
}
