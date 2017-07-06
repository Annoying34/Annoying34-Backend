package annoying34.mail;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import annoying34.request.RequestGenerator;

@RunWith(SpringRunner.class)
@DataJpaTest
@RestClientTest(MailController.class)
public class MailControllerTest {

	private MockMvc mvc;

	@Before
	public void setUp() {
		MailController controller = new MailController();
		mvc = standaloneSetup(controller).build();
	}
	
	@Test
	public void testHeaderResponse() throws Exception {
		MockHttpServletResponse response = this.mvc.perform(get("/mail/header")
                .accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse();
		assertEquals(response.getContentAsString(), RequestGenerator.getRequestForInformationSubject());
	}
	
	@Test
	public void testBadRequestIfNoNameSupplied() throws Exception {
		this.mvc.perform(get("/mail/body")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isBadRequest());
	}
	
	@Test
	public void testRequestBodyForName() throws Exception {
		MockHttpServletResponse response = this.mvc.perform(get("/mail/body").header("name", "Test")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn().getResponse();
		assertEquals(response.getContentAsString(), RequestGenerator.getRequestForInformationBody("Test"));
	}
}
