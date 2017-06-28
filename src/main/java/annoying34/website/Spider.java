package annoying34.website;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

@Component
public class Spider {

    // bestimmte die Anzahl der zu crawlenden Seiten
    private static final int MAX_PAGES_TO_SEARCH = 100;
    Logger log = LogManager.getLogger();
    private String someString;
    private String highPrioEmail;
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();

    private static boolean isPrio(String context) {
        String[] prioList = {"service", "info", "feedback", "mail", "hilfe", "impressum", "datenschutz"};

        for (String keyword : prioList) {
            if (context.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public CrawlerResult search(String url) throws IOException {

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        // URL u = new URL(url);
        // URLConnection uc = u.openConnection();

        LinkedList<String> emails = new LinkedList<>();
        String favIconURL = "";

        try {
            while (this.pagesToVisit.isEmpty() == false && this.pagesVisited.size() < MAX_PAGES_TO_SEARCH && highPrioEmail == null) {
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
                    String email = leg.searchFormail(someString);
                    if (email != null) {
                        emails.add(email);

                        if (Spider.isPrio(email)) {
                            highPrioEmail = email;
                        }
                    }
                }

                if (favIconURL == null || Objects.equals(favIconURL, "")) {
                    favIconURL = leg.relativeFavIcon();
                }

                bufferreader.close();

                this.pagesToVisit.addAll(leg.getLinks());

            }
        } // try
        catch (Exception e) {
            log.error("", e);
        }
        // TODO: This email might be wrong, we should add better logic to
        // determine which is the support email address.

        String email = ((emails.size() == 0) ? null : emails.getFirst());

        if (highPrioEmail != null) {
            email = highPrioEmail;
        }

        return new CrawlerResult(new URL(url).getHost(), email, favIconURL);

    }

    /**
     * Returnt die nächste URL, die man besuchen möchte. Außerdem stellen wir
     * sicher dass keine URL mehrmals besucht wird.
     */
    private String nextUrl() {
        String nextUrl;

        do {
            nextUrl = this.pagesToVisit.remove(0);
        } while (this.pagesVisited.contains(nextUrl));

        this.pagesToVisit.sort((o1, o2) -> {
            Integer isO1Prio = (Spider.isPrio(o1) ? 1 : 0);
            Integer isO2Prio = (Spider.isPrio(o2) ? 1 : 0);

            return isO2Prio.compareTo(isO1Prio);
        });

        this.pagesVisited.add(nextUrl);
        return nextUrl;
    }
}
