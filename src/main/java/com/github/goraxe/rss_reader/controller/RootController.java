package com.github.goraxe.rss_reader.controller;

import com.github.goraxe.rss_reader.OPMLHandler;
import com.github.goraxe.rss_reader.domain.Group;
import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.repositories.mongo.GroupMongoRepository;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by goraxe on 2016-04-13.
 * Boo Yar
 */
@Controller
public class RootController {
    @Autowired
    GroupMongoRepository groups;
    @Autowired
    RSSFeedRepository feeds;
    @Autowired
    OPMLHandler opmlHandler;

    static final Logger log = Logger.getLogger(RootController.class.getName());


    @RequestMapping(method = RequestMethod.POST, value = "/opml")
    @ResponseBody
    public String handleUploadOpml(@RequestParam("file") MultipartFile file) {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        //OPMLHandler opmlHandler = new OPMLHandler();
        try {

            SAXParser saxParser = spf.newSAXParser();
            saxParser.parse(file.getInputStream(), opmlHandler);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String,Group> entry: opmlHandler.getGroups().entrySet()) {
            groups.save(entry.getValue());
            ///groups.put(entry.getKey(), entry.getValue());
        }
        return "";
    }

    @RequestMapping("/groups")
    @ResponseBody
    public Set<String> getGroups() {


        Set<String> groupNames = new HashSet<String>();

        for (Group group: groups.findAll()) {
            groupNames.add(group.getName());
        }
        return groupNames;

    }

    @RequestMapping("/group/{name}")
    @ResponseBody
    public ResponseEntity<Group> getGroup(@PathVariable("name") String name) {
        log.info("got request for group: " + name);
        if (groups == null) {
            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
        }
        Group group  =  groups.findByName(name);
        if (group == null) {
            return new ResponseEntity<Group>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(group);
    }

    @RequestMapping("/feed/{id}")
    @ResponseBody
    public ResponseEntity<RSSFeed> getFeed(@PathVariable("id") String id) {
        RSSFeed feed = feeds.findOne(id);

        if (feed == null) {
            return new ResponseEntity<RSSFeed>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(feed);
    }

    @RequestMapping("/feed/{id}/fetch")
    @ResponseBody
    public ResponseEntity fetchFeed(@PathVariable("id") String id) {
        RSSFeed feed = feeds.findOne(id);

        if (feed == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        /*SyndFeed content =  feed.fetch();
        List<SyndEntry> entries = content.getEntries();
        */
        //     System.out.println(.toString());
        //   entries.get(0)
        /*
        for (SyndEntry entry : entries) {

        }
        */
        return new ResponseEntity(HttpStatus.OK);
    }

}
