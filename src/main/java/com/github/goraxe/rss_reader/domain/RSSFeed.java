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
    private String etag;
    private Date last_modified;

    public RSSFeed(String name, String title, String url) {
        this.name = name;
        this.title = title;
        this.url = url;
    }

    public RSSFeed() {
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

}
