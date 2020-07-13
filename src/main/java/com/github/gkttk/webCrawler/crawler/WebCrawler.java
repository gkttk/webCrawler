package com.github.gkttk.webCrawler.crawler;

import com.github.gkttk.webCrawler.dto.CrawlingResult;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Kirill Sviridov
 * @version 1.0
 * @since 1.0
 */
public class WebCrawler {

    /**
     * Maximum number of pages visited
     */
    private static final int MAXLINKS = 10000;

    public WebCrawler() {
    }

    /**
     * Crawling URL
     *
     * @param result crawling data collector object
     * @return filled in crawling data object
     * @throws IOException If stream to write the result to a file is interrupted {@link WebCrawler#writeResultCSV(Map)}
     */
    public CrawlingResult crawl(CrawlingResult result) throws IOException {
        /**
         * Initial depth(always 0)
         */
        int startDepth = 0;
        getPageLinks(result, result.getURL(), startDepth, result.getWords());
        writeResultCSV(result.getCountWordsOnPages());
        result.getPagesWithMaxResults().forEach((key, value) -> {
            try {
                getPageLinksForTopResults(result, key, result.getWords());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        result.setPagesWithMaxResultsOut(sortingTopResults(result));
        return result;
    }

    /**
     * Private method for crawl(until the number of links to reach {@link WebCrawler#MAXLINKS} or the
     * maximum depth of({@link CrawlingResult#getMaxDepth()} object will not be reached
     * This method is called recursively until the maximum depth of {@link CrawlingResult#getMaxDepth()} object will not be reached
     *
     * @param result       crawling data collector object
     * @param URL          initial url
     * @param currentDepth Current depth increment in the method until the maximum depth of {@link CrawlingResult#getMaxDepth()}
     *                     object will not be reached
     * @param words        Set of search words
     */
    private void getPageLinks(CrawlingResult result, String URL, int currentDepth, Set<String> words) {
        if ((!result.getVisitedPages().contains(URL) && (currentDepth <= result.getMaxDepth()))) {
            if (result.getVisitedPages().size() == MAXLINKS) {
                return;
            }
            System.out.println("Current depth: " + currentDepth + " [" + URL + "]");
            try {
                result.getVisitedPages().add(URL);
                try {
                    Document document = Jsoup.connect(URL).get();
                    int totalCountWordsOnPage = findWordsOnPage(result, words, document);
                    if (result.getPagesWithMaxResults().size() < 10) {
                        result.getPagesWithMaxResults().put(URL, totalCountWordsOnPage);
                    } else {
                        checkTotalWordsOnPage(result, totalCountWordsOnPage, URL);
                    }
                    Elements linksOnPage = document.select("a[href]");
                    currentDepth++;
                    for (Element page : linksOnPage) {
                        getPageLinks(result, page.attr("abs:href"), currentDepth, words);
                    }
                } catch (IllegalArgumentException a) {
                    System.out.println("For " + URL + " : " + a.getMessage());
                }
            } catch (IOException e) {
                System.err.println("For " + URL + " : " + e.getMessage());
            }
        }
    }

    /**
     * Private words search method on page
     *
     * @param words    set of search words
     * @param document page HTML code
     * @return return total word count on page
     */
    private int findWordsOnPage(CrawlingResult result, Set<String> words, Document document) {
        int totalCountWordsOnPage = 0;
        for (String word : words) {
            int countWordOnPage = countWordOnPage(document, word);
            if (!result.getCountWordsOnPages().containsKey(word)) {
                result.getCountWordsOnPages().put(word, countWordOnPage);
            } else {
                result.getCountWordsOnPages().put(word, countWordOnPage + result.getCountWordsOnPages().get(word));
            }
            totalCountWordsOnPage += countWordOnPage;
        }
        return totalCountWordsOnPage;
    }

    /**
     * Private method for sorting final result
     *
     * @param result crawling data collector object which will be sorted
     * @return sorted result
     */
    private Map<String, List<Integer>> sortingTopResults(CrawlingResult result) {
        return result.getPagesWithMaxResultsOut()
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().stream().reduce(Integer::sum).orElse(0)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Private search method for pages with the maximum number of words found.
     * This method fills in {@link CrawlingResult} object
     *
     * @param result      crawling data collector object
     * @param totalResult Total word count on page
     * @param URL         Page link
     */
    private void checkTotalWordsOnPage(CrawlingResult result, int totalResult, String URL) {
        if (result.getPagesWithMaxResults().values().stream().min(Integer::compareTo).get() >= totalResult) {
        } else {
            String key = Collections.min(result.getPagesWithMaxResults().entrySet(), Map.Entry.comparingByValue()).getKey();
            result.getPagesWithMaxResults().remove(key);
            result.getPagesWithMaxResults().put(URL, totalResult);
        }
    }

    /**
     * Private method for write the result in a CSV file
     *
     * @param countWordsOnPage Total number of words found {@link CrawlingResult#getCountWordsOnPages()}
     * @throws IOException If stream to write the result to a file is interrupted
     */
    private void writeResultCSV(Map<String, Integer> countWordsOnPage) throws IOException {
        String[] headers = new String[]{"Word", "Hits"};
        File file = new File("file.csv");
        try (FileWriter out = new FileWriter(file)) {
            out.write('\ufeff');
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(headers))) {
                countWordsOnPage.forEach((key, value) -> {
                    try {
                        printer.printRecord(key, value);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }


    /**
     * Private method for crawl pages with top results.
     * The method fill {@link CrawlingResult#getCountWordsOnPages()} object
     *
     * @param result crawling data collector object
     * @param URL    page link
     * @param words  set of search words
     * @throws IOException
     */
    private void getPageLinksForTopResults(CrawlingResult result, String URL, Set<String> words) throws IOException {
        Document document = Jsoup.connect(URL).get();
        List<Integer> list = new ArrayList<>(words.size());
        for (String word : words) {
            int resultCount = countWordOnPage(document, word);
            list.add(resultCount);
        }
        list.add(list.stream().reduce(Integer::sum).orElse(0));
        result.getPagesWithMaxResultsOut().put(URL, list);
    }

    /**
     * Private method for counting the number of word on page
     *
     * @param document page HTML code
     * @param word     search word
     * @return word count
     */
    private int countWordOnPage(Document document, String word) {
        int count = 0;
        Pattern pattern = Pattern.compile(word);
        Matcher matcher = pattern.matcher(document.toString());
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}

