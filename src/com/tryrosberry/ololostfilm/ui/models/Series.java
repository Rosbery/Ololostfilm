package com.tryrosberry.ololostfilm.ui.models;

import org.htmlcleaner.TagNode;

/**
 * Created by typhus on 07.06.13.
 */
public class Series {
    public String name;
    public String subName;
    public String url;
    public Series(){}

    public Series(TagNode node){
//        url = node.getAttributeByName("href");
//        name = HtmlParser.getContent(node);
//        subName = HtmlParser.getContent(HtmlParser.getLinksByClass(node,"span").get(0));
    }
}
