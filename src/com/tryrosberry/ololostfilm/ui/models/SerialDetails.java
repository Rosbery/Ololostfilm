package com.tryrosberry.ololostfilm.ui.models;

import com.tryrosberry.ololostfilm.logic.api.HtmlParser;

import org.htmlcleaner.TagNode;

import java.io.Serializable;

public class SerialDetails implements Serializable {

    public enum detType{TEXT,PICTURE,UNKNOWN}

    public String imageLink;
    public String description;

    public SerialDetails(){}

    public SerialDetails(String imageLink, String description){
        this.imageLink = imageLink;
        this.description = description;
    }

}
