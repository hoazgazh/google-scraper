import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class GoogleScraper {
    public static void main(String[] args) throws IOException, JSONException {

        String googleUrl = "https://www.google.com/search?q=youtube#ip=1";

        // Connect to the Google search page
        Document doc = Jsoup.connect(googleUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36")
                .get();

        int c = 0;
        Elements results = doc.select("div.g");
        System.out.println("start print row site link =========");
        JSONArray jsonArray = new JSONArray();

        for (Element result : results) {
            // Extract the title, link, and content of the result
            String title = result.select("h3").text();
            String link = result.select("a").attr("href");

            Element span = result.select("span").last().parent();
            String test = result.select("div[data-snf]:not(:has(a)) span").text();

            // Create a JSONArray for the "Site link"
            JSONArray siteLinksArray = new JSONArray();

            // Iterate through all h3 elements within the current result
            Elements aElements = result.select("a[href]");
            //System.out.println(aElements);

            for (Element aElement : aElements) {
                // Add each h3 text to the siteLinksArray only if a link is present
                String h3Text = aElement.text();
                String h3Link = aElement.select("a").attr("href");
                if (!h3Link.isEmpty() && !Objects.equals(link, h3Link)) {
                    JSONObject h3Object = new JSONObject();
                    h3Object.put("text", h3Text);
                    h3Object.put("link", h3Link);
                    siteLinksArray.put(h3Object);
                }
            }

            // Create a JSON object for each result
            JSONObject resultObject = new JSONObject();
            resultObject.put("title", title);
            resultObject.put("link", link);
            resultObject.put("content", test);
            resultObject.put("position", c + 1);

            // Add the siteLinksArray to resultObject only if it is not empty
            if (siteLinksArray.length() > 0) {
                resultObject.put("siteLinks", siteLinksArray);
            }

            // Add the JSON object to the array
            jsonArray.put(resultObject);

            c++;
        }

        // Convert the JSON array to a string and print it
        String jsonOutput = jsonArray.toString(2); // The second parameter is for indentation level
        System.out.println(jsonOutput);
    }
}
