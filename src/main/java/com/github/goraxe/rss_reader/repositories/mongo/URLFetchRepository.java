package com.github.goraxe.rss_reader.repositories.mongo;

import com.github.goraxe.rss_reader.domain.URLFetchResult;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by goraxe on 2016-04-07.
 * Boo Yar
 */
public interface URLFetchRepository extends MongoRepository<URLFetchResult, String> {
}
