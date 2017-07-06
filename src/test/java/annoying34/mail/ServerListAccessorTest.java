package annoying34.mail;

import annoying34.mail.Server;
import annoying34.mail.ServerConfig;
import annoying34.mail.ServerListAccessor;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerListAccessorTest {

    @Test
    @Ignore("Passing of other tests implies accessibility")
    public void testAccessibilityToStrongerThanStrongCiphers() throws IOException {
        assertFalse(ServerListAccessor.getServerConfig("gmx.de") == null);
    }

    @Test
    public void testServerConfigRetrieval1() throws IOException {
        ServerConfig config = ServerListAccessor.getServerConfig("gmx.de");
        Server imapServer = config.getImapServer();
        Server smtpServer = config.getSmtpServer();
        assertTrue(config.getDomain().equals("gmx.de"));
        assertTrue(imapServer.getHostname().equals("imap.gmx.net"));
        assertTrue(imapServer.getPort() == 143);
        assertTrue(imapServer.getSocketType().equals("STARTTLS"));
        assertTrue(smtpServer.getHostname().equals("mail.gmx.net"));
        assertTrue(smtpServer.getPort() == 587);
        assertTrue(smtpServer.getSocketType().equals("STARTTLS"));
    }

    @Test
    public void testServerConfigRetrieval2() throws IOException {
        ServerConfig config = ServerListAccessor.getServerConfig("gmail.com");
        Server imapServer = config.getImapServer();
        Server smtpServer = config.getSmtpServer();
        assertTrue(config.getDomain().equals("gmail.com"));
        assertTrue(imapServer.getHostname().equals("imap.gmail.com"));
        assertTrue(imapServer.getPort() == 993);
        assertTrue(imapServer.getSocketType().equals("SSL"));
        assertTrue(smtpServer.getHostname().equals("smtp.gmail.com"));
        assertTrue(smtpServer.getPort() == 465);
        assertTrue(smtpServer.getSocketType().equals("SSL"));
    }
}
