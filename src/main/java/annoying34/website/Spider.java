package annoying34.website;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Spider {

    // bestimmte die Anzahl der zu crawlenden Seiten
    private static final int MAX_PAGES_TO_SEARCH = 30;
    Logger log = LogManager.getLogger();
    String someString;
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();

    public CrawlerResult search(String url) throws IOException {

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        LinkedList<String> emails = new LinkedList<>();
        String favIconURL = "";

        while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            SpiderLeg leg = new SpiderLeg();
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                this.pagesVisited.add(url);
            } else {
                currentUrl = this.nextUrl();
            }

            if (currentUrl == null) {
                break;
            }

            leg.crawl(currentUrl); // rufe crawl in SpiderLeg auf

            URL urlbuffer = new URL(currentUrl);

            BufferedReader bufferreader = new BufferedReader(new InputStreamReader(urlbuffer.openStream()));

            while ((someString = bufferreader.readLine()) != null) {
                emails.add(leg.searchFormail(someString));
            }

            if (StringUtils.isEmpty(favIconURL)) {
                favIconURL = leg.relativeFavIcon();
            }

            bufferreader.close();

            this.pagesToVisit.addAll(leg.getLinks());
        }

        // TODO: This email might be wrong, we should add better logic to determine which is the support email address.
        log.info(emails.toString());
        return new CrawlerResult(new URL(url).getHost(), emails.getFirst(), favIconURL);
    }

    /**
     * Returnt die nächste URL, die man besuchen möchte. Außerdem stellen wir sicher
     * dass keine URL mehrmals besucht wird.
     */
    private String nextUrl() {
        String nextUrl;

        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));

        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }
}
