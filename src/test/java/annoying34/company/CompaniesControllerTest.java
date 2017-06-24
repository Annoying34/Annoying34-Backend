package annoying34.company;

import annoying34.communication.User;
import annoying34.communication.UserRepository;
import annoying34.communication.UserService;
import annoying34.mail.MailAddress;
import annoying34.mail.MailException;
import annoying34.mail.MailService;
import annoying34.website.CrawlerResult;
import annoying34.website.Spider;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@DataJpaTest
@RestClientTest(CompaniesController.class)
public class CompaniesControllerTest {

    private MockMvc mvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private MailService mailService;

    @MockBean
    private Spider spider;

    @Before
    public void setUp() {
        CompanyService companyService = new CompanyService();
        companyService.setCompanyRepository(companyRepository);
        companyService.setMailService(mailService);
        companyService.setWebcrawler(spider);

        UserService userService = new UserService();
        userService.setRepository(userRepository);

        CompaniesController controller = new CompaniesController();
        controller.setMailService(mailService);
        controller.setCompanyService(companyService);
        controller.setUserService(userService);
        mvc = standaloneSetup(controller).build();
    }

    @Test
    public void NotFound_IfGetNoCompanyIsInDB() throws Exception {
        this.mvc.perform(get("/companies")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ok_IfGetCompany_WithNoHeader() throws Exception {
        addDemoCompanyInDB();
        MockHttpServletResponse response = this.mvc.perform(get("/companies")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Company> resultList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Company>>() {
        });
        Company company = resultList.get(0);
        assertEquals("TestCompany", company.getName());
        assertEquals("test@testcompany.com", company.getEmail());
        assertEquals("testcompany.com", company.getDomain());
    }

    @Test
    public void ok_IfGetCompany_WithHeaderAndImapServer() throws Exception {
        CompanySearch s = new CompanySearch("test@test.bar", "incorrect", "imap.test.bar", "");
        Map<String, MailAddress> map = new HashMap<>();
        map.put("test.bar", new MailAddress(s.getEmail()));
        when(mailService.getMailAddressMap(any(), any())).thenReturn(map);
        when(spider.search("test.bar")).thenReturn(new CrawlerResult("test", "info@test.bar", "favIcon"));
        addDemoCompanyInDB();
        MockHttpServletResponse response = this.mvc.perform(get("/companies")
                .accept(MediaType.APPLICATION_JSON)
                .header("email", s.getEmail())
                .header("password", s.getPassword())
                .header("imapURL", s.getImapURL()))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        ObjectMapper objectMapper = new ObjectMapper();
        List<Company> resultList = objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Company>>() {
        });
        Company c1 = resultList.get(0);
        assertEquals("TestCompany", c1.getName());
        assertEquals("test@testcompany.com", c1.getEmail());
        assertEquals("testcompany.com", c1.getDomain());
        Company c2 = resultList.get(1);
        assertEquals("test", c2.getName());
        assertEquals("info@test.bar", c2.getEmail());
        assertEquals("test.bar", c2.getDomain());
        assertEquals(2, companyRepository.findAll().size());
    }

    @Test
    public void error_IfPostCouldNotSendMails() throws Exception {
        doThrow(new MailException("")).when(mailService).sendMail(any(), any(), any(), any(), any());
        this.mvc.perform(post("/companies")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isInternalServerError());
        assertEquals(0, userRepository.findAll().size());
    }

    @Test
    public void insertUser_IfPostIsValid() throws Exception {
        MockHttpServletResponse response = this.mvc.perform(post("/companies")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("name", "name")
                .header("email", "foo@bar.spam")
                .header("password", "incorrect")
                .header("smtpURL", "smtp.test.test")
                .content("[]"))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        List<User> users = userRepository.findAll();
        assertEquals(1, users.size());
        User toTest = users.get(0);
        assertEquals("name", toTest.getName());
        assertEquals("foo@bar.spam", toTest.getEmail());
        assertEquals(response.getContentAsString(), toTest.getToken());
    }

    @Test
    public void error_IfPutIsEmpty() throws Exception {
        this.mvc.perform(put("/companies")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void insertCompany_IfPutValidCompany() throws Exception {
        this.mvc.perform(put("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": 1,\"name\": \"TestCorp\",\"email\": \"mailto@testcorp.test\",\"imageURL\": \"\",\"domain\": \"testcorp.test\", \"selected\": false}"))
                .andExpect(status().isOk());
        List<Company> companyList = companyRepository.findAll();
        assertEquals(1, companyList.size());
        Company toTest = companyList.get(0);
        assertEquals("TestCorp", toTest.getName());
        assertEquals("mailto@testcorp.test", toTest.getEmail());
        assertEquals("testcorp.test", toTest.getDomain());

    }

    private void addDemoCompanyInDB() {
        Company company = new Company("TestCompany", "test@testcompany.com", "url.testcompany.com/icon.ico", "testcompany.com", false);
        companyRepository.save(company);
    }
}