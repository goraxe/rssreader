package com.github.goraxe.rss_reader;

import com.github.goraxe.rss_reader.domain.Group;
import com.github.goraxe.rss_reader.domain.RSSFeed;
import com.github.goraxe.rss_reader.repositories.mongo.RSSFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * Created by goraxe on 2016-03-26.
 */
@Service
public class OPMLHandler extends DefaultHandler {


    /*
    public OPMLHandler() {
    }
    */
    @Autowired
    RSSFeedRepository feeds;

    private HashMap<String,Group> groups;
    private Group curGroup;
    //private HashMap tags;
    @Override
    public void startDocument() throws SAXException {
        //    tags = new HashMap();
        groups = new HashMap<String, Group>();
    }

    public void endDocument() throws SAXException {
         /*
            for(Map.Entry<String, String> entry : tags.values()) {

            }
          */
    }

    @Override
    public void startElement( String namespaceURI,
                              String localName,
                              String qName,
                              Attributes attrs)
            throws SAXException {
        System.out.println("namespace: " + namespaceURI + ", localName: " + localName + " qName: " + qName );
        for(Integer i= 0; i< attrs.getLength(); i++) {
            String name = attrs.getQName(i);
            String value = attrs.getValue(i);
            System.out.println("\t " + name + " = " + value);
        }

        if ( qName.equalsIgnoreCase("outline") ) {
            if (attrs.getValue("type") == null) {
                Group group = new Group(attrs.getValue("title"),attrs.getValue("text"));
                curGroup = group;
                groups.put(group.getName(),group);
            } else if (attrs.getValue("type").equalsIgnoreCase("rss")) {
                RSSFeed rssFeed = new RSSFeed(attrs.getValue("title"), attrs.getValue("text"), attrs.getValue("xmlUrl"));
                feeds.save(rssFeed);
                curGroup.addFeed(rssFeed);
            }
        }
    }

    public HashMap<String, Group> getGroups() {
        return groups;
    }
}
