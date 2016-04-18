package com.github.goraxe.rss_reader.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

/**
 * Created by goraxe on 2016-04-06.
 * Boo Yar
 */
@Service
public class HTTPClientService {

    HttpClientBuilder clientBuilder;

    HttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }
}
