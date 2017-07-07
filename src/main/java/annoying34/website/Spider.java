package annoying34.website;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spider {
	private static final int MAX_PAGES_TO_SEARCH = 200;
	private static final Logger log = LogManager.getLogger();
	private Set<String> pagesVisited = new HashSet<String>();
	private List<String> pagesToVisit = new LinkedList<String>();

	public CrawlerResult search(String url) throws IOException, FileNotFoundException {
		LinkedList<String> emails = new LinkedList<String>();
		String favIconURL = null;
		String highPrioEmail = null;

		url = prependProtocolIfNeccessary(url);

		try {
			while (pagesVisited.size() < MAX_PAGES_TO_SEARCH && highPrioEmail == null) {
				String currentUrl;
				String line;
				SpiderLeg leg = new SpiderLeg();
				if (pagesToVisit.isEmpty()) {
					currentUrl = url;
					pagesVisited.add(url);
				} else {
					currentUrl = getNextUrl();
					pagesVisited.add(currentUrl);
				}
				if (currentUrl == null) {
					break;
				}

				leg.crawl(currentUrl);
				pagesToVisit.addAll(leg.getLinks());

				URL urlbuffer = new URL(currentUrl);
				BufferedReader bufferreader = new BufferedReader(new InputStreamReader(urlbuffer.openStream()));

				while ((line = bufferreader.readLine()) != null) {
					String email = leg.searchForMail(line);
					if (email != null) {
						emails.add(email);
						if (Spider.isPrio(email)) {
							highPrioEmail = email;
						}
					}
				}
				bufferreader.close();

				if (favIconURL == null || favIconURL == "") {
					favIconURL = leg.relativeFavIcon();
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		String email = ((emails.size() == 0) ? null : emails.getFirst());
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
	 * Returnt die nächste URL, die man besuchen möchte. Außerdem stellen wir
	 * sicher dass keine URL mehrmals besucht wird.
	 */
	private String getNextUrl() {
		String nextUrl;
		do {
			nextUrl = pagesToVisit.remove(0);
		} while (pagesVisited.contains(nextUrl));

		pagesToVisit.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Integer isO1Prio = (Spider.isPrio(o1) ? 1 : 0);
				Integer isO2Prio = (Spider.isPrio(o2) ? 1 : 0);
				return isO2Prio.compareTo(isO1Prio);
			}
		});
		return nextUrl;
	}

	private static boolean isPrio(String context) {
		String[] prioList = { "presse", "service", "info", "kontakt", "feedback", "mail", "hilfe", "impressum", "datenschutz" };
		for (String keyword : prioList) {
			if (context.toLowerCase().contains(keyword.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}