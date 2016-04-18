package com.github.goraxe.rss_reader.repositories.mongo;

import com.github.goraxe.rss_reader.domain.Group;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by goraxe on 2016-03-28.
 */

public interface GroupMongoRepository extends MongoRepository<Group, String> {

    Group findByName(String name);
}
