package annoying34.mail;

import annoying34.mail.MailException;
import annoying34.mail.MailAddress;
import org.junit.Ignore;
import org.junit.Test;

public class MailAddressTest {

    // Test cases taken from https://en.wikipedia.org/wiki/Email_address

    @Test
    public void testValidAddresses() throws MailException {
        new MailAddress("prettyandsimple@example.com");
        new MailAddress("very.common@example.com");
        new MailAddress("disposable.style.email.with+symbol@example.com");
        new MailAddress("other.email-with-dash@example.com");
        new MailAddress("fully-qualified-domain@example.com");
        new MailAddress("x@example.com");
        new MailAddress("\"much.more unusual\"@example.com");
        new MailAddress("\"very.unusual.@.unusual.com\"@example.com");
        new MailAddress("\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.example.com");
        new MailAddress("example-indeed@strange-example.com");
        new MailAddress("admin@mailserver1");
        new MailAddress("#!$%&'*+-/=?^_`{}|~@example.org");
        new MailAddress("\"()<>[]:,;@\\\"!#$%&'-/=?^_`{}| ~.a\"@example.org");
        new MailAddress("\" \"@example.org");
        new MailAddress("example@localhost");
        new MailAddress("example@s.solutions");
        new MailAddress("user@localserver");
        new MailAddress("user@tt");
        new MailAddress("user@[IPv6:2001:DB8::1]");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses1() throws MailException {
        new MailAddress("Abc.example.com");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses2() throws MailException {
        new MailAddress("A@b@c@example.com");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses3() throws MailException {
        new MailAddress("a\"b(c)d,e:f;g<h>i[j\\k]l@example.com");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses4() throws MailException {
        new MailAddress("just\"not\"right@example.com");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses5() throws MailException {
        new MailAddress("this is\"not\\allowed@example.com");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses6() throws MailException {
        new MailAddress("this\\ still\\\"not\\\\allowed@example.com");
    }

    @Ignore("Do we really care that too long addresses are not rejected?")
    @Test(expected = MailException.class)
    public void testInvalidAddresses7() throws MailException {
        new MailAddress("1234567890123456789012345678901234567890123456789012345678901234+x@example.com");
    }

    @Ignore("GMail ignore double dots")
    @Test(expected = MailException.class)
    public void testInvalidAddresses8() throws MailException {
        new MailAddress("john..doe@example.com");
    }

    @Test(expected = MailException.class)
    public void testInvalidAddresses9() throws MailException {
        new MailAddress("john.doe@example..com");
    }

    @Test
    public void testTrimLeadingSpace() throws MailException {
        new MailAddress(" valid@example.com");
    }

    @Test
    public void testTrimTrailingSpace() throws MailException {
        new MailAddress("valid@example.com ");
    }
}
