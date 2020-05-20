package com.qingqu.utils;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.qingqu.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *
 * </p>
 *
 * @author Administrator
 * @since 2020/5/9
 */
@Component
public class HtmlParseUtil {

    /*public static void main(String[] args) throws IOException {
            new HtmlParseUtil().parseJD("心理学").forEach(System.out::println);
    }*/

    public List<Content> parseJD(String keywords) throws IOException {
        //获取请求  https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword="+keywords;

        //解析网页.(Jsoup 返回Document 就是Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有js中可以使用的方法，这里都可以用
        Element element = document.getElementById("J_goodsList");
        //System.out.println(element.html());
        //获取所有的li元素
        Elements elements = document.getElementsByTag("li");
        //获取元素的内容,这里el 就是每一个li标签了！
        List<Content> goodlist = new ArrayList<>();
        for (Element el : elements) {
            //关于图片特别多的网站，所有图片都是延迟加载的
            //source-data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            if(!img.equals("") &&!price.equals("")&&!title.equals("")){
                Content content = new Content();
                content.setImg(img);
                content.setPrice(price);
                content.setTitle(title);
                goodlist.add(content);
            }
        }

        return goodlist;
    }
}
