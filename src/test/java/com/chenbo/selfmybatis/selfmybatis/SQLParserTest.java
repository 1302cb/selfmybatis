package com.chenbo.selfmybatis.selfmybatis;

import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.ParseSQL;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.SQLParserProcessor;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.SQLUtil;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate.DeleteParser;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate.InsertParser;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate.SelectParser;
import com.chenbo.selfmybatis.selfmybatis.jsqlparseutils.oprate.UpdateParser;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SQLParserTest {

    private static Logger logger = LoggerFactory.getLogger(SQLParserTest.class);


    @Test
    public void testFirst() {
        String inStr = "SELECT comment_id, parent_id, student_id, content, pid\n" +
                "\t, is_delete, is_admin, create_time, like_count\n" +
                "FROM ep_sub_course_comment\n" +
                "WHERE is_delete = 0\n" +
                "\tAND pid IN (\n" +
                "\t\t'55251069678c4d09b80e85d1af134cf8', \n" +
                "\t\t'993564b81aaa4016add34e97f3c6083e', \n" +
                "\t\t'083e7cff20f141209d36160182db56f1', \n" +
                "\t\t'886080ba171c4464951973da50c4b847', \n" +
                "\t\t'800558a5bcf644b8b444f6ecceaed282', \n" +
                "\t\t'5b284878758641e092efe454a95af3f6', \n" +
                "\t\t'dda66b0723d746ac92f8219466083dd6', \n" +
                "\t\t'5ad2491149554295b50b4cfeacb7be37', \n" +
                "\t\t'5a48ce327dc54c8c941bbadef491f723', \n" +
                "\t\t'07eb826740a949e2a4739958433a48a7', \n" +
                "\t\t'99082d9f6e984a9d810c9f4aa3c9fe08', \n" +
                "\t\t'21a93f905f8045db98f46bbb977e6d7c', \n" +
                "\t\t'2dc510d170084e9fb594b2af62a294da', \n" +
                "\t\t'3d21ca5139e44f519d5834b0125d19d4', \n" +
                "\t\t'520f93bd7ede4fd08a7495b18ec357fa', \n" +
                "\t\t'398551cb5ed84fc5827c5c823b1b0c42', \n" +
                "\t\t'e52a5383d9994f61bee490859b474c3d', \n" +
                "\t\t'5b971953d6b641b791e31fea2b707346', \n" +
                "\t\t'2764be0ad92948d9b7d2f375269a9b7b', \n" +
                "\t\t'180e290f421a4e6080c36633f0d9e30c'\n" +
                "\t)\n" +
                "ORDER BY create_time ASC\n";
//        String res = new SelectParser().parse(inStr,"?");
//
//        logger.info("result-->"+res);
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(inStr));
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        String res = statement.toString();
        String parttern = "'.'";
        res = res.replaceAll(parttern, "?");
        logger.info("result-->" + res);
    }

    @Test
    public void testUnion() throws JSQLParserException {
        String sql = "(select name from Student natural join Math where class=\"A\" and sex=\"women\" order by score desc)\n" +
                "union\n" +
                "(select name from Math where score between 60 and 90 order by score);";
        String res = CCJSqlParserUtil.parse(new StringReader(sql)).toString();
        String[] temp = res.split("UNION");
        String[] strings = new String[temp.length];
        for (int i = 0; i < strings.length; i++) {
            temp[i] = temp[i].trim();
            strings[i] = temp[i].substring(1, temp[i].length() - 1);
            logger.info("strings-->" + strings[i]);
        }
    }

    @Test
    public void test1() {
        String sql = "SELECT id, course_id, title, short_desc, course_desc\n" +
                "\t, price, care_bean_price, origin_price, large_image, medium_image\n" +
                "\t, small_image, mini_image, share_image, author_id, create_time\n" +
                "\t, update_time, course_num, is_deleted, on_sale, sub_course_can_buy\n" +
                "\t, media_type, has_free_course, on_sale_time\n" +
                "FROM ep_edu_course\n" +
                "WHERE course_id = 'c8251accc9634f3185d7309fd92c661b'\n" +
                "\tAND is_deleted = 0\n" +
                "\tAND on_sale = 1\n";
        String res = new SelectParser().parse(sql, "?");

        logger.info("result-->" + res);
    }

    @Test
    public void test2() {
        String sql = "SELECT resourceid, student_id, potential_label_type, performance_score, stat_week_time\n" +
                "\t, answers_score\n" +
                "FROM ep_potential_week_student_score\n" +
                "WHERE stat_week_time = 20191118\n" +
                "\tAND student_id = '1b7fdf58d88b4bae80b651a10ac82c30'\n" +
                "\tAND is_deleted = 0";
        String res = new SelectParser().parse(sql, "?");

        logger.info("result-->" + res);
    }

    @Test
    public void testSelect() {
        String str = "select name from Student natural join Math where class=\"A\" and sex=\"women\" order by score desc;";
        String temp = "SELECT account_id, grade, expire_time FROM ep_im_account WHERE account_id in() AND isDeleted = 0";
        String res = new SelectParser().parse(temp, "?");
        logger.info("result-->" + res);
    }

    @Test
    public void testInsert() {
        String sql = "insert into Student(ID,name,class) values(10152510302,\"Tom\",\"class 1\");";
        String res = new InsertParser().parse(sql, "?");

        logger.info("result-->" + res);
    }

    @Test
    public void testDelete() {
        String sql = "delete from tb_table where id = 3";
        String res = new DeleteParser().parse(sql, "?");

        logger.info("result-->" + res);
    }

    @Test
    public void testUpdate() {
        String sql = "update Student set age=18 where name=\"Tom\";";
//        String sql = "SELECT account_id, grade, expire_time FROM ep_im_account WHERE account_id in() AND isDeleted = 0";
        String res = new UpdateParser().parse(sql, "?");

        logger.info("result-->" + res);
    }

    @Test
    public void testBufferedReader() throws IOException {
        String address = "/Users/it00002772/sql.txt";
        List<String> list = SQLUtil.readLineFromAddress(address);
        logger.info("list-->"+list.toString());
        Map<String,Set<String>> map = SQLUtil.parseSQLFromSQL(list);
        logger.info("map-->");
        SQLUtil.writeToExcel("/Users/it00002772/sql/sql.xls",map);
    }

    @Test
    public void testCode() {

//
//        遍历塞入不同的表里面
//        while ((temp = bufferedReader.readLine()) != null) {
//            logger.info("len-->" + (++len));
//            logger.info("res-->" + SQLUtil.forceParse(temp, "?"));
//            String tableName = SQLUtil.getTableName(temp);
//            logger.info("tan-->" + tableName);
//            if (map.get(tableName) == null) {
//                Set<String> set = new HashSet<>();
//                set.add(SQLUtil.forceParse(temp, "?"));
//                map.put(tableName, set);
//            } else {
//                Set<String> set = map.get(tableName);
//                set.add(SQLUtil.forceParse(temp, "?"));
//                map.put(tableName, set);
//            }
//        }
//        bufferedReader.close();
//        logger.info("map-->" + map);
//        FileOutputStream fileOutputStream = new FileOutputStream("/Users/it00002772/sql/sql.csv");
//        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream), 1024);
//        bufferedWriter.write("表名" + "," + "sql语句");
//        bufferedWriter.newLine();
//        for (String key : map.keySet()) {
//            Set<String> set = map.get(key);
//            for (String value : set) {
//                logger.info("value-->" + value);
//                bufferedWriter.write(key + "," + value);
//                bufferedWriter.newLine();
//            }
//        }
//
//        for (String key : map.keySet()) {
//            FileWriter writer = new FileWriter("/Users/it00002772/sql/" + key + ".txt");
//            Set<String> values = map.get(key);
//            logger.info("set-->" + values);
//            for (String value : values) {
//                writer.write(value + "\n");
//            }
//            writer.close();
//        }
    }

    @Test
    public void testSpacial() {

        String sql = "SELECT account_id, grade, expire_time FROM ep_im_account WHERE account_id in() AND isDeleted = 0;";
        Statement statement = null;
        try {
            statement = CCJSqlParserUtil.parse(new StringReader(sql));
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
//        PlainSelect select = (PlainSelect) ((Select) statement).getSelectBody();
        logger.info("result->" + statement.toString());
    }
}
