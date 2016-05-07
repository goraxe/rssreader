package com.github.goraxe.rss_reader.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.net.URI;
import java.util.Date;

/**
 * Created by goraxe on 2016-04-06.
 * Boo Yar
 */
@Document
public class URLFetchResult {

    @Id
    private String id;
    private Date fetchdate;
    private Date last_modified;
    private URI uri;
    private String etag;
    private int statusCode;

/*
    public URLFetchResult(Date fetchdate, Date last_modified, String etag, RSSFeed url, int statusCode) {
        this.fetchdate =  fetchdate;
        this.last_modified = last_modified;
        this.etag = etag;
        this.url = url;
        this.statusCode = statusCode;
    }
*/

    public URLFetchResult() {

    }

    public URLFetchResult(Date fetchdate, Date last_modified, String etag, URI uri, int statusCode) {
        this.fetchdate =  fetchdate;
        this.last_modified = last_modified;
        this.etag = etag;
        this.uri = uri;
        this.statusCode = statusCode;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public String getEtag() {
        return etag;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getId() {
        return id;
    }
}
