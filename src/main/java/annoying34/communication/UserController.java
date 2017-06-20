package annoying34.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public String revisit(@RequestParam(value="token") String token) {
        User user = userRepository.findByToken(token);

        if(user == null) {
            return "";
        } else {
            return user.getName();
        }
    }
}
