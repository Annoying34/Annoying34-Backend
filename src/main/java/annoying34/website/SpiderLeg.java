package annoying34.website;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpiderLeg {
	// use a fake USER_AGENT so the web server thinks the robot is a
	// normal web browser.
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.112 Safari/535.1";
    private List<String> links = new LinkedList<>();
    private Document htmlDocument;

	/**
	 * die Methode crawl vollzieht einen HTTP request, fragt die response ab und
	 * collected dann die ganzen Links auf der Seite
	 */
	public boolean crawl(String url) {

		try {

			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT).ignoreHttpErrors(true);
			Document htmlDocument = connection.get();
			this.htmlDocument = htmlDocument;

			// if (!connection.response().contentType().contains("text/html")) {
			// return false;
			// }
			if (connection.response().statusCode() >= 200 && connection.response().statusCode() < 400) {

				Elements linksOnPage = htmlDocument.select("a[href]");

				for (Element link : linksOnPage) {
					this.links.add(link.absUrl("href"));
				}

				return true;
			}

			return false;
		} catch (Exception e) {
			// not successful in our HTTP request
			return false;
		}
	}

	public String relativeFavIcon() {
		try{
		 Element element = htmlDocument.head().select("link[href~=.*\\.(ico|png|svg)").first();
		 return element.attr("abs:href");

		}
		catch(Exception e){
			return null;
		}
	}

	// sucht nach Email-Adressen mithilfe von regulären Ausdrücken
	public String searchFormail(String searchWord) {

		Pattern pattern = Pattern
				.compile("([_A-Za-z0-9-]+)(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})");
        Pattern pattern2 = Pattern.compile("([\\w\\-]([.\\w])+[\\w]+	\\(at\\)([\\w\\-]+\\.)+[A-Za-z]{2,4})");

		Matcher matchs = pattern.matcher(searchWord);
		Matcher matchs2 = pattern2.matcher(searchWord);

		if (matchs.find() || matchs2.find()) {
			return searchWord.substring(matchs.start(), matchs.end());

		}

		return null;
	}

	public List<String> getLinks() {
		return this.links;
	}
}