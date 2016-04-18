package com.github.goraxe.rss_reader.repositories.mongo;

import com.github.goraxe.rss_reader.domain.RSSFeed;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RSSFeedRepository extends MongoRepository<RSSFeed, String> {}
