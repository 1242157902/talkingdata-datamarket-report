package com.talkingdata.utils;

/**
 * User：    ysl
 * Date:   2016/7/14
 * Time:   10:24
 */
public class StringUtils {

    /**
     * 判断该字符串是否为空，是的话，返回true，否则返回false
     * @param str       :字符串
     * @return
     */
    public static boolean isEmpty(String str)
    {
        if(str!=null&&!"".equals(str))
        {
            return false;
        }else{
            return true;
        }
    }
}
