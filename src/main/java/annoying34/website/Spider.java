package annoying34.website;

import sun.security.provider.ConfigFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Spider {

	// bestimmte die Anzahl der zu crawlenden Seiten
	private static final int MAX_PAGES_TO_SEARCH = 100;
	String someString;
	String highPrioEmail;
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
				
				if (favIconURL == null || favIconURL == "") {
					favIconURL = leg.relativeFavIcon();
				}

				bufferreader.close();

				this.pagesToVisit.addAll(leg.getLinks());
				
			}
		} // try
		catch (Exception e) {

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

		this.pagesToVisit.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				Integer isO1Prio = (Spider.isPrio(o1) ? 1 : 0);
				Integer isO2Prio = (Spider.isPrio(o2) ? 1 : 0);

				return isO2Prio.compareTo(isO1Prio);
			}
		});

		this.pagesVisited.add(nextUrl);
		return nextUrl;
	}
	
	private static boolean isPrio(String context) {
		String[] prioList = {"service", "info", "feedback", "mail", "hilfe", "impressum", "datenschutz"};

		for (String keyword:prioList) {
			if (context.toLowerCase().contains(keyword.toLowerCase())) {
				return true;
			}
		}

		return false;
	}
}
