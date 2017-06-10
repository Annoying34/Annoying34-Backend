package annoying34.crawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Spider {

	// bestimmte die Anzahl der zu crawlenden Seiten
	private static final int MAX_PAGES_TO_SEARCH = 10; 
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();
	String someString;

	
	public CrawlerResult search(String url) throws IOException {
		while (this.pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
			String currentUrl;
			SpiderLeg leg = new SpiderLeg();
			if (this.pagesToVisit.isEmpty()) {
				currentUrl = url;
				this.pagesVisited.add(url);
			} else {
				currentUrl = this.nextUrl();
			}
			leg.crawl(currentUrl); // rufe crawl in SpiderLeg auf

			URL urlbuffer = new URL(currentUrl);

			BufferedReader bufferreader = new BufferedReader(new InputStreamReader(urlbuffer.openStream()));
			System.out.println("Folgende Emails gefunden:" );
			while ((someString = bufferreader.readLine()) != null) {

				leg.searchFormail(someString);
			}
			
			
			bufferreader.close();

			this.pagesToVisit.addAll(leg.getLinks());
		}
		System.out.println("\n**Done** Besuchte Seiten: " + this.pagesVisited.size() + " Seiten");

		return new CrawlerResult("email", "favicon");
	}

	/**
	 * Returnt die nächste URL, die man besuchen möchte. Außerdem stellen wir sicher
	 * dass keine URL mehrmals besucht wird. 
	 * 
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
