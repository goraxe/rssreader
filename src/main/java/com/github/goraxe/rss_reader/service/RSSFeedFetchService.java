package com.github.goraxe.rss_reader.service;

import com.github.goraxe.rss_reader.component.ContentStorage;
import com.github.goraxe.rss_reader.component.URLFetcher;
import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import com.github.goraxe.rss_reader.repositories.mongo.URLFetchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by goraxe on 2016-04-01.
 * Boo Yar
 */
@Service
public class RSSFeedFetchService {


    @Autowired
    private
    RSSFeedRepository feeds;
    @Autowired
    private URLFetchRepository urlFetchRepository;
    @Autowired
    private
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private
    ContentStorage contentStorage;

    @Scheduled(fixedRate = 60000)
    public void fetchFeeds() {

        for (RSSFeed rssFeed : feeds.findAll()) {
            System.out.println("fetching feed: " + rssFeed.getName() + " url: " + rssFeed.getUrl());

            try {
                URI uri = new URI(rssFeed.getUrl());
                taskExecutor.submit(new URLFetcher(urlFetchRepository, contentStorage, uri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
