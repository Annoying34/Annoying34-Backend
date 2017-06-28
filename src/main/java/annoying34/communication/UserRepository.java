package annoying34.communication;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

    User findByToken(String token);

    List<User> findAll();
}
