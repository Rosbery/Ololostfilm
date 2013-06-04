package com.tryrosberry.ololostfilm.logic.api;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
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

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

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

    /*public static ArrayList<Event> parseEvents(Document doc){
        ArrayList<Event> events = null;

        if(doc != null){
            events = new ArrayList<Event>(0);
            NodeList nl = doc.getElementsByTagName("Eventlist"); //parent nod
            NodeList eventsList = nl.item(0).getChildNodes();

            if(nl != null && eventsList.getLength() > 0){
                for (int i = 0; i < eventsList.getLength(); i++) {
                    Element e = (Element) eventsList.item(i);
                    events.add(new Event(e));
                }
            }
        } else events = new ArrayList<Event>(0);

        return events;
    }

    public static List<State> parseStates(Document doc){
        List<State> states = null;

        if(doc != null){
            states = new ArrayList<State>(0);
            NodeList nl = doc.getElementsByTagName("StatesList"); //parent nod
            NodeList stateList = nl.item(0).getChildNodes();

            if(nl != null && stateList.getLength() > 0){
                for (int i = 0; i < stateList.getLength(); i++) {
                    if (stateList.item(i) instanceof Element){
                        Element e = (Element) stateList.item(i);
                        states.add(new State(e));
                    }
                }
            }
        }

        return states;
    }

    public static List<Genre> parseGenres(Document doc){
        List<Genre> genres = null;

        if(doc != null){
            genres = new ArrayList<Genre>(0);
            NodeList nl = doc.getElementsByTagName("GenresList"); //parent nod
            NodeList genreList = nl.item(0).getChildNodes();

            if(nl != null && genreList.getLength() > 0){
                for (int i = 0; i < genreList.getLength(); i++) {
                    if (genreList.item(i) instanceof Element){
                        Element e = (Element) genreList.item(i);
                        genres.add(new Genre(e));
                    }
                }
            }
        }

        return genres;
    }*/

    public static Document parseResponse(String xml){
        return FeedParser.getDomElement(xml);
    }

}
