package com.github.goraxe.rss_reader.domain;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

//mock creation

/**
 * Created by goraxe on 2016-04-06.
 * Boo Yar
 */
public class RSSFeedTest {


    @Mock
    private CloseableHttpClient mockHttpClient;

    @org.junit.Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void fetch() throws Exception {



    }

}