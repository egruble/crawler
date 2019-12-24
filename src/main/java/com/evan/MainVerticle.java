package com.evan;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;

public class MainVerticle extends AbstractVerticle {
    Set<String> visited = new HashSet<>();

    @Override
    public void start() {
        WebClient client = WebClient.create(vertx);
        EventBus eb = vertx.eventBus();

        MessageConsumer<String> newUrl = eb.consumer("url.new");
        newUrl.handler(message -> {
            try {
                client.getAbs(message.body())
                .send(ar -> {
                    if(ar.succeeded()) {
                        System.out.println(visited.size() + ": " + message.body() + " - " + ar.result().statusCode());
                        switch (ar.result().statusCode()) {
                            case 200:
                                JsonObject headers = new JsonObject();
                                for(Entry<String, String> header : ar.result().headers().entries()) {
                                    headers.put(header.getKey(), header.getValue());
                                }
                                JsonObject event = new JsonObject()
                                    .put("body", ar.result().bodyAsString())
                                    .put("headers", headers);
                                eb.publish("url.valid", event);
                                break;
                            default:
                        }
                    } else {
                        //System.out.println(ar.cause());
                    }
                });
                
            } catch (Exception e) {
                //System.out.println(e.getMessage());
            }
        });
        
        MessageConsumer<JsonObject> validUrl = eb.consumer("url.valid");
        validUrl.handler(message -> {
            String contentType = message.body().getJsonObject("headers").getString("Content-Type").split(";")[0];
            switch(contentType) {
                case "text/html":
                    Elements links = Jsoup.parse(message.body().getString("body")).select("a");
                    for (Element link : links) {
                        String href = link.attr("abs:href");
                        if(!visited.contains(href) && href != null && !href.isEmpty()) {
                            visited.add(href);
                            eb.publish("url.new", href);
                        }
                    }
                    break;
                default:
                    System.out.println("Unsupported content type: " + contentType);
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MainVerticle(), ar -> {
            if(ar.succeeded()) {
                vertx.eventBus().publish("url.new", "https://mediacomcable.com");
            }
        });
    }
}