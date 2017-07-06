package annoying34.communication;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import annoying34.company.Company;


@RunWith(SpringRunner.class)
@DataJpaTest
@RestClientTest(UserController.class)
public class UserControllerTest {
	
	private MockMvc mvc;
	
    @Autowired
    private UserRepository userRepository;
    
    @Before
    public void setUp() {
    	UserController controller = new UserController(userRepository);
    	mvc = standaloneSetup(controller).build();
    }
    
    @Test
    public void NotFound_IfTokenIsNotInDB() throws Exception {
    	this.mvc.perform(get("/user?token=invalidtoken")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void findUserIfPresent() throws Exception {
    	Company company = new Company("TestCompany", "test@testcompany.com", "url.testcompany.com/icon.ico", "testcompany.com", false);
        List<Company> companies = new ArrayList<Company>();
        companies.add(company);
    	
        User user = new User();
        user.setEmail("annoy34@test.de");
        user.setName("Annoying 34");
        user.setToken("someValidToken");
        user.setCompanies(companies);
        
        userRepository.save(user);
    	
    	MockHttpServletResponse response = this.mvc.perform(get("/user?token=someValidToken")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	Map<String, Object> result = objectMapper.readValue(response.getContentAsString(), new TypeReference<Map<String, Object>>() {
        });
    	String userName = (String) result.get("name");
    	String userToken = (String) result.get("token");
    	String userMail = (String) result.get("email");
    	LinkedHashMap userCompany= (LinkedHashMap) ((List) result.get("companies")).get(0);
    	
    	assertEquals(userName, user.getName());
    	assertEquals(userToken, user.getToken());
    	assertEquals(userMail, user.getEmail());
    	assertEquals(new Long((Integer) userCompany.get("id")).longValue(), company.getId());
    	assertEquals((String) userCompany.get("name"), company.getName());
    	assertEquals((String) userCompany.get("email"), company.getEmail());
    	assertEquals((String) userCompany.get("imageURL"), company.getImageURL());
    	assertEquals((String) userCompany.get("domain"), company.getDomain());
    	assertEquals((boolean) userCompany.get("selected"), company.isSelected());
    }
}
