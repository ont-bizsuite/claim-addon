package com.ontology.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.crypto.Digest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public class HelperUtil {

    public static Boolean isEmptyOrNull(Object... params) {
        if (params != null) {
            for (Object val : params) {
                if ("".equals(val) || val == null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }


    public static byte[] byteMerrage(byte[] head, byte[] tail) {
        byte[] temp = new byte[head.length + tail.length];
        System.arraycopy(head, 0, temp, 0, head.length);
        System.arraycopy(tail, 0, temp, head.length, tail.length);
        return temp;
    }


    public static Boolean isJSONStr(String str) {
        try {
            JSONObject obj = JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static long Date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr).getTime() / 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get current method name
     *
     * @return
     */
    public static String currentMethod() {
        return new Exception("").getStackTrace()[1].getMethodName();
    }


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    /**
     * hash
     */
    public static String sha256(String data) {
        byte[] bytes = Digest.sha256(data.getBytes());
        return com.github.ontio.common.Helper.toHexString(bytes);
    }

}
