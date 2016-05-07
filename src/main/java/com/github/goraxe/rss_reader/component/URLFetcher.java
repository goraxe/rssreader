package com.github.goraxe.rss_reader.component;

import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.domain.URLFetchResult;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import com.github.goraxe.rss_reader.repositories.mongo.URLFetchRepository;
import com.google.common.io.Files;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by goraxe on 2016-04-12.
 * Boo Yar
 */
@Component
@Scope("prototype")
public class URLFetcher implements Callable<URLFetchResult> {

    static private final Logger log = LoggerFactory.getLogger(URLFetcher.class);
    private final URLFetchRepository fetchRepository;
    private final URI uri;
    private final ContentStorage contentStorage;
    private HttpClient client;
    private HttpUriRequest method;
    private Date lastModified;
    private String etag;

    /*
    public URLFetcher(RSSFeedRepository feeds, URLFetchRepository fetchRepository, RSSFeed rssFeed, ContentStorage contentStorage) {
        this.feeds = feeds;
        this.fetchRepository = fetchRepository;
        this.rssFeed = rssFeed;
        this.contentStorage = contentStorage;
        this.client = HttpClients.createMinimal();
    }
*/

    public URLFetcher(URLFetchRepository urlFetchRepository, ContentStorage contentStorage, URI uri) {
        this.fetchRepository = urlFetchRepository;
        this.contentStorage = contentStorage;
        this.uri = uri;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }

    public URLFetchResult fetch() throws IOException {
        try {
            log.debug("retrieving: " + uri);
            GetResult getResult = new GetResult().invoke();
            URLFetchResult result = getResult.getResult();
            HttpResponse  response = getResult.getResponse();

            switch (result.getStatusCode()) {
                case 200: // success
                    contentStorage.saveContent(response.getEntity().getContent());
                    break;
                case 404: // not found
                case 301: // moved perm
                case 302: // moved temp
                case 304: // not modifed
                    break;
                default:
                    log.warn("do not know how to handle code: " + result.getStatusCode());
            }
            return result;
        }catch (UnknownHostException e) {
            log.warn("url does not resolve: " + uri);
        }
        catch (ConnectTimeoutException | HttpHostConnectException e) {
            log.warn("could not connect to host: " + uri);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HttpUriRequest getHttpUriRequest() {
        if (method == null ) {
            method = new HttpGet(uri);
            if (etag != null) {
                method.addHeader("If-None-Match", etag);
            }
            if (lastModified != null) {
                method.addHeader("If-Modified-Since", DateUtils.formatDate(lastModified, DateUtils.PATTERN_RFC1123));
            }
        }
        return method;
    }

/*
    private void saveHeaders(HttpResponse response) {
        if (response.containsHeader("last-modified")) {
            rssFeed.setLast_modified(DateUtils.parseDate(response.getFirstHeader("last-modified").getValue()));
        }

        if (response.containsHeader("etag")) {
            rssFeed.setEtag(response.getFirstHeader("etag").getValue());
        }
    }
*/

    @Override
    public URLFetchResult call() {

        try {
            URLFetchResult result = fetch();
            if (result != null) {
                fetchRepository.save(result);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    private class GetResult {

        private final HttpUriRequest method;
        private final Date now;
        private HttpResponse response;
        private URLFetchResult result;

        public GetResult() {

            this.method = getHttpUriRequest();
            this.now = new Date();
        }

        public HttpResponse getResponse() {
            return response;
        }

        public URLFetchResult getResult() {
            return result;
        }

        public GetResult invoke() throws IOException {
            response = client.execute(method);
            result = getUrlFetch(now, response);
            return this;
        }

/*
        private void saveHeaders(HttpResponse response) {
            if (response.containsHeader("last-modified")) {
                rssFeed.setLast_modified(DateUtils.parseDate(response.getFirstHeader("last-modified").getValue()));
            }

            if (response.containsHeader("etag")) {
                rssFeed.setEtag(response.getFirstHeader("etag").getValue());
            }
        }
*/

        private URLFetchResult getUrlFetch(Date now, HttpResponse response) {
            StatusLine status = response.getStatusLine();

            log.debug("result: " + status.getStatusCode() + " : " + status.getReasonPhrase());
       //     saveHeaders(response);
            Date modfied = null;
            if (response.containsHeader("last-modified")) {
                modfied = DateUtils.parseDate(response.getFirstHeader("last-modified").getValue());
            }
            String etag = null;
            if (response.containsHeader("etag")) {
                etag = response.getFirstHeader("etag").getValue();
            }
            return new URLFetchResult(now, modfied, etag, uri, status.getStatusCode());
        }
    }

    /*
        HttpHead head = new HttpHead(url);
        if (etag != null) {
            head.addHeader("If-None-Match", etag);
        }
        if (last_modified != null) {
            head.addHeader("If-Modified-Since", last_modified.toString());
        }

        CloseableHttpResponse response = client.execute(head);

        StatusLine status = response.getStatusLine();
        System.out.println("HEAD result: " + status.getStatusCode() + " : " + status.getReasonPhrase() + " content: " );
        if (response.getEntity() != null ) {
            System.out.println("head content: " + response.getEntity().getContent());
        }
        if (status.getStatusCode() == 301 && !response.getFirstHeader("Location").getValue().isEmpty()) {
            URI uri = null;
            try {
                uri = new URI(response.getFirstHeader("Location").getValue());
                if (  uri != null && uri.getHost() != null) {
                    System.out.println("updating url to location: " + response.getFirstHeader("Location").getValue());
                    url = response.getFirstHeader("Location").getValue();
                }
            } catch (URISyntaxException e) {
                System.out.println("could not understand : " + uri);
            }

        }
        */





}
