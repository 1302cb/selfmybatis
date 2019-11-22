package com.chenbo.selfmybatis.selfmybatis.crawjob.crowJobImpl;

import com.chenbo.selfmybatis.selfmybatis.crawjob.entity.CrowMate;
import com.chenbo.selfmybatis.selfmybatis.crawjob.entity.CrowResult;
import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SimpleCrawJob extends AbstractCrowJob {

    private static Logger logger = LoggerFactory.getLogger(SimpleCrawJob.class);

    private CrowMate crowMate;

    private CrowResult crowResult;

    /*
    抓取页面数据
     */
    @Override
    public void doFetchPage() throws Exception {
        logger.info("开始爬取URL");
        URL url = new URL(crowMate.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        BufferedReader in = null;

        StringBuffer read = new StringBuffer();

        /*
        设置通用的请求头
         */
        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 建立实际的连接
        connection.connect();

        Map<String, List<String>> map = connection.getHeaderFields();

        for (String key : map.keySet()) {
            logger.info("请求头的内容-->" + map.get(key));
        }

        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String temp = "";

        while ((temp = in.readLine()) != null) {
            logger.info("抓取的每行的内容-->" + temp);
            read.append(temp);
        }
        /*
        在最后的时候记得关闭输入流
         */
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        对得到的数据进行解析
         */
        doParse(read.toString());
    }

    private void doParse(String html) {
        logger.info("开始解析html");
        Document doc = Jsoup.parse(html);
        Map<String, List<String>> map = new HashMap<>(crowMate.getSelectorRule().size());
        for (String rule : crowMate.getSelectorRule()) {
            logger.info("规则-->"+rule);
            List<String> value = new ArrayList<>();
            for (Element ruleElement : doc.select(rule)) {
                logger.info("规则下面获取的内容-->"+ruleElement.text());
                value.add(ruleElement.text());
            }
            map.put(rule, value);
        }
        this.crowResult = new CrowResult();
        this.crowResult.setHtmlDoc(doc);
        this.crowResult.setResult(map);
        this.crowResult.setUrl(this.crowMate.getUrl());
    }
}
