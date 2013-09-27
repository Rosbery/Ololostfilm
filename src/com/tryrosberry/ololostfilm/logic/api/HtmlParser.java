package com.tryrosberry.ololostfilm.logic.api;

import com.tryrosberry.ololostfilm.logic.storage.ConstantStorage;
import com.tryrosberry.ololostfilm.ui.models.NewsDetails;
import com.tryrosberry.ololostfilm.ui.models.NewsFeedItem;
import com.tryrosberry.ololostfilm.ui.models.Season;
import com.tryrosberry.ololostfilm.ui.models.Serial;
import com.tryrosberry.ololostfilm.ui.models.SerialDetails;
import com.tryrosberry.ololostfilm.ui.models.Series;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.List;

public class HtmlParser {

    public HtmlParser(){}

    public static ArrayList<Serial> parseSerials(String response){
        TagNode rootNode = getRootNode(response);
        rootNode = getLinksByClass(rootNode, "div", "class", "mid").get(0);
        return getSerials(getLinksByClass(rootNode, "a", "class", "bb_a"));
    }

    public static ArrayList<NewsFeedItem> parseNews(String response){
        return getNews(parse(response, "div", "class", "content_body"));
    }

    public static ArrayList<NewsDetails> parseNewsDetails(String response){
        return getNewsDetails(parse(response, "div", "class", "content_body"));
    }

    public static String parseNewsDetailsForWebView(String response){
        List<TagNode> nodes = parse(response, "div", "class", "content_body");
        if(nodes != null && nodes.size() > 0) {
            TagNode root = nodes.get(0);
            rebuildRootNode(root);
            return getHtmlContent(root);
        } else return null;
    }

    public static List<TagNode> parseSerialDetails(String response){
        return getLinksByClass(parse(response, "div", "class", "mid").get(0), "div", false); // get all <div> in <div class="mid"> //needed 0-1 items
    }

    public static String parseSerialDetailsForWebView(String response){
        List<TagNode> nodes = parse(response, "div", "class", "mid");
        if(nodes != null && nodes.size() > 0) {
            TagNode root = nodes.get(0);
            rebuildRootNode(root);
            return getHtmlContent(root);
        } else return null;
    }

    public static void rebuildRootNode(TagNode root){
        List<TagNode> imgs = HtmlParser.getLinksByClass(root,"img");
        for(TagNode img : imgs){
            String oldAttr = img.getAttributeByName("src");
            if(oldAttr.startsWith("/")){
                String newAttr = ConstantStorage.BASE_URL + oldAttr;
                img.removeAttribute("src");
                img.addAttribute("src",newAttr);
            }
            if(img.getAttributeByName("style") != null){
                oldAttr = "width: 100%";
                img.removeAttribute("style");
                img.addAttribute("style",oldAttr);
            }
        }
        List<TagNode> as = HtmlParser.getLinksByClass(root,"a");
        for(TagNode a : as){
            String oldAttr = a.getAttributeByName("href");
            if(oldAttr.startsWith("/")){
                String newAttr = ConstantStorage.BASE_URL + oldAttr;
                a.removeAttribute("href");
                a.addAttribute("href",newAttr);
            }
        }
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

    private static ArrayList<NewsFeedItem> getNews(List<TagNode> nodes) {
        ArrayList<NewsFeedItem> newsFeed = new ArrayList<NewsFeedItem>();
        if(nodes.size() >= 1){
            TagNode newsNodes = nodes.get(0);
            List<TagNode> newsTagNodes = newsNodes.getChildTagList();
            List<TagNode> newsTagLinks = getLinksByClass(newsNodes, "a", "class", "a_full_news");

            int hcounter = 0;
            for (int i = 0; i < newsTagNodes.size(); i++){
                TagNode feedTagNode = newsTagNodes.get(i);
                if(feedTagNode.getName().equals("h1")){
                    NewsFeedItem feedItem = new NewsFeedItem();
                    feedItem.title = getContent(feedTagNode);
                    feedItem.image = ConstantStorage.BASE_URL + HtmlParser.getLinksByClass(newsTagNodes.get(i+1),"img").get(0).getAttributeByName("src");
                    int what = 2;
                    String content;
                    do {
                        content = getContent(newsTagNodes.get(i+what));
                        what++;
                    } while (content.trim().equals(""));
                    feedItem.description = content;
                    if(hcounter < newsTagLinks.size())feedItem.link = newsTagLinks.get(hcounter).getAttributeByName("href");
                    newsFeed.add(feedItem);
                    hcounter++;
                }

            }
        }

        return newsFeed;
    }

    private static ArrayList<NewsDetails> getNewsDetails(List<TagNode> nodes) {
        ArrayList<NewsDetails> details = new ArrayList<NewsDetails>();
        if(nodes.size() > 0){
            TagNode newsRootNode = nodes.get(0);
            TagNode newsContent = newsRootNode;
            List <TagNode> descriptItems = newsContent.getChildTagList();
            //new versting
            List<TagNode> newVerstNews = HtmlParser.getLinksByClass(newsContent, "div", "class", "news-container");
            if(newVerstNews.size() > 0) {
                newsContent = newVerstNews.get(0);
                List <TagNode> newDescriptItems = newsContent.getChildTagList();
                newDescriptItems.addAll(descriptItems);
                descriptItems = newDescriptItems;
            }
            //
            if(descriptItems.size() > 0){
                int textCounter = 0;
                for(int i = 0; i < descriptItems.size();i++){
                    NewsDetails detail = new NewsDetails();
                    TagNode item = descriptItems.get(i);
                    if(item.getName().equals("p")){
                        if(textCounter != 0 && !HtmlParser.getContent(item).contains("Дата")){
                            String content = HtmlParser.getContent(item);
                            if(!content.trim().equals("")) {
                                textCounter++;
                                detail.type = NewsDetails.detType.TEXT;
                                detail.content = content;
                                details.add(detail);
                            }
                        }
                    } else if(item.getName().equals("div")){
                        String classType = item.getAttributeByName("class");
                        if(classType != null && classType.equals("center")){
                            String content = HtmlParser.getContent(item);
                            if(!content.trim().equals("")) {
                                textCounter++;
                                detail.type = NewsDetails.detType.TEXT;
                                detail.content = content;
                                details.add(detail);
                            }
                            List<TagNode> imageUrls = HtmlParser.getLinksByClass(item, "img");
                            if(imageUrls.size() > 0){
                                detail.type = NewsDetails.detType.PICTURE;
                                detail.content = imageUrls.get(0).getAttributeByName("src");
                                details.add(detail);
                            }

                        }

                    }

                }

                if(textCounter <= 1){
                    String content = HtmlParser.getContent(newsRootNode);
                    if(!content.trim().equals("")) {
                        NewsDetails detail = new NewsDetails();
                        textCounter++;
                        detail.type = NewsDetails.detType.TEXT;
                        detail.content = content;
                        details.add(detail);
                    }
                }

            }

        }
        return details;
    }

    public static Serial getSerialDetails(String s){
        Serial serial = new Serial();
        SerialDetails detail;
        ArrayList<Season> seasons;
        List<TagNode> nodes = HtmlParser.parseSerialDetails(s);
        if(nodes.size() >= 2){

            TagNode serialDescriptionNode = nodes.get(0);
            if(serialDescriptionNode != null){

                detail = new SerialDetails();

                String url = HtmlParser.getLinksByClass(serialDescriptionNode,"img").get(0).getAttributeByName("src");
                if(!url.trim().equals("")){
                    url = ConstantStorage.BASE_URL + url;
                    detail.imageLink = url;
                }

                String description = HtmlParser.getContent(serialDescriptionNode);
                if(!description.trim().equals("")) detail.description = description;

                serial.setDetails(detail);

            }

            TagNode serialTorrListNode = nodes.get(1);
            if(serialTorrListNode != null){
                seasons = HtmlParser.getSessons(serialTorrListNode);
                serial.seasonCounter = seasons.size();
                serial.setSeasons(seasons);
            }

        }

        return serial;
    }

    public static ArrayList<Season> getSessons(TagNode serialTorrListNode) {
        ArrayList<Season> seasons = new ArrayList<Season>();

        List<TagNode> torrentsNodes = HtmlParser.getLinksByClass(serialTorrListNode,"div");
        if(torrentsNodes.size() > 0){

            Season season = null;
            for(TagNode torNod : torrentsNodes){
                String classType = torNod.getAttributeByName("class");
                if (classType != null){
                    //create a ll with 1 season (inflate)
                    if(classType.equals("content")){
                        if(season != null)seasons.add(season);
                        season = new Season();
                        season.name = HtmlParser.getContent(torNod);
                    } else if(classType.contains("t_row")){

                        Series series = new Series();
                        season.seriesCounter++;

                        List<TagNode> numbers = HtmlParser.getLinksByClass(torNod,"td","class","t_episode_num");
                        if(numbers.size() > 0){
                            String number = HtmlParser.getContent(numbers.get(0));
                            if(!number.trim().equals("")) series.number = number;
                        }

                        List<TagNode> titles = HtmlParser.getLinksByClass(torNod,"nobr",true);
                        if(titles.size() > 0){
                            String title = HtmlParser.getContent(titles.get(0));
                            if(!title.trim().equals("")) series.title = title;
                        }

                        //TODO need to get links

                        season.series.add(series);
                    }
                }
            }
            if(season != null)seasons.add(season);
        }
        return seasons;
    }

    private static String getHtmlContent(TagNode root){
        if(root != null) return "<" + root.getName() + ">" + new HtmlCleaner().getInnerHtml(root) + "</" + root.getName() + ">";
        else return "";
    }

    private static TagNode getRootNode(String response) {
        HtmlCleaner cleaner = null;
        try {
            cleaner = new HtmlCleaner();
        } catch (Exception e) {
            e.printStackTrace();
            return new TagNode("error");
        }

        return cleaner.clean(response);
    }

    public static String getContent(TagNode node) {
        StringBuilder result = new StringBuilder();
        List<Object> items = node.getAllChildren();
        for (Object item : items) {
            if (item instanceof ContentNode) {
                result.append(((ContentNode) item).getContent());
            } else if(item instanceof TagNode){
                if(((TagNode) item).getName().equals("br") || ((TagNode) item).getName().equals("p")) result.append("<br>");
                else if (((TagNode) item).getName().equals("script")) continue;
                result.append(getContent((TagNode) item));
            }
        }
        return result.toString();
    }

    public static List<TagNode> getLinksByClass(TagNode rootNode, String nodeName, String TagVal, String CSSClassname){
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

    public static List<TagNode> getLinksByClass(TagNode rootNode, String nodeName, boolean recursive){
        return rootNode.getElementListByName(nodeName, recursive);
    }

    public static List<TagNode> getLinksByClass(TagNode rootNode, String nodeName){
        return rootNode.getElementListByName(nodeName, true);
    }

}
