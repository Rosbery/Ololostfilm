package com.tryrosberry.ololostfilm.ui.models;

import com.tryrosberry.ololostfilm.logic.api.FeedParser;

import org.w3c.dom.Element;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RssItem implements Serializable {

    public String title;
    public String description;
    public String pubDate;
    public String link;

    public RssItem(){}

    public RssItem(Element e){
        title = FeedParser.getValue(e, "title");
        description = FeedParser.getValue(e, "description");
        pubDate = new SimpleDateFormat("d MMM - hh:mm:ss").format(new Date(FeedParser.getValue(e, "pubDate")));
        link = FeedParser.getValue(e, "link");
    }

}
