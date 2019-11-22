package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate;

import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.ParseSQL;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.SQLUtil;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.update.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.Arrays;
//@Component
public class UpdateParser implements ParseSQL {


    private static Logger logger = LoggerFactory.getLogger(UpdateParser.class);

    @Override
    public String parse(String sql,String placeholder) {
        String res = "";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(sql));
        } catch (Exception ignored) {
            ignored.printStackTrace();
            logger.info("初始化解析错误");
        }
        if(statement!=null){
            res = statement.toString();
            logger.info("res " + res);
            String columns = ((Update) statement).getColumns().toString();
            String[] splitBySpace = SQLUtil.sqlSplit(res, " ");
            logger.info(Arrays.asList(splitBySpace).toString());
            String[] columnsArray = SQLUtil.splitExpressions(columns);
            int columnsLen = columnsArray.length;
            for (int i = 0; i < columnsLen; i++) {
                int index = SQLUtil.getIndexFromStringArray(splitBySpace, columnsArray[i]);
                splitBySpace[index + 2] = placeholder;
                if (i != columnsLen - 1) {
                    splitBySpace[index + 2] += ",";
                }
            }
            //先将set的部分处理好
            res = SQLUtil.stringArrayTransferString(splitBySpace);

            logger.info("解决set后的sql-->" + res);
            //处理where的部分
            if (res.contains("WHERE")) {
                String where = ((Update) statement).getWhere().toString();
                logger.info("where " + where);
                res = SQLUtil.whereProcess(res, where, placeholder);
                logger.info("res " + res);
            }
            return res;
        }
        return "sql语法错误";
    }
}
