package com.tryrosberry.ololostfilm.ui.models;

import org.htmlcleaner.TagNode;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by typhus on 07.06.13.
 */
public class Sesson implements Serializable {

    public String name;
    public String subName;
    public String url;
    public ArrayList<Series> mSeries;
    public Sesson(){}

    public Sesson(TagNode node){
//        url = node.getAttributeByName("href");
//        name = HtmlParser.getContent(node);
//        subName = HtmlParser.getContent(HtmlParser.getLinksByClass(node,"span").get(0));
    }
}
