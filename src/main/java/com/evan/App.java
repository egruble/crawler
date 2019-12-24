package com.evan;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.evan.models.LinkDiagnostic;
import com.evan.utils.WebPageUtils;

import org.jsoup.nodes.Document;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws IOException
    {
        String url = "https://www.mediacomcable.com/index.html";
        
        Queue<String> toVisit = new ArrayDeque<>();
        Map<String, LinkDiagnostic> visited = new HashMap<>();
        visited.put(url, new LinkDiagnostic(url));
        toVisit.add(url);

        int i = 0;

        while(!toVisit.isEmpty()) {
            System.out.println("Count: " + ++i + " Depth: " + toVisit.size());
            url = toVisit.poll();
            try {
                Document doc = WebPageUtils.load(url);
                Set<String> links = WebPageUtils.getLinks(doc);
                LinkDiagnostic diag = visited.get(url);
                for(String link : links) {
                    diag.addSourceUrl(link);
                    if(!visited.containsKey(link) && link.contains("mediacomcable.com")) {
                        visited.put(link, new LinkDiagnostic(link));
                        toVisit.add(link);
                    }
                }
            } catch (Exception e) {
                visited.get(url).setError(e);
            }
        }

        System.out.println("Links with errors:");
        for(LinkDiagnostic d : visited.values()) {
            if (d.getError().isPresent()) {
                System.out.println(d.getUrl() + " - " + d.getError().get().getMessage());
            }
        }
    }
}
