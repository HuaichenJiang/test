package com.jhc.util;
import org.jumpmind.symmetric.csv.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @author ShiYuying
 * @Title
 * @Description
 * @date 2018/11/21 9:33
 */
public class CsvUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CsvUtils.class);

    private static final String REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String CHARSET = "GBK";

    /**
     *  读取csv文件
     * @param filepath
     * @return
     */
    public static List<List<Object>> readCsv (String filepath) {
        File csv = new File(filepath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line = "";
        List<List<Object>> allString = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                List<Object> lineTxt = new ArrayList<Object>();
                String[] everyCell = line.split(REGEX);
                for(Object s : everyCell){
                    lineTxt.add(s);
                }
                allString.add(lineTxt);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allString;
    }

    /**
     * 将一个List存入csv
     * @param dataList
     * @param filename
     * @author HuaichenJiang
     * @return
     */
    public static boolean createCSVFile(List<List<Object>> dataList, String filename) {
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(filename);
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream
                    (csvFile), CHARSET), 1024);
            for (List<Object> row : dataList) {
                writeRow(row, csvWtriter);
            }
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将String写入csv文件 (追加方式)
     * @param result
     * @author HuaichenJiang
     * @return
     */
    public static boolean writeFile(String result, String fileName) {
        try {
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file, true);
            fileWritter.write(result);
            fileWritter.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将String写入csv文件 (非追加方式)
     * @param result
     * @author HuaichenJiang
     * @return
     */
    public static boolean writeFileNotAppend(String result, String fileName) {
        try {
            File file = new File(fileName);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file);
            fileWritter.write(result);
            fileWritter.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }


    /**
     *  读取csv文件
     * @param filepath
     * @return
     */
    public static List<List<Object>> readCsv2 (String filepath, String charset) {
        List<List<Object>> lists = new ArrayList<>();
        try {
            CsvReader csvReader = new CsvReader(filepath, ',', Charset.forName(charset));
            while (csvReader.readRecord()) {
                List<Object> list = Arrays.asList(csvReader.getValues());
                lists.add(list);
            }
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }

    public static void main(String[] args) {
        String path = "E:\\bill\\cmb";
        File Dic = new File(path);
        File[] fileList = Dic.listFiles();
        for (File file : fileList) {
            List<List<Object>> rs = readCsv(path + "/" + file.getName());
            if (rs.size() > 1){

            }
        }
    }

}