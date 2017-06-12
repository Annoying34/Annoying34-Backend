package annoying34.website;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderLeg {
    //  use a fake USER_AGENT so the web server thinks the robot is a
    // normal web browser.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<String>();
    private Document htmlDocument;


    /**
     * die Methode crawl vollzieht einen HTTP request, fragt die response ab
     * und collected dann die ganzen Links auf der Seite
     */
    public boolean crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;


            if (connection.response().statusCode() == 200) // 200 is the HTTP OK
            // status code

            {
                System.out.println("\n**Seite wird besucht..* Seite gefunden: " + url);
            }
            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** no HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[href]");

            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
            }
            //sammle die Favicons
            Element element = htmlDocument.head().select("link[href~=.*\\.(ico|png)]").first();
            System.out.println("Link zum FavIcon: ");
            System.out.println(element.attr("href"));
            System.out.println("\n");


            return true;
        } catch (IOException ioe) {
            //  not successful in our HTTP request
            return false;
        }
    }

    // sucht nach Email-Adressen mithilfe von regulären Ausdrücken
    public void searchFormail(String searchWord) {

        Pattern pattern = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})");
        Pattern pattern2 = Pattern.compile("([\\w\\-]([\\.\\w])+[\\w]+	\\[at\\]([\\w\\-]+\\.)+[A-Za-z]{2,4})");


        Matcher matchs = pattern.matcher(searchWord);
        Matcher matchs2 = pattern2.matcher(searchWord);


        if (matchs.find() || matchs2.find()) {

            System.out.println(searchWord.substring(matchs.start(), matchs.end()));


        }


    }

    public List<String> getLinks() {
        return this.links;
    }

}