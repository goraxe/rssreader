package com.github.goraxe.rss_reader.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by goraxe on 2016-03-28.
 */
@Document
public class RSSFeed {
    @Id
    private String url;
    private String name;
    private String title;

    public RSSFeed(String name, String title, String url) {
        this.name = name;
        this.title = title;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

}
