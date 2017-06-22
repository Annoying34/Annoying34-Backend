package annoying34.company;

import annoying34.mail.MailService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.Assert.assertEquals;
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
    private MailService mailService;

    @Before
    public void setUp() {
        CompanyService companyService = new CompanyService();
        companyService.setCompanyRepository(repository);
        ;
        companyService.setMailService(mailService);

        CompaniesController controller = new CompaniesController();
        controller.setMailService(mailService);
        controller.setCompanyService(companyService);
        mvc = standaloneSetup(controller).build();
    }

    @Test
    public void NotFound_IfGetNoCompanyIsInDB() throws Exception {
        this.mvc.perform(get("/companies").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ok_IfGetCompany_WithNoHeader() throws Exception {
        addDemoCompanyInDB();
        MockHttpServletResponse response = this.mvc.perform(get("/companies").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(response.getContentAsString());
        List<Company> resultList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Company>>() {
        });
        Company company = resultList.get(0);
        assertEquals("TestCompany", company.getName());
        assertEquals("test@testcompany.com", company.getEmail());
        assertEquals("testcompany.com", company.getDomain());

    }

    private void addDemoCompanyInDB() {
        Company company = new Company("TestCompany", "test@testcompany.com", "url.testcompany.com/icon.ico", "testcompany.com", false);
        repository.save(company);
    }

}