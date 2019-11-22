package com.chenbo.selfmybatis.selfmybatis;


import com.chenbo.selfmybatis.selfmybatis.crawjob.crowJobImpl.SimpleCrawJob;
import com.chenbo.selfmybatis.selfmybatis.crawjob.entity.CrowMate;
import com.chenbo.selfmybatis.selfmybatis.crawjob.entity.CrowResult;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SelfmybatisApplicationTests {

    private Logger logger = LoggerFactory.getLogger(SelfmybatisApplicationTests.class);

    @Test
    public void testCeshi() {
        try {
            URL url = new URL("http://zhibo.zuoyebang.com/goods/web/course/list?isJson=1&grade=&subject=&season=&type=2&seasonTime=&pn=");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            logger.info("开始打印抓取的头");
            for (Map.Entry entry : map.entrySet()) {
                logger.info(String.valueOf(entry));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void fetchPage() {
        String url = "https://my.oschina.net/u/566591/radar/getUserPortraitRadarMap";
        Set<String> selectRule = new HashSet<>();
        selectRule.add("div[class=title]"); // 博客标题
        selectRule.add("div[class=blog-body]"); // 博客正文

        CrowMate crawlMeta = new CrowMate();
        crawlMeta.setUrl(url); // 设置爬取的网址
        crawlMeta.setSelectorRule(selectRule); // 设置抓去的内容


        SimpleCrawJob job = new SimpleCrawJob();
        job.setCrowMate(crawlMeta);
        Thread thread = new Thread(job, "crawler-test");
        thread.start();

        try {
            thread.join(); // 确保线程执行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        CrowResult result = job.getCrowResult();
        logger.info("" + result.getResult());
    }

    @Test
    public void testClient() {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://my.oschina.net/u/566591/radar/getUserPortraitRadarMap?userId=566591&skillsNum=5");
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            logger.info(Streams.asString(entity.getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testComplexSelect() {
        try {
            Select select = (Select) new CCJSqlParserManager().parse(new StringReader("select name from Math where score between 60 and 90 order by score asc;"));
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            logger.info("selectBody-->" + select.getSelectBody().toString());
            // TODO: 2019/11/20 between的处理
            logger.info(plainSelect.getWhere().toString());
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testSqlParserSelect() {
        String sql = "select * from t1 where id = 112 and age > 100;";
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = null;
        try {
            select = (Select) parserManager.parse(new StringReader(sql));
            logger.info("select-->" + select);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        logger.info("selectitems-->" + plain);
        String selectitems = plain.getWhere().toString();
        logger.info("where-->" + selectitems);


    }

    /*
    获取插入的数据
     */
    @Test
    public void testSqlParseInsert() {
        String sql = "insert into Student(ID,name,class) values(10152510302,\"Tom\",\"class 1\");";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        Insert insertStatement = (Insert) statement;
        // TODO: 2019/11/20 考虑到多条插入的问题。
        List<Expression> insert_values_expression = ((ExpressionList) insertStatement
                .getItemsList()).getExpressions();
        List<String> str_values = new ArrayList<String>();
        for (int i = 0; i < insert_values_expression.size(); i++) {
            logger.info("insert-->" + insert_values_expression.get(i).toString());
            str_values.add(insert_values_expression.get(i).toString());
        }
    }

    @Test
    public void testSqlParseDelete() throws JSQLParserException {
//        logger.info(String.valueOf(new CCJSqlParserManager().parse(new StringReader("delete from table where id =1;"))));
        try {
            Delete delete = (Delete) new CCJSqlParserManager().parse(new StringReader("delete from table where id =1;"));
            String string = delete.getWhere().toString();
            logger.info("delete-->" + string);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSqlParseUpdate() {
        try {
            Update update = (Update) new CCJSqlParserManager().parse(new StringReader("update  table " +
                    "set age=43,name= 32" +
                    "where id = 1"));
            logger.info("update-->" + update);
            logger.info("set-->" + update.getColumns().toString() + " where-->" + update.getWhere().toString() + " setString " + update.getReturningExpressionList());
            logger.info("set-->" + update.getExpressions().toString());
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLimit() throws JSQLParserException {
//        String sql = "select name from Math where score between 60 and 90 order by score asc having score>=30 limit 9,10;";
        String sql = "select class,avg(score) as avg_score from Student natural join Math group by class having avg(score) < 60;";
        Select select = (Select) new CCJSqlParserManager().parse(new StringReader(sql));
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        logger.info("body-->" + plainSelect);
        logger.info("limit-->" + plainSelect.getLimit());
        logger.info("havin-->" + plainSelect.getHaving());
    }


    @Test
    public void testSlf4j() {
        String str = "(1, 2, 3), (2, 3, 4)";
        String[] splitArray = str.split(" ");
        logger.info("size:"+splitArray.length);
        String temp = str.replaceAll("\\(","");
        temp = temp.replaceAll("\\)","");
        logger.info(temp);

    }
}
