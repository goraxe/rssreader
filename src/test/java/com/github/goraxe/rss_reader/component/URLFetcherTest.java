package com.github.goraxe.rss_reader.component;

import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.domain.URLFetchResult;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import com.github.goraxe.rss_reader.repositories.mongo.URLFetchRepository;
import com.mmnaseri.utils.spring.data.dsl.factory.RepositoryFactoryBuilder;
import com.mmnaseri.utils.spring.data.dsl.factory.Start;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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


    @org.junit.Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Start builder = RepositoryFactoryBuilder.builder();
        rssFeedRepository = builder.mock(RSSFeedRepository.class);

        urlFetchRepository = builder.mock(URLFetchRepository.class);
    }

    @Test
    public void fetch() throws Exception {
        RSSFeed testFeed = new RSSFeed("test","test title", "http://www.google.com");
        assertEquals("test", testFeed.getName());
        assertEquals("test title", testFeed.getTitle());
        assertEquals("http://www.google.com", testFeed.getUrl());

        URLFetcher test = new URLFetcher(rssFeedRepository,urlFetchRepository, testFeed );

        // head returns 301
        String url = "http://www.google.com";
        HttpUriRequest testMethod = test.getHttpUriRequest();

        BasicStatusLine statusLine = new BasicStatusLine(new ProtocolVersion("http", 1, 1), 200, "we succeed");
        BasicHttpContext context = new BasicHttpContext();
        HttpResponse response = DefaultHttpResponseFactory.INSTANCE.newHttpResponse(statusLine, context );
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

        HttpEntityWrapper httpEntityWrapper  = new HttpEntityWrapper(entity);

        Mockito.when(mockHttpClient.execute(testMethod)).thenReturn(response);

        test.setClient(mockHttpClient);
        URLFetchResult testResult = test.call();

        // check we get a success code
        assertEquals(testResult.getStatusCode(), 200);

        // check result is stored in repository
        URLFetchResult repoResult = urlFetchRepository.findOne(testResult.getId());
        assertEquals(testResult, repoResult);
     }
}