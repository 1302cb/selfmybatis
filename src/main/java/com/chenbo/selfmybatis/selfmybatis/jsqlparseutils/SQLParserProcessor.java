package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils;
/**
 * 解析的一个实现类
 *
 * @author chenbo
 * @Date 2019/11/21 3:35 下午
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
public class SQLParserProcessor {

    private static Logger logger = LoggerFactory.getLogger(SQLParserProcessor.class);

    ParseSQL parseSQL;

    public String parse(String sql,String placeholder) {
        String res = parseSQL.parse(sql,placeholder);
        logger.info(res);
        return res;
    }

}
