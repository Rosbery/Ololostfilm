package com.tryrosberry.ololostfilm.logic.api;

import android.util.Log;

import com.tryrosberry.ololostfilm.ui.models.RssItem;

import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FeedParser {

    public static Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            /*InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            is.setEncoding(HTTP.UTF_8);
            doc = db.parse(is);*/


            ByteArrayInputStream encXML = new ByteArrayInputStream(xml.getBytes(HTTP.UTF_16));
            doc = db.parse(encXML);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (Exception e){
            Log.e("Error: ", e.getMessage());
            return null;
        }
        return doc;
    }

    public static String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return FeedParser.getElementValue(n.item(0));
    }

    public static final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    return child.getNodeValue();
                }
            }
        }
        return "";
    }

    public static ArrayList<String> getValues(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        ArrayList<String> nodeStringList = new ArrayList<String>(n.getLength());
        for(int i = 0;i < n.getLength();i++){
            nodeStringList.add(FeedParser.getElementValue(n.item(i)));
        }
        return nodeStringList;
    }

    public static ArrayList<RssItem> parseRss(Document doc){
        ArrayList<RssItem> rssFeed = null;

        if(doc != null){
            rssFeed = new ArrayList<RssItem>(0);
            NodeList rssList = doc.getElementsByTagName("item"); //parent nod

            if(rssList != null && rssList.getLength() > 0){
                for (int i = 0; i < rssList.getLength(); i++) {
                    Element e = (Element) rssList.item(i);
                    rssFeed.add(new RssItem(e));
                }
            }
        } else rssFeed = new ArrayList<RssItem>(0);

        return rssFeed;
    }

    public static Document parseResponse(String xml){
        return FeedParser.getDomElement(xml);
    }

}
