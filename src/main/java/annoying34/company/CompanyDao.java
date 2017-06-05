package annoying34.company;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface CompanyDao extends CrudRepository<Company, Long>{

}
