package com.tryrosberry.ololostfilm.ui.models;

import com.tryrosberry.ololostfilm.logic.api.HtmlParser;

import org.htmlcleaner.TagNode;

import java.io.Serializable;
import java.util.ArrayList;

public class Serial implements Serializable {

    public String name;
    public String subName;
    public String url;
    public SerialDetails details;
    public ArrayList<Season> seasons = new ArrayList<Season>();
    public int seasonCounter = 0;

    public Serial(){}

    public Serial(TagNode node){
        url = node.getAttributeByName("href");
        name = HtmlParser.getContent(node);
        subName = HtmlParser.getContent(HtmlParser.getLinksByClass(node,"span").get(0));
    }

    public void setSeasons(ArrayList<Season> seasons){
        this.seasons = seasons;
    }

    public void setDetails(SerialDetails details){
        this.details = details;
    }
}
