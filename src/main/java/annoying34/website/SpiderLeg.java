package annoying34.website;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderLeg {
    // use a fake USER_AGENT so the web server thinks the robot is a
    // normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("[A-Z0-9._%+-]+(@|\\(at\\)|\\[at\\])[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);
    private List<String> links = new ArrayList<>();
    private Set<String> emails = new HashSet<>();
    private Document htmlDocument;

    /**
     * Die Methode crawl vollzieht einen HTTP Request, fragt die Response ab und
     * sammelt dann die ganzen Links auf der Seite
     */
    public boolean crawl(String url) {
        url = url.replaceAll("[\\s|\\u00A0]+", "");
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).ignoreHttpErrors(true);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;
            if (connection.response().statusCode() >= 200 && connection.response().statusCode() < 400) {
                Elements linksOnPage = htmlDocument.select("a[href]");
                for (Element link : linksOnPage) {
                    links.add(removeAnchorFromLink(link.absUrl("href")));
                }
                emails.addAll(searchMailAddressesInPageText(htmlDocument.body().text()));
                return true;
            }
            return false;
        } catch (Exception e) {
            // not successful in our HTTP request
            return false;
        }
    }

    private String removeAnchorFromLink(String foundUrl) {
        if (foundUrl.contains("#")) {
            return foundUrl.split("#")[0];
        } else {
            return foundUrl;
        }
    }

    public String relativeFavIcon() {
        try {
            Element element = htmlDocument.head().select("link[href~=.*\\.(ico|png|svg)").first();
            return element.attr("abs:href");

        } catch (Exception e) {
            return null;
        }
    }

    public List<String> searchMailAddressesInPageText(String pageText) {
        List<String> list = new ArrayList<>();
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(pageText);
        while (matcher.find()) {
            String mail = matcher.group();
            list.add(mail.replace("(at)", "@").replace("[at]", "@"));
        }
        return list;
    }

    public List<String> getLinks() {
        return links;
    }

    public Set<String> getEmails() {
        return emails;
    }
}