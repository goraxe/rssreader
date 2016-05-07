package com.github.goraxe.rss_reader.component;

import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.domain.URLFetchResult;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import com.github.goraxe.rss_reader.repositories.mongo.URLFetchRepository;
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder;
import com.mmnaseri.utils.spring.data.dsl.factory.Start;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

/**
 * Created by goraxe on 2016-04-13.
 * Boo Yar
 */
public class URLFetcherTest {

    @Mock
    private HttpClient mockHttpClient;
    private RSSFeedRepository   rssFeedRepository;
    private URLFetchRepository urlFetchRepository;
    private ContentStorage contentStorage;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Start builder = RepositoryFactoryBuilder.builder();
        rssFeedRepository = builder.mock(RSSFeedRepository.class);
        urlFetchRepository = builder.mock(URLFetchRepository.class);

        folder.delete();
        try {
            folder.create();
            contentStorage = new ContentStorage();
            contentStorage.setRepoDir(folder.getRoot().getPath());
        } catch (IOException e) {
            fail("could not create temp dir store");
            e.printStackTrace();
        }

        thrown = ExpectedException.none();
    }

    @Test
    public void fetchSuccess() throws Exception {
        // Given
        String url = "http://www.google.com";
        String name = "test";
        String title = "test title";

        URI uri = new URI(url);

        URLFetcher test = getTestUrlFetcher(uri, name, title);
        HttpUriRequest testMethod = test.getHttpUriRequest();
        HttpResponse response = createMockHttpResponse(200, "we succeed");


        Mockito.when(mockHttpClient.execute(testMethod)).thenReturn(response);
        test.setClient(mockHttpClient);

        // Then
        URLFetchResult testResult = test.call();

        // check we get a success code
        assertEquals(testResult.getStatusCode(), 200);

        // check result is stored in repository
        URLFetchResult repoResult = urlFetchRepository.findOne(testResult.getId());
        assertEquals(testResult, repoResult);
     }

    @Test
    public void fetchSuccessEtag() throws Exception {
        // Given
        String url = "http://www.google.com";
        String name = "test";
        String title = "test title";
        String etag  = "blah-blah";

        URI uri = new URI(url);

        URLFetcher test = getTestUrlFetcher(uri, name, title, etag);
        HttpUriRequest testMethod = test.getHttpUriRequest();

        Map<String,String> testHeaders = new HashMap<>();
        testHeaders.put("etag", etag);
        HttpResponse response = createMockHttpResponse(200, "we succeed", testHeaders);


        Mockito.when(mockHttpClient.execute(testMethod)).thenReturn(response);
        test.setClient(mockHttpClient);

        // Then
        URLFetchResult testResult = test.call();

        // check we get a success code
        assertEquals(testResult.getStatusCode(), 200);

        // check result is stored in repository
        URLFetchResult repoResult = urlFetchRepository.findOne(testResult.getId());
        assertEquals(testResult, repoResult);
        assertEquals(etag, repoResult.getEtag());
    }

    @Test
    public void fetchSuccessResponseEtag() throws Exception {
        // Given
        String url = "http://www.google.com";
        String name = "test";
        String title = "test title";
        String etag  = "blah-blah-blah";

        URI uri = new URI(url);

        URLFetcher test = getTestUrlFetcher(uri, name, title);
        HttpUriRequest testMethod = test.getHttpUriRequest();
        HttpResponse response = createMockHttpResponse(200, "we succeed");
        response.addHeader("etag", etag);

        Mockito.when(mockHttpClient.execute(testMethod)).thenReturn(response);
        test.setClient(mockHttpClient);

        // Then
        URLFetchResult testResult = test.call();

        // check we get a success code
        assertEquals(testResult.getStatusCode(), 200);

        // check result is stored in repository
        URLFetchResult repoResult = urlFetchRepository.findOne(testResult.getId());
        assertEquals(testResult, repoResult);
        assertEquals(etag, testResult.getEtag());
    }

    @Test
    public void fetchSuccessResponseLastModified() throws Exception {
        // Given
        String url = "http://www.google.com";
        String name = "test";
        String title = "test title";
        Date   modified = new Date();

        URI uri = new URI(url);

        URLFetcher test = getTestUrlFetcher(uri, name, title);
        HttpUriRequest testMethod = test.getHttpUriRequest();
        HttpResponse response = createMockHttpResponse(200, "we succeed");
        response.addHeader("last-modified", DateUtils.formatDate(modified, DateUtils.PATTERN_RFC1123));

        Mockito.when(mockHttpClient.execute(testMethod)).thenReturn(response);
        test.setClient(mockHttpClient);

        // Then
        URLFetchResult testResult = test.call();

        // check we get a success code
        assertEquals(testResult.getStatusCode(), 200);

        // check result is stored in repository
        URLFetchResult repoResult = urlFetchRepository.findOne(testResult.getId());
        assertEquals(testResult, repoResult);
        assertEquals(DateUtils.formatDate(modified, DateUtils.PATTERN_RFC1123), DateUtils.formatDate(testResult.getLast_modified(), DateUtils.PATTERN_RFC1123));
    }



    @Test
    public void fetchSuccessLastModified() throws Exception {
        // Given
        String url = "http://www.google.com";
        String name = "test";
        String title = "test title";
        Date   modified = new Date();

        URI uri = new URI(url);

        URLFetcher test = getTestUrlFetcher(uri, name, title, modified);
        HttpUriRequest testMethod = test.getHttpUriRequest();
        Map<String, String> testHeaders = new HashMap<String, String>();
        testHeaders.put("last-modified", DateUtils.formatDate(modified, DateUtils.PATTERN_RFC1123));
        HttpResponse response = createMockHttpResponse(200, "we succeed", testHeaders);


        Mockito.when(mockHttpClient.execute(testMethod)).thenReturn(response);
        test.setClient(mockHttpClient);

        // Then
        URLFetchResult testResult = test.call();

        // check we get a success code
        assertEquals(testResult.getStatusCode(), 200);

        // check result is stored in repository
        URLFetchResult repoResult = urlFetchRepository.findOne(testResult.getId());
        assertEquals(testResult, repoResult);
        assertEquals(DateUtils.formatDate(modified, DateUtils.PATTERN_RFC1123), DateUtils.formatDate(testResult.getLast_modified(), DateUtils.PATTERN_RFC1123));
    }



    @Test
    public void fetchHostNotFound() throws Exception {
        // Given
        String url = "http://www.google.com";
        String name = "test";
        String title = "test title";

        URI uri = new URI(url);

        URLFetcher test = getTestUrlFetcher(uri, name, title);
        HttpUriRequest testMethod = test.getHttpUriRequest();

        Mockito.when(mockHttpClient.execute(testMethod)).thenThrow(UnknownHostException.class);
        test.setClient(mockHttpClient);

        // Then
        thrown.expect(UnknownHostException.class);
        URLFetchResult testResult = test.call();
    }


    private URLFetcher getTestUrlFetcher(URI uri, String name, String title) {
        return getTestUrlFetcher(uri, name, title, null, null);
    }

    private URLFetcher getTestUrlFetcher(URI uri, String name, String title, String etag) {
        return getTestUrlFetcher(uri, name, title, etag, null);
    }

    private URLFetcher getTestUrlFetcher(URI uri, String name, String title, Date modified) {
        return getTestUrlFetcher(uri, name, title, null, modified);
    }


    private URLFetcher getTestUrlFetcher(URI uri, String name, String title, String etag, Date modified) {

        /*
            RSSFeed testFeed = new RSSFeed(name,title, url);
            assertEquals(name, testFeed.getName());
            assertEquals(title, testFeed.getTitle());
            assertEquals(url, testFeed.getUrl());

            if (etag != null) {
                testFeed.setEtag(etag);
            }

            if (modified != null) {
                testFeed.setLast_modified(modified);
            }
        */

        URLFetcher urlFetcher = new URLFetcher(urlFetchRepository, contentStorage, uri);
        if (etag != null ) {
            urlFetcher.setEtag(etag);
        }
        if (modified != null) {
            urlFetcher.setLastModified(modified);
        }
        return urlFetcher;
    }


    private HttpResponse createMockHttpResponse(int status, String content) {
        return createMockHttpResponse(status, content, null);
    }

    private HttpResponse createMockHttpResponse(int status, String content, Map<String,String> headers) {
        BasicStatusLine statusLine = new BasicStatusLine(new ProtocolVersion("http", 1, 1), status, content);
        BasicHttpContext context = new BasicHttpContext();
        HttpResponse response = DefaultHttpResponseFactory.INSTANCE.newHttpResponse(statusLine, context );

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                response.addHeader(entry.getKey(), entry.getValue());
            }
        }

        HttpEntity entity = new HttpEntity() {
            @Override
            public boolean isRepeatable() {
                return false;
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public long getContentLength() {
                return 0;
            }

            @Override
            public Header getContentType() {
                return null;
            }

            @Override
            public Header getContentEncoding() {
                return null;
            }

            @Override
            public InputStream getContent() throws IOException, UnsupportedOperationException {
                String testString = "this is a set of bytes";
                byte[] bytes = testString.getBytes();
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public void writeTo(OutputStream outputStream) throws IOException {

            }

            @Override
            public boolean isStreaming() {
                return false;
            }

            @Override
            public void consumeContent() throws IOException {

            }
        };
        response.setEntity(entity);
        return response;
    }
}