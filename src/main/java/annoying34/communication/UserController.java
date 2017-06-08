package annoying34.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserDao userDao;

    @Autowired
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/user")
    public String revisit(@RequestParam(value="token") String token) {
        User user = userDao.findByToken(token);
        User user2 = new User();
        user2.setEmail("foo@bar.org");
        user2.setFirstname("Lassmiranda");
        user2.setLastname("Dennsiewillja");
        String token2 = new UserService().generateToken(user2);
        userDao.save(user2);

        if(user == null) {
            return token2;
        } else {
            return user.getFirstname() + " " + user.getLastname();
        }
    }
}
