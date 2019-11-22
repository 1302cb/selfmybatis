package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate;

import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.ParseSQL;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.SQLUtil;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
//@Component
public class DeleteParser implements ParseSQL {

    private static Logger logger = LoggerFactory.getLogger(UpdateParser.class);

    @Override
    public String parse(String sql,String placeholder){
        logger.info(sql);
        String res = "";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(sql));
            logger.info("初始化成功");
        } catch (Exception ignored) {
            logger.info("初始化解析错误");
        }
        if(statement!=null){
            res = statement.toString();
            logger.info("删除中的sql语句-->" + res);
            if (res.contains("WHERE")) {
                res = SQLUtil.whereProcess(res, ((Delete) statement).getWhere().toString(), placeholder);
                logger.info("delete中处理了where之后的res-->" + res);
            }
            return res;
        }
        return "sql语法错误";
    }
}
