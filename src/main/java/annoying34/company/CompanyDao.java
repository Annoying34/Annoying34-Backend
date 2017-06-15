package annoying34.company;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface CompanyDao extends CrudRepository<Company, Long>{

    List<Company> findAll();
}
