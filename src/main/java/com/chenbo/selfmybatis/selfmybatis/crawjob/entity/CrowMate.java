package com.chenbo.selfmybatis.selfmybatis.crawjob.entity;

import lombok.Data;

import java.util.*;

@Data
public class CrowMate {
    /*
    爬虫的url地址
     */
    private String url;
    /*
    选择的规则
     */
    private Set<String> selectorRule;

    /*
    防止npe
     */
    public Set<String> getSelectorRule() {
        return selectorRule == null ? new HashSet<>() : selectorRule;
    }
}
