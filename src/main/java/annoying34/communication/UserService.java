package annoying34.communication;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    public String generateToken(User user) {
        String salt = BCrypt.gensalt();
        String token = BCrypt.hashpw(user.getEmail() + user.getId(), salt);
        user.setToken(token);
        //TODO save token in DB

        return token;
    }
}
