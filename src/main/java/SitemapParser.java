import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class SitemapParser {

    private static String siteUrl = "https://wp-dev.space/otm/zymac/develop/";
    private static String sitemapUrl = siteUrl + "sitemap_index.xml";
    private static HashSet<String> hrefHashSet = new HashSet<String>();

    public static void main(String[] args) throws IOException {

        Elements sitemaps = getElementsFromXml(sitemapUrl);
        for (Element sitemap : sitemaps) {
            Elements sitemapUrls = getElementsFromXml(sitemap.text());
            for (Element sitemapUrl : sitemapUrls) {
                Elements hrefs = getElementsFromHtml(sitemapUrl.text());
                for (Element href : hrefs) {
                    String link = href.attr("href");
                    if (!link.contains("#") && !link.contains("tel") && !link.contains("@")&& !link.isEmpty()) {
                        hrefHashSet.add(link);
                    }
                }
            }
        }
        System.out.println("Total set size: " + hrefHashSet.size());
        for (String linkHashSet : hrefHashSet) {
            verifyLink(linkHashSet);
        }
    }

    private static Elements getElementsFromXml(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements xmlLinks = doc.select("loc");
        return xmlLinks;
    }

    private static Elements getElementsFromHtml(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements href = doc.select("a[href]");
        return href;
    }

    private static void verifyLink(String locInternalUrl) {
        try {
            URL url = new URL(locInternalUrl);
            HttpURLConnection httpURLConnect = (HttpURLConnection) url.openConnection();
            httpURLConnect.setConnectTimeout(3000);
            httpURLConnect.connect();
            if (httpURLConnect.getResponseCode() == 200) {
                System.out.println(locInternalUrl + " - " + httpURLConnect.getResponseMessage());
            }
            if (httpURLConnect.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                System.out.println(locInternalUrl + " - " + httpURLConnect.getResponseMessage() + " - " + HttpURLConnection.HTTP_NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}