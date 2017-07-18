package annoying34.website;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpiderLegTest {

    SpiderLeg toTest = new SpiderLeg();

    @Test
    public void checkValidNormalMail() throws Exception {
        String email = "mail@mydomain.com";
        List<String> found = toTest.searchMailAddressesInPageText(email);
        assertEquals(email, found.get(0));
    }

    @Test
    public void checkValidNormalMailWithLongDomain() throws Exception {
        String email = "mail@mydomain.online";
        List<String> found = toTest.searchMailAddressesInPageText(email);
        assertEquals(email, found.get(0));
    }

    @Test
    public void checkMailWithEscapedAtAndBraces() throws Exception {
        String email = "mail(at)mydomain.com";
        List<String> found = toTest.searchMailAddressesInPageText(email);
        assertEquals(email.replace("(at)", "@"), found.get(0));
    }

    @Test
    public void checkMailWithEscapedAtAndSquareBracket() throws Exception {
        String email = "mail[at]mydomain.com";
        List<String> found = toTest.searchMailAddressesInPageText(email);
        assertEquals(email.replace("[at]", "@"), found.get(0));
    }

    @Test
    public void doNotFindInvalidMail() throws Exception {
        String email = "mail@mydomaincom";
        List<String> found = toTest.searchMailAddressesInPageText(email);
        assertTrue(found.isEmpty());
    }

    @Test
    public void findMailInText() throws Exception {
        String email = "mail@mydomain.com";
        String email2 = "mail@foobar.com";
        String text = " Lorem ipsum dolor sit amet, consetetur sadipscing elitr " + email
                + " sed diam nonumy eirmod tempor invidunt ut " + email2 + " labore et dolore magna aliquyam";
        List<String> found = toTest.searchMailAddressesInPageText(text);
        assertEquals(2, found.size());
        assertEquals(email, found.get(0));
        assertEquals(email2, found.get(1));
    }
}