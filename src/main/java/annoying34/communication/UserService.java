package annoying34.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository repository;

    public static String generateToken(User user) {
        String salt = BCrypt.gensalt();
        String token = BCrypt.hashpw(user.getEmail() + user.getId(), salt);
        user.setToken(token);

        return token;
    }

    public User saveUser(String name, String email) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        String token = UserService.generateToken(user);
        repository.save(user);

        return user;
    }

    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}
