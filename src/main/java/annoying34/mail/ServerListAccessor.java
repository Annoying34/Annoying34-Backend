package annoying34.mail;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.xml.XmlPage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerListAccessor {
    public static final String SERVERLIST_BASE_URL = "https://autoconfig.thunderbird.net/v1.1/";

    public static ServerConfig getServerConfig(MailAddress mail) {
        return getServerConfig(mail.getDomain());
    }

    public static ServerConfig getServerConfig(String domain) {
        try {
            String content = getPageContent(domain);
            ServerConfig config = parseXML(content, domain);
            return config;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getPageContent(String domain) throws IOException {
        final URL queryUrl = new URL(SERVERLIST_BASE_URL + domain);

        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_45);
        try {
            XmlPage page = webClient.getPage(queryUrl); // Will be XML for a valid domain
            final String xmlPage = page.asXml();
            return xmlPage;
        } catch (FailingHttpStatusCodeException | ClassCastException e) {
            // No such domain in ISP database
            System.out.println("No such domain");
        } catch (SSLHandshakeException e) {
            // Java does not support sufficiently strong crypto to communicate with the ISP database
            System.out.println("Java still doesn't roll out strongest crypto by default");
        } finally {
            webClient.close();
        }
        return null;
    }

    private static ServerConfig parseXML(String xml, String domain) throws ParserConfigurationException, UnsupportedEncodingException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));
        NodeList potentialImapServers = doc.getElementsByTagName("incomingServer");
        NodeList potentialSmtpServers = doc.getElementsByTagName("outgoingServer");

        List<Server> imapServers = getServerInformation(potentialImapServers, "imap");
        List<Server> smtpServers = getServerInformation(potentialSmtpServers, "smtp");

        if (!imapServers.isEmpty() && !smtpServers.isEmpty()) {
            return new ServerConfig(domain, getPreferredImapServer(imapServers), getPreferredSmtpServer(smtpServers));
        } else {
            return null;
        }
    }


    private static List<Server> getServerInformation(NodeList potentialServers, String protocol) {
        List<Server> servers = new ArrayList<Server>();

        for (int i = 0; i < potentialServers.getLength(); i++) {
            Node potentialServer = potentialServers.item(i);
            if (potentialServer.getAttributes().getNamedItem("type").getNodeValue().equals(protocol)) {
                NodeList attributes = potentialServer.getChildNodes();
                String hostname = null;
                int port = 0;
                String socketType = null;
                for (int j = 0; j < attributes.getLength(); j++) {
                    Node attribute = attributes.item(j);
                    if (attribute.getNodeName().equals("hostname")) {
                        hostname = attribute.getTextContent().trim();
                    } else if (attribute.getNodeName().equals("port")) {
                        port = new Integer(attribute.getTextContent().trim()).intValue();
                    } else if (attribute.getNodeName().equals("socketType")) {
                        socketType = attribute.getTextContent().trim();
                    }
                }
                if (hostname != null && port != 0 && socketType != null) {
                    servers.add(new Server(hostname, port, socketType));
                }
            }
        }
        return servers;
    }

    private static Server getPreferredImapServer(List<Server> servers) {
        // 143 is standard port for IMAP
        for (Server server : servers) {
            if (server.getPort() == 143) {
                return server;
            }
        }

        // 993 is standard port for IMAPS
        for (Server server : servers) {
            if (server.getPort() == 993) {
                return server;
            }
        }

        // Default to first server in list
        return servers.get(0);
    }

    private static Server getPreferredSmtpServer(List<Server> servers) {
        // 587 is preferred SMTP port for mail clients
        for (Server server : servers) {
            if (server.getPort() == 587) {
                return server;
            }
        }

        // 25 is a port for SMTP
        for (Server server : servers) {
            if (server.getPort() == 25) {
                return server;
            }
        }

        // 465 is a deprecated non-standard port
        for (Server server : servers) {
            if (server.getPort() == 465) {
                return server;
            }
        }

        // Default to first server in list
        return servers.get(0);
    }

}
