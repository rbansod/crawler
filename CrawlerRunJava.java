import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
  private static Set<String> visitedUrls = new HashSet<>();

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter file name containing URLs: ");
    String fileName = scanner.nextLine();
    scanner.close();

    // Read URLs from file
    try (Scanner fileScanner = new Scanner(WebCrawler.class.getClassLoader().getResourceAsStream(fileName))) {
      while (fileScanner.hasNextLine()) {
        String url = fileScanner.nextLine().trim();
        if (!url.isEmpty()) {
          crawl(url);
        }
      }
    } catch (Exception e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
  }

  private static void crawl(String url) {
    if (visitedUrls.contains(url)) {
      return;
    }
    visitedUrls.add(url);

    try {
      Document doc = Jsoup.connect(url).get();
      String text = doc.body().text();
      System.out.println("Text from URL " + url + ": " + text);

      Elements links = doc.select("a[href]");
      for (Element link : links) {
        crawl(link.absUrl("href"));
      }
    } catch (IOException e) {
      System.err.println("Error connecting to URL " + url + ": " + e.getMessage());
    }
  }
}
