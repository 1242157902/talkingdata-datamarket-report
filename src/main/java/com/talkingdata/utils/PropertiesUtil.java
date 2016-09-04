package com.talkingdata.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/** 
 * 读取properties文件工具类 
 */  
public class PropertiesUtil {

    public static void main(String[] args)
    {
        PropertiesUtil pu = new PropertiesUtil();
    }

      
    /** 
     * 获取指定路径下property文件中的某个字段(没有默认值) 
     * @param key 字段 
     * @param filePath 属性文件 
     * @return 与key对应的value 
     * @throws FileNotFoundException if property file doesn't exists 
     * @throws IOException if there is some exception when load from property file 
     */  
    public static String getProperties(String key,String filePath) throws FileNotFoundException, IOException{  
        File file = new File(filePath);  
        Properties props = new Properties();  
        props.load(new FileInputStream(file));  
        return props.getProperty(key);  
    }  
      
    /** 
     * 获取指定路径下property文件中的某个字段(可以设置默认值) 
     * @param key 字段 
     * @param defaultValue 默认值
     * @param filePath 属性文件 
     * @return 与key对应的value 
     * @throws FileNotFoundException if property file doesn't exists 
     * @throws IOException if there is some exception when load from property file 
     */  
    public static String getProperties(String key,String defaultValue,String filePath) throws FileNotFoundException, IOException{  
        File file = new File(filePath);  
        Properties props = new Properties();  
        props.load(new FileInputStream(file));  
        return props.getProperty(key,defaultValue);  
    }  
}