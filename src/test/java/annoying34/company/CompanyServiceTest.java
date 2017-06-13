package annoying34.company;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompanyServiceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CompanyDao companyDao;
    private CompanyService service;

    @Before
    public void setup() {
        service = new CompanyService(companyDao);
    }

    @Test
    public void loadCompanies() throws Exception {
        this.entityManager.persist(new Company("foo", "foo@bar.org", "icon.ico", "foo-bar.de", true));

        List<Company> result = service.getCompanies();
        assertFalse(result.isEmpty());
        Optional<Company> testData = result.stream().filter(c -> c.getEmail().equals("foo@bar.org")).filter(c -> c.getName().equals("foo")).findAny();
        assertTrue(testData.isPresent());
        assertEquals("foo", testData.get().getName());
        assertEquals("foo@bar.org", testData.get().getEmail());
        assertEquals("icon.ico", testData.get().getImageURL());
        assertEquals(true, testData.get().isSelected());
    }

}