package com.evan.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public final class WebPageUtils {

    public static Document load(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static Set<String> getLinks(Document doc) {
        Elements links = doc.select("a");
        Set<String> ret = new HashSet<>();
        for(Element link : links) {
            ret.add(link.attr("abs:href").toString());
        }
        return ret;
    }

    public static Set<String> getWords(Document doc) {
        return Arrays.stream(doc.body().text().split("\\W+"))
            .map(str -> str.toLowerCase())
            .collect(Collectors.toSet());
    }
}