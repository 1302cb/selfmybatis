package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate;

import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.ParseSQL;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.SQLUtil;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
//@Component
public class InsertParser implements ParseSQL {

    private static Logger logger = LoggerFactory.getLogger(UpdateParser.class);

    @Override
    public String parse(String sql,String placeholder){
        String res = "";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(sql));
        } catch (Exception ignored) {
            logger.info("初始化解析错误");
        }

        if(statement!=null){
            res = statement.toString();
            logger.info("插入中的sql语句" + res);
            String values = ((Insert) statement).getItemsList().toString();
            int columnLen = ((Insert) statement).getColumns().size(), sumLen = values.split(" ").length;
            StringBuffer resultLine = new StringBuffer();
            int line = sumLen / columnLen;
            for (int i = 0; i < line; i++) {
                resultLine.append(SQLUtil.getOneInsert(columnLen,placeholder));
                if (i != line - 1) {
                    resultLine.append(", ");
                }
            }
            res = res.replace(values, resultLine.toString());
            logger.info("res:" + res);
            return res;
        }
        return "sql语法错误";
    }
}
