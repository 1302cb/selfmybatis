package com.chenbo.selfmybatis.selfmybatis.jsqlparseutils;

import lombok.extern.flogger.Flogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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
    public static String getOneInsert(int columnLen,String placeholder) {
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
            if("".equals(strings[i])){
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
            if("IN".equals(wheres[i])){
                int len = 1;
                int t = i+1;
                while(!wheres[t].contains(")")){
                    wheres[t]="";
                    len++;
                    t++;
                }
                wheres[t] = getOneInsert(len,placeholder);
                i=t;
            }
        }
        String resWhere = stringArrayTransferString(wheres);
        res = res.replace(where, resWhere);
        return res;
    }
}
