package com.tryrosberry.ololostfilm.logic.api;

import com.tryrosberry.ololostfilm.ui.models.Serial;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.List;

public class HtmlParser {

    public HtmlParser(){}

    public static ArrayList<Serial> parseSerials(String response){
        return getSerials(parse(response, "a", "class", "bb_a"));
    }

    private static List<TagNode> parse(String response, String nodeName, String tagvalue, String CSSClassname) {
        TagNode rootNode = getRootNode(response);
        return getLinksByClass(rootNode, nodeName, tagvalue, CSSClassname);
    }

    private static ArrayList<Serial> getSerials(List<TagNode> nodes) {
        ArrayList<Serial> serials = new ArrayList<Serial>();
        for (TagNode node : nodes){
            serials.add(new Serial(node));
        }
        return serials;
    }

    private static TagNode getRootNode(String response) {
        HtmlCleaner cleaner = null;
        try {
            cleaner = new HtmlCleaner();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cleaner.clean(response);
    }

    public static String getContent(TagNode node) {
        StringBuilder result = new StringBuilder();
        List<Object> items = node.getAllChildren();
        for (Object item : items) {
            if (item instanceof ContentNode) {
                result.append(((ContentNode) item).getContent());
            }
        }
        return result.toString();
    }

    private static List<TagNode> getLinksByClass(TagNode rootNode, String nodeName, String TagVal, String CSSClassname){
        List<TagNode> linkList = new ArrayList<TagNode>();

        //Выбираем все ссылки
        TagNode linkElements[] = rootNode.getElementsByName(nodeName, true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            //получаем атрибут по имени
            String classType = linkElements[i].getAttributeByName(TagVal);
            //если атрибут есть и он эквивалентен искомому, то добавляем в список
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }

    public static List<TagNode> getLinksByClass(TagNode rootNode, String nodeName){
        List<TagNode> linkList = new ArrayList<TagNode>();
        TagNode linkElements[] = rootNode.getElementsByName(nodeName, true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            linkList.add(linkElements[i]);
        }
        return linkList;
    }

}