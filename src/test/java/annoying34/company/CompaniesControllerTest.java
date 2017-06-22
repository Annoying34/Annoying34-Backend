package annoying34.company;

import annoying34.mail.MailService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@DataJpaTest
@RestClientTest(CompaniesController.class)
public class CompaniesControllerTest {

    private MockMvc mvc;

    @Autowired
    private CompanyRepository repository;

    @MockBean
    private MailService service;

    @Before
    public void setUp() {
        mvc = standaloneSetup(CompaniesController.class).build();
    }

    @Test
    public void NotFound_IfGetNoCompanyIsInDB() throws Exception {
        this.mvc.perform(get("/companies").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound());
    }

    @Ignore // defect
    @Test
    public void ok_IfGetCompany_WithNoHeader() throws Exception {
        addDemoCompanyInDB();
        this.mvc.perform(get("/companies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void addDemoCompanyInDB() {
        Company company = new Company("TestCompany", "test@testcompany.com", "url.testcompany.com/icon.ico", "testcompany.com", false);
        repository.save(company);
    }

}