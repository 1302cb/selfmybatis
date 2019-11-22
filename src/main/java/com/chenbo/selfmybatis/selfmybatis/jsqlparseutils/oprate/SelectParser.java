package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate;

import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.ParseSQL;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.SQLUtil;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
//@Component
public class SelectParser implements ParseSQL {


    private static Logger logger = LoggerFactory.getLogger(UpdateParser.class);

    @Override
    public String parse(String sql,String placeholder) {
        String res = "";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(sql));
        } catch (Exception ignored) {
            logger.info("初始化解析错误");
        }
        //select 有几种情况 where，where里面还有between，having,limit
        // TODO: 2019/11/21 没有考虑union的情况,子查询的情况
        if(statement!=null){
            PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
            res = select.toString();
            logger.info("res " + res);
            if(res.contains("UNION")){
                return processorUnion(res,placeholder);
            }else{
                return processorSimple(sql,placeholder);
            }
        }
        return "sql语法错误";
    }
    private String processorUnion(String res,String placeholder){
        String[] temp = res.split("UNION");
        String[] strings = new String[temp.length];
        for(int i=0;i<strings.length;i++){
            temp[i] = temp[i].trim();
            strings[i] = temp[i].substring(1,temp[i].length()-1);
            logger.info("strings-->"+strings[i]);
        }
        for(int i=0;i<strings.length;i++){
            strings[i] = processorSimple(res,placeholder);
        }
        return "("+strings[0]+") UNION ("+strings[1]+")";
    }
    private String processorSimple(String sql,String placeholder){
        String res = "";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(sql));
        } catch (Exception ignored) {
            logger.info("初始化解析错误");
        }
        //select 有几种情况 where，where里面还有between，having,limit
        // TODO: 2019/11/21 没有考虑union的情况,子查询的情况
        if(statement!=null){
            PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
            res = select.toString();
            logger.info("res " + res);
            if (res.contains("WHERE")) {
                res = SQLUtil.whereProcess(res, select.getWhere().toString(),placeholder);
            }
            if (res.contains("HAVING")) {
                logger.info("having " + select.getHaving());
                String[] havings = select.getHaving().toString().split(" ");
                for (int i = 0; i < havings.length; i++) {
                    logger.info(havings[i]);
                }
                havings[2] = placeholder;
                String resHaving = SQLUtil.stringArrayTransferString(havings);
                res = res.replace(select.getHaving().toString(), resHaving);
                logger.info("res " + res);
            }
            return res;
        }
        return "sql语法错误";
    }
    private String processorSubQuery(){
        return null;
    }
}
