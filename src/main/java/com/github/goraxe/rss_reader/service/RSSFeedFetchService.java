package com.github.goraxe.rss_reader.service;

import com.github.goraxe.rss_reader.component.URLFetcher;
import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import com.github.goraxe.rss_reader.repositories.mongo.URLFetchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

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
    private URLFetchRepository urls;
    @Autowired
    private
    ThreadPoolTaskExecutor taskExecutor;

    @Scheduled(fixedRate = 60000)
    public void fetchFeeds() {

        for (RSSFeed rssFeed : feeds.findAll()) {
            System.out.println("fetching feed: " + rssFeed.getName() + " url: " + rssFeed.getUrl());

            taskExecutor.submit(new URLFetcher(feeds, urls, rssFeed));

        }

    }
}
