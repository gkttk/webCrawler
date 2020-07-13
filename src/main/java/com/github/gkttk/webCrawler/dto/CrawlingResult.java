package com.github.gkttk.webCrawler.dto;

import java.util.*;


/**
 * @author Kirill Sviridov
 * @version 1.0
 * @since 1.0
 * Data object with crawling result
 */
public class CrawlingResult {

    /**
     * initial url(depth = 0)(set by user)
     */
    private String URL;

    /**
     * Maximum depth for crawling(set by user)
     */
    private int maxDepth;


    /**
     * search words(set by user)git
     */
    private Set<String> words;
    /**
     * Crawler visited pages
     */
    private HashSet<String> visitedPages;

    /**
     * Total number of words found
     */
    private Map<String, Integer> countWordsOnPages;

    /**
     * Top 10 pages by number of words found
     */
    private Map<String, Integer> pagesWithMaxResults;

    /**
     * Top 10 pages by number of words found(for output)
     */
    private Map<String, List<Integer>> pagesWithMaxResultsOut;

    public CrawlingResult(String URL, int maxDepth, Set<String> words) {
        this.URL = URL;
        this.maxDepth = maxDepth;
        this.words = words;
        this.visitedPages = new HashSet<>();
        this.countWordsOnPages = new HashMap<>(5);
        this.pagesWithMaxResults = new HashMap<>(10);
        this.pagesWithMaxResultsOut = new HashMap<>(10);
    }


    /**
     * Method for output data on jsp table
     */
    public int countWordOnPage(String word) {
        return countWordsOnPages.get(word);
    }

    /**
     * Method for output data on jsp table
     */
    public int totalWordsResult() {
        return countWordsOnPages.values().stream().reduce(Integer::sum).orElse(0);
    }

    /**
     * Method for output data on jsp table
     */
    public Set<String> getTopURLs(){
        return pagesWithMaxResultsOut.keySet();
    }

    /**
     * Method for output data on jsp table
     */
    public String getInfoTopResult(String word){
        String reduce = pagesWithMaxResultsOut.get(word)
                .stream()
                .map(element -> element.toString() + " ")
                .reduce((element1, element2) -> element1 + element2).orElse(" ");

        return word + reduce;


    }



    public Set<String> getWords() {
        return words;
    }

    public HashSet<String> getVisitedPages() {
        return visitedPages;
    }

    public Map<String, Integer> getPagesWithMaxResults() {
        return pagesWithMaxResults;
    }

    public String getURL() {
        return URL;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setPagesWithMaxResultsOut(Map<String, List<Integer>> pagesWithMaxResultsOut) {
        this.pagesWithMaxResultsOut = pagesWithMaxResultsOut;
    }

    public Map<String, Integer> getCountWordsOnPages() {
        return countWordsOnPages;
    }

    public Map<String, List<Integer>> getPagesWithMaxResultsOut() {
        return pagesWithMaxResultsOut;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(URL).append(" ");
        countWordsOnPages.forEach((k, v) -> stringBuilder.append(" ").append(k).append(" - ").append(v));
        stringBuilder.append("<br>").append("Total : ");
        stringBuilder.append(countWordsOnPages.values().stream().reduce(Integer::sum).orElse(0)).append("<br>");
        pagesWithMaxResultsOut
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().stream().reduce(Integer::sum).orElse(0)))
                .forEach(entry -> {
                    stringBuilder.append(entry.getKey()).append(" ");
                    entry.getValue().forEach(element -> stringBuilder.append(element).append(" "));
                    stringBuilder.append("<br>");
                });
        return stringBuilder.toString();

    }
}
