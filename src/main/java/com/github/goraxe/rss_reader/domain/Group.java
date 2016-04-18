package com.github.goraxe.rss_reader.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goraxe on 2016-03-28.
 */

/*
@Entity
@Table(name = "Groups")
*/
public class Group {
    @Id
    private String name;
    private String title;

/*
    @ManyToMany()
    @JoinTable(name = "group_feed", joinColumns = @JoinColumn(name = "group_name", referencedColumnName = "name"))
*/
@DBRef(lazy = true)
private List<RSSFeed> feeds;

//    private ArrayList<RSSFeed> feeds;


    public Group(String name, String title) {
        this.name = name;
        this.title = title;
        feeds = new ArrayList<RSSFeed>();
    }


    public Group(String name, String title, ArrayList<RSSFeed> feeds) {
        this.name = name;
        this.title = title;
        this.feeds = feeds;
        //feeds = new ArrayList<RSSFeed>();
    }

    public Group() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addFeed(RSSFeed feed) {
        feeds.add(feed);
    }

    public List<RSSFeed> getFeeds (){
        return feeds;
    }

    public void setFeeds(List<RSSFeed> feeds) {
        this.feeds = feeds;
    }
}
