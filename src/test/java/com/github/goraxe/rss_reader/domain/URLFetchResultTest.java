package com.github.goraxe.rss_reader.domain;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Created by goraxe on 2016-04-06.
 * Boo Yar
 */
public class URLFetchResultTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void createURLFetch() {
        URLFetchResult fetch = new URLFetchResult();
        assertThat(fetch, instanceOf(URLFetchResult.class));
    }
}