package annoying34.website;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class Spider {
    private static final Logger log = LogManager.getLogger();
    private static final String[] prioList = {"presse", "service", "info", "kontakt", "feedback", "mail", "hilfe", "impressum", "datenschutz"};
    private static final int MAX_PAGES_TO_SEARCH = 50;

    private final Set<String> pagesVisited = new HashSet<>();
    private final List<String> pagesToVisit = new LinkedList<>();

    private static boolean isPrio(String context) {
        for (String keyword : prioList) {
            if (context.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private static boolean emailHasSameDomain(String url, String context) {
        String domainUrl = getDomainName(url);
        return !StringUtils.isEmpty(domainUrl) && !StringUtils.isEmpty(context) && context.contains(domainUrl);

    }

    private static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException | NullPointerException e) {
            return "";
        }
    }

    public CrawlerResult search(String urlString) throws IOException {
        List<String> emails = new LinkedList<>();
        String favIconURL = null;
        String highPrioEmail = null;

        String url = prependProtocolIfNeccessary(urlString);
        pagesToVisit.add(url);

        try {
            while (pagesVisited.size() < MAX_PAGES_TO_SEARCH && highPrioEmail == null) {
                String currentUrl;
                SpiderLeg leg = new SpiderLeg();
                if (pagesToVisit.isEmpty()) {
                    break;
                } else {
                    currentUrl = getNextUrl();
                    pagesVisited.add(currentUrl);
                }
                if (currentUrl == null) {
                    break;
                }
                leg.crawl(currentUrl);
                pagesToVisit.addAll(leg.getLinks());
                List<String> mails = new ArrayList<>(leg.getEmails());
                emails.addAll(mails);
                Optional<String> prio = mails.stream()
                        .filter(Spider::isPrio)
                        .filter(mail -> Spider.emailHasSameDomain(url, mail))
                        .findAny();
                if (prio.isPresent()) {
                    highPrioEmail = prio.get();
                }

                if (favIconURL == null || Objects.equals(favIconURL, "")) {
                    favIconURL = leg.relativeFavIcon();
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        String email = ((emails.size() == 0) ? null : emails.get(0));
        if (highPrioEmail != null) {
            email = highPrioEmail;
        }
        return new CrawlerResult(new URL(url).getHost(), email, favIconURL);
    }

    private String prependProtocolIfNeccessary(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * Gibt die nächste, unbesuchte URL zurück.
     */
    private String getNextUrl() {
        pagesToVisit.removeAll(pagesVisited);
        if (pagesToVisit.isEmpty()) {
            return null;
        }

        Optional<String> impressumsURL = pagesToVisit.stream()
                .filter(e -> e.contains("mpressum"))
                .findAny();

        if (impressumsURL.isPresent()) {
            String url = impressumsURL.get();
            pagesToVisit.remove(url);
            return url;
        } else {
            pagesToVisit.sort((o1, o2) -> {
                Integer isO1Prio = (Spider.isPrio(o1) ? 1 : 0);
                Integer isO2Prio = (Spider.isPrio(o2) ? 1 : 0);
                return isO2Prio.compareTo(isO1Prio);
            });
            String nextUrl = pagesToVisit.remove(0);
            return nextUrl;
        }
    }
}