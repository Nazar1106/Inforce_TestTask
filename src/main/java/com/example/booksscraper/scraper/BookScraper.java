package com.example.booksscraper.scraper;

import com.example.booksscraper.dto.BookDTO;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Component
public class BookScraper {
    public static final String CSS_QUERY_POD = ".product_pod";
    public static final String CSS_QUERY_h3 = "h3 a";
    public static final String ATTRIBUTE_KEY = "title";
    public static final String ATTRIBUTE_KEY_TITLE = ATTRIBUTE_KEY;
    public static final String CSS_QUERY_PRICE_COLOR = ".price_color";
    public static final String CSS_QUERY_RATING = "p.star-rating";
    public static final String ATTRIBUTE_KEY_CLASS = "class";
    public static final String TARGET_STAR_RATING = "star-rating ";
    public static final String REPLACEMENT = "";
    public static final String CSS_QUERY_AVAILABILITY = ".instock.availability";
    public static final String REGEX = "[^0-9.,]";
    public static final char OLD_CHAR = ',';
    public static final char NEW_CHAR = '.';
    public static final String CSS_QUERY_NEXT_A = ".next a";
    public static final String ATTRIBUTE_KEY_NEXT_PAGE = "href";
    private final AsyncHttpClient asyncHttpClient;

    @Value("${scraper.firstPage.url}")
    private String firstPageUrl;

    public BookScraper() {
        this.asyncHttpClient = Dsl.asyncHttpClient();
    }

    public CompletableFuture<List<BookDTO>> scrapeAllPages(String startUrl) {
        List<BookDTO> bookList = new ArrayList<>();
        CompletableFuture<Void> allPagesScraped = scrapePageRecursively(startUrl, bookList);
        return allPagesScraped.thenApply(v -> bookList);
    }

    private CompletableFuture<Void> scrapePageRecursively(String startUrl, List<BookDTO> bookList) {
        return fetchPage(startUrl).thenCompose(document -> {
            extractBooksFromPage(document, bookList);
            String nextPageUrl = getNextPage(document, firstPageUrl);
            if (nextPageUrl != null) {
                return scrapePageRecursively(nextPageUrl, bookList);
            } else {
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    private CompletableFuture<Document> fetchPage(String url) {
        return asyncHttpClient.prepareGet(url).execute()
                .toCompletableFuture()
                .thenApply(Response::getResponseBody)
                .thenApply(Jsoup::parse);
    }

    private void extractBooksFromPage(Document document, List<BookDTO> bookList) {
        Elements books = document.select(CSS_QUERY_POD);
        for (Element book : books) {
            String title = book.select(CSS_QUERY_h3).attr(ATTRIBUTE_KEY_TITLE);
            String priceText = book.select(CSS_QUERY_PRICE_COLOR).text();
            double price = convertPrice(priceText);
            String availability = book.select(CSS_QUERY_AVAILABILITY).text().trim();
            String rating = book.select(CSS_QUERY_RATING)
                    .attr(ATTRIBUTE_KEY_CLASS).replace(TARGET_STAR_RATING, REPLACEMENT);
            bookList.add(new BookDTO(title, price, availability, rating));
        }
    }

    private double convertPrice(String priceText) {
        String cleanedPrice = priceText.replaceAll(REGEX, REPLACEMENT);
        return Double.parseDouble(cleanedPrice.replace(OLD_CHAR, NEW_CHAR));
    }

    private static String getNextPage(Document document, String firstPageUrl) {
        Element nextPage = document.select(CSS_QUERY_NEXT_A).first();
        if (nextPage != null) {
            return firstPageUrl + nextPage.attr(ATTRIBUTE_KEY_NEXT_PAGE);
        } else {
            return null;
        }
    }
}
