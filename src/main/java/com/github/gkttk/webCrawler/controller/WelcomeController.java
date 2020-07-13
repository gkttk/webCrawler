package com.github.gkttk.webCrawler.controller;

import com.github.gkttk.webCrawler.dto.CrawlingResult;
import com.github.gkttk.webCrawler.crawler.WebCrawler;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Controller
public class WelcomeController {

    private WebCrawler webCrawler;

    public WelcomeController(WebCrawler webCrawler) {
        this.webCrawler = webCrawler;
    }

    @GetMapping("/")
    public String welcome() {
        return "index";
    }

    @PostMapping("/crawl")
    public String crawl(HttpServletRequest request) throws IOException {
       request.setCharacterEncoding("cp1251");
        String url = request.getParameter("url");
        int depth = Integer.parseInt(request.getParameter("depth"));
        String[] wordsArray = request.getParameterValues("words");
        Set<String> words = new HashSet<>(Arrays.asList(wordsArray));
        CrawlingResult crawlingResult = new CrawlingResult(url, depth, words);
        request.getSession().setAttribute("result", webCrawler.crawl(crawlingResult));
        return "index";
    }


    @GetMapping(value = "/downloadResult")
    public ResponseEntity<byte[]> downloadResult() {
        File file = new File("file.csv");
        byte[] content = new byte[0];
        try {
            if (file.exists()) {
                content = Files.readAllBytes(file.toPath());
            } else {
                throw new RuntimeException("File not found");
            }
        } catch (IOException ex) {
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentLength(content.length);
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }


    @GetMapping("/test")
    public String test() {
        return "test";
    }

}

