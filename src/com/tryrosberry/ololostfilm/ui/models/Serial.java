package com.tryrosberry.ololostfilm.ui.models;

import com.tryrosberry.ololostfilm.logic.api.HtmlParser;

import org.htmlcleaner.TagNode;

import java.io.Serializable;

/**
 * Created by extazy on 04.06.13.
 */
public class Serial implements Serializable {

    public String name;
    public String subName;
    public String url;


    public Serial(){}

    public Serial(TagNode node){
        url = node.getAttributeByName("href");
        name = HtmlParser.getContent(node);
        subName = HtmlParser.getContent(HtmlParser.getLinksByClass(node,"span").get(0));
    }

}
