package annoying34.website;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
<<<<<<< Updated upstream
=======
import java.net.URLConnection;
>>>>>>> Stashed changes
import java.util.*;

public class Spider {

<<<<<<< Updated upstream
    // bestimmte die Anzahl der zu crawlenden Seiten
    private static final int MAX_PAGES_TO_SEARCH = 10;
    String someString;
    private Set<String> pagesVisited = new HashSet<String>();
    private List<String> pagesToVisit = new LinkedList<String>();

    public CrawlerResult search(String url) throws IOException {

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }

        LinkedList<String> emails = new LinkedList<String>();
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

            if (favIconURL == null || favIconURL == "") {
                favIconURL = leg.relativeFavIcon();
            }

            bufferreader.close();

            this.pagesToVisit.addAll(leg.getLinks());
        }

        // TODO: This email might be wrong, we should add better logic to determine which is the support email address.
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
=======
	// bestimmte die Anzahl der zu crawlenden Seiten
	private static final int MAX_PAGES_TO_SEARCH = 10;
	String someString;
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();

	public CrawlerResult search(String url) throws IOException, FileNotFoundException {

		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "http://" + url;
		}

		// URL u = new URL(url);
		// URLConnection uc = u.openConnection();

		LinkedList<String> emails = new LinkedList<String>();
		String favIconURL = "";

		try {
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

				// if(urlbuffer.getPort()>=-1) {

				BufferedReader bufferreader = new BufferedReader(new InputStreamReader(urlbuffer.openStream()));

				while ((someString = bufferreader.readLine()) != null) {
					emails.add(leg.searchFormail(someString));
					
					
				}
				
				if (favIconURL == null || favIconURL == "") {
					favIconURL = leg.relativeFavIcon();
				}

				bufferreader.close();

				this.pagesToVisit.addAll(leg.getLinks());
				// }
				
				
				
				
			}
	
				
					
			
			
		} // try
		catch (Exception e) {
			System.out.println("");

		}
		// TODO: This email might be wrong, we should add better logic to
		// determine which is the support email address.
		
		
		
		return new CrawlerResult(new URL(url).getHost(), emails.getFirst(), favIconURL);

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

		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}
	
	
	
}
>>>>>>> Stashed changes
