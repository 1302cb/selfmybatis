package com.chenbo.selfmybatis.selfmybatis.crawjob.entity;

import lombok.Data;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;
@Data
public class CrowResult {
    /*
    爬取的URL
     */
    private String url;
    /*
    爬取的html
     */
    private Document htmlDoc;
    /*
    最后的结果
     */
    private Map<String, List<String>> result;
}
