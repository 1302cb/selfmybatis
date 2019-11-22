package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils;

import lombok.extern.flogger.Flogger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.NestingKind;
import java.io.*;
import java.util.*;

/**
 * sql的一个工具类
 *
 * @author chenbo
 * @Date 2019/11/21 3:29 下午
 */
public class SQLUtil {
    private static Logger logger = LoggerFactory.getLogger(SQLUtil.class);

    /*
    根据一行的元素个数返回替换好的行
     */
    public static String getOneInsert(int columnLen, String placeholder) {
        if (columnLen <= 0) {
            return "";
        } else if (columnLen == 1) {
            return "(?)";
        }
        StringBuffer resultLine = new StringBuffer();
        resultLine.append("(");
        for (int i = 0; i < columnLen; i++) {
            resultLine.append(placeholder);
            if (i != columnLen - 1) {
                resultLine.append(", ");
            }
        }
        resultLine.append(")");
        return resultLine.toString();
    }

    /*
    将String数组组装成一个单词中间隔开一个空格的字符串
     */
    public static String stringArrayTransferString(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            //为空的时候需要跳过
            if ("".equals(strings[i]) || strings[i] == null) {
                continue;
            }
            if (i != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(strings[i]);
        }
        return stringBuilder.toString();
    }

    /*
    分割得到的sql组件
     */
    public static String[] sqlSplit(String component, String splitter) {
        if (component == null) {
            return null;
        }
        return component.split(splitter);
    }


    /*
    将[xxx, xxx]格式切割出来
     */
    public static String[] splitExpressions(String value) {
        if ("".equals(value) || value == null) {
            return null;
        }
        value = value.substring(1, value.length() - 1);
        String[] temp = value.split(",");
        String[] res = new String[temp.length];
        for (int i = 0; i < temp.length; i++) {
            res[i] = temp[i].trim();
        }
        return res;
    }

    /*
    得到一个单词在一个字符串数组里面到位置
     */
    public static int getIndexFromStringArray(String[] strings, String target) {
        if (strings == null || "".equals(target)) {
            return -1;
        }
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            if (target.equals(strings[i])) {
                return i;
            }
        }
        return -1;
    }

    /*
    处理where
     */
    public static String whereProcess(String res, String where, String placeholder) {
        //between 和 =
        List<String> list = new ArrayList<>();
        list.add("=");
        list.add(">=");
        list.add("<=");
        list.add("<>");
        list.add(">");
        list.add("<");
        list.add("!>");
        list.add("!<");
        list.add("!=");
        String[] wheres = where.split(" ");
        for (int i = 0; i < wheres.length; i++) {
            if ("BETWEEN".equals(wheres[i])) {
                wheres[i + 1] = placeholder;
                wheres[i + 3] = placeholder;
            }
            //若是内连接则不好判断 条件 &&!(wheres[i].contains(".")&&wheres[i].contains("."))
            if (list.contains(wheres[i])) {
                wheres[i + 1] = placeholder;
            }
            if ("IN".equals(wheres[i])) {
                int len = 1;
                int t = i + 1;
                while (!wheres[t].contains(")")) {
                    wheres[t] = "";
                    len++;
                    t++;
                }
                wheres[t] = getOneInsert(len, placeholder);
                i = t;
            }
        }
        String resWhere = stringArrayTransferString(wheres);
        res = res.replace(where, resWhere);
        return res;
    }

    /*
    获取表名，由于并没有连表操作，就直接返回一个表了
     */
    public static String getTableName(String sql) {
        String[] strings = sql.split(" ");
        int len = strings.length;
        for (int i = 0; i < len; i++) {
            //三种情况下可以得到表名
            if ("FROM".equals(strings[i].toUpperCase())||"INTO".equals(strings[i].toUpperCase())||"UPDATE".equals(strings[i].toUpperCase())) {
                return strings[i + 1];
            }
        }
        return "";
    }

    /*
    判断是字符串是否为数字
     */
    public static boolean isNumber(String string) {
        int len = string.length();
        for (int i = 0; i < len; i++) {
            if (string.charAt(i) > '9' || string.charAt(i) < '0') {
                return false;
            }
        }
        return true;
    }

    /*
    强制解析，碰到数字和字符串直接转换
     */
    public static String forceParse(String sql, String placeholder) {
        //先将双引号的换成占位符
        String res = quotationMarkTransferToQuestionMark(sql, placeholder);
        res = stringArrayTransferString(res.split(" "));
//        logger.info("res-->" + res);

        //在将数字也转换出来.
        String[] strings = res.split(" ");
        int len = strings.length;
//        for (int i = 0; i < len; i++) {
//            logger.info("res-->" + strings[i]);
//        }
        for (int i = 0; i < len; i++) {
            if (isNumber(strings[i])) {
                strings[i] = placeholder;
            }
        }
        return stringArrayTransferString(strings);
    }

    /*
    判断是不是单引号或者双引号
     */
    public static boolean isQuotationMarks(String str) {
        return "\'".equals(str) || "\"".equals(str);
    }

    /*
    将sql里面有单引号或者双引号字符的全部都替换成占位符
     */
    public static String quotationMarkTransferToQuestionMark(String str, String placeholder) {
        StringBuffer stringBuffer = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            if (isQuotationMarks("" + str.charAt(i))) {
                int temp = i + 1;
                while (temp < len && !isQuotationMarks("" + str.charAt(temp))) {
                    temp++;
                }
                stringBuffer.append("?");
                i = temp;
            } else {
                stringBuffer.append(str.charAt(i));
            }
        }
        return stringBuffer.toString();
    }

    /*
    根据地址一行一行的读取，存到list里面
     */
    public static List<String> readLineFromAddress(String address) throws IOException {
        List<String> list = new ArrayList<>();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(address);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
        String temp = "";
        Map<String, Set<String>> map = new HashMap<>();
        int len = 0;

        while ((temp = bufferedReader.readLine())!=null){
            logger.info("len-->"+(++len));
            logger.info("res-->"+temp);
            list.add(temp);
        }
        return list;
    }
    /*
    将列表的sql解析并且根据表名分到不同到set里面去
     */
    public static Map<String,Set<String>> parseSQLFromSQL(List<String> list){
        int len = list.size();
        Map<String,Set<String>> map = new HashMap<>();
        for(int i=0;i<len;i++){
            String sql = list.get(i);
            String tableName = getTableName(sql);
            if(tableName!=null){
                if(map.get(tableName)==null){
                    Set<String> set = new HashSet<>();
                    set.add(forceParse(sql,"?"));
                    map.put(tableName,set);
                }else{
                    Set<String> set = map.get(tableName);
                    set.add(forceParse(sql,"?"));
                    map.put(tableName,set);
                }
            }
        }
        return map;
    }

    /*
    将按表分好到map导入到表格当中
     */
    public static void writeToExcel(String address,Map<String,Set<String>> map) {
        int index = 1;
        HSSFWorkbook wb = new HSSFWorkbook();
        //创建工作表
        HSSFSheet sheet = wb.createSheet("sheet1");
        //将数据填充到表格中去
        HSSFRow row = sheet.createRow(0);
        //将第一行填充进去
        row.createCell(0).setCellValue("查询的表");
        row.createCell(1).setCellValue("具体的sql语句");
        for(String key:map.keySet()){
            Set<String> set = map.get(key);
            for(String value:set){
                HSSFRow temp = sheet.createRow(index);
                temp.createCell(0).setCellValue(key);
                temp.createCell(1).setCellValue(value);
                index++;
            }
        }
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(new File(address));
            wb.write(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(stream!=null){
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
