package annoying34.company;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface CompanyRepository extends CrudRepository<Company, Long> {

    List<Company> findAll();

    List<Company> findByIdIn(List<Long> ids);
}
