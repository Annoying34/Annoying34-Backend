package annoying34.communication;

import org.springframework.security.crypto.bcrypt.BCrypt;


public class UserService {

    public static String generateToken(User user) {
        String salt = BCrypt.gensalt();
        String token = BCrypt.hashpw(user.getEmail() + user.getId(), salt);
        user.setToken(token);

        return token;
    }
}
