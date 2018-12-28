package com.jhc.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuaichenJiang
 * @Title
 * @Description
 * @date 2018/11/21  14:47
 */
public class ExcelUtil2 {

    // 定义单元格为数字时的格式
    private static DecimalFormat df = new DecimalFormat("0");
    // 定义单元格为日期时的格式
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    // 定义单元格为小数时的格式
    private static DecimalFormat nf = new DecimalFormat("0.000000");

    public static ArrayList<ArrayList<Object>> readExcel(File file) {
        if (file == null) {
            return null;
        }
        if (file.getName().endsWith("xlsx")) {
            return readExcel2007(file);
        } else {
            return readExcel2003(file);
        }
    }

    // 读取2003版本的excel文件
    public static ArrayList<ArrayList<Object>> readExcel2003(File file) {
        try {
            // rowList存储整个Excel文件的数据
            ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
            // colList存储一行的数据
            ArrayList<Object> colList;
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
            // 读取sheet1中的内容
            HSSFSheet sheet = wb.getSheetAt(0);
            // 每一行
            HSSFRow row;
            // 每个单元格
            HSSFCell cell;
            Object value;
            // 遍历有数据的第一行到最后一行
            for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount < sheet
                    .getPhysicalNumberOfRows(); i++) {
                // 得到当前行
                row = sheet.getRow(i);
                colList = new ArrayList<Object>();
                // 如果当前行数据为空，并且不是最后一行，那么直接将空的colList加入rowList中
                if (row == null) {
                    if (i != sheet.getPhysicalNumberOfRows()) {
                        rowList.add(colList);
                    }
                    continue;
                } else {
                    //
                    rowCount++;
                }
                for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null
                            || cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                        if (j != row.getLastCellNum()) {
                            colList.add("");
                        }
                        continue;
                    }
                    switch (cell.getCellType()) {
                        case XSSFCell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            break;
                        case XSSFCell.CELL_TYPE_NUMERIC:
                            if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                                value = df.format(cell.getNumericCellValue());
                            } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                                value = nf.format(cell.getNumericCellValue());
                            } else if(HSSFDateUtil.isCellDateFormatted(cell)){
                                value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                            } else{
                                value = cell.getNumericCellValue();
                            }
                            break;
                        case XSSFCell.CELL_TYPE_BOOLEAN:
                            value = Boolean.valueOf(cell.getBooleanCellValue());
                            break;
                        case XSSFCell.CELL_TYPE_BLANK:
                            value = "";
                            break;
                        default:
                            value = cell.toString();
                    }
                    System.out.print(value + "\t");
                    colList.add(value);
                }
                System.out.println();
                rowList.add(colList);
            }
            return rowList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<ArrayList<Object>> readExcel2007(File file) {
        try {
            ArrayList<ArrayList<Object>> rowList = new ArrayList<ArrayList<Object>>();
            ArrayList<Object> colList;
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;
            Object value;
            for (int i = sheet.getFirstRowNum(), rowCount = 0; rowCount <= sheet
                    .getLastRowNum(); i++) {
                row = sheet.getRow(i);
                colList = new ArrayList<Object>();
                if (row == null) {
                    if (i != sheet.getPhysicalNumberOfRows()) {
                        rowList.add(colList);
                    }
                    continue;
                } else {
                    rowCount++;
                }
                for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if (cell == null
                            || cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                        if (j != row.getLastCellNum()) {
                            colList.add("");
                        }
                        continue;
                    }
                    switch (cell.getCellType()) {
                        case XSSFCell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            break;
                        case XSSFCell.CELL_TYPE_NUMERIC:
                            if ("@".equals(cell.getCellStyle().getDataFormatString())) {
                                value = df.format(cell.getNumericCellValue());
                            } else if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                                value = nf.format(cell.getNumericCellValue());
                            } else if(HSSFDateUtil.isCellDateFormatted(cell)){
                                value = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
                            } else{
                                value = cell.getNumericCellValue();
                            }
                            break;
                        case XSSFCell.CELL_TYPE_BOOLEAN:
                            value = Boolean.valueOf(cell.getBooleanCellValue());
                            break;
                        case XSSFCell.CELL_TYPE_BLANK:
                            value = "";
                            break;
                        default:
                            value = cell.toString();
                    }
                    colList.add(value);
                }
                rowList.add(colList);
            }
            return rowList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeExcel(ArrayList<ArrayList<Object>> result,
                                  String path) {
        if (result == null) {
            return;
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("sheet1");
        for (int i = 0; i < result.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            if (result.get(i) != null) {
                for (int j = 0; j < result.get(i).size(); j++) {
                    HSSFCell cell = row.createCell(j);
                    cell.setCellValue(result.get(i).get(j).toString());
                }
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        File file = new File(path);
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DecimalFormat getDf() {
        return df;
    }

    public static void setDf(DecimalFormat df) {
        ExcelUtil2.df = df;
    }

    public static SimpleDateFormat getSdf() {
        return sdf;
    }

    public static void setSdf(SimpleDateFormat sdf) {
        ExcelUtil2.sdf = sdf;
    }

    public static DecimalFormat getNf() {
        return nf;
    }

    public static void setNf(DecimalFormat nf) {
        ExcelUtil2.nf = nf;
    }

    /**
     * 判断文件是否存在
     * @param fileName
     * @return
     */
    private static String judgeFileExist(String fileName){
        String resultName = "";
        File file = new File(fileName);
        if(file.exists()){
            String temp3 = fileName.substring(0, fileName.lastIndexOf("."));
            try{
                System.out.println("fileName.indexOf('-'):"+fileName.lastIndexOf("-")+ ", fileName.indexOf('.')"+ fileName.indexOf('.'));
                String temp  = fileName.substring(fileName.indexOf('-') + 1, fileName.indexOf('.'));
                System.out.println("temp:" + temp);
                String temp2  = fileName.substring(0, fileName.indexOf('-'));
                int num = Integer.parseInt(temp);
                num ++;
                resultName = temp2 + "-" + num + fileName.substring(fileName.lastIndexOf("."), fileName.length());
            } catch (Exception e){
                resultName = temp3 + "-1" + fileName.substring(fileName.lastIndexOf("."), fileName.length());
            }
        }else{
            resultName = fileName;
        }
        System.out.println("resultName:" + resultName);
        return resultName;
    }


    public static void main(String[] args){
        File file = new File("E:/bill/898319856914266_20181119_76.xls");
        ArrayList<ArrayList<Object>> result = ExcelUtil2.readExcel(file);
        for(ArrayList<Object> resultLine : result){
            for(Object cell : resultLine){
                String s = cell.toString();
                System.out.print(s+"("+ cell.getClass() +")"+"\t");
            }
            System.out.println();
        }
        String fileName = "E:/bill/test/898319856914266_20181119_76.xlsx";
        String rsName = judgeFileExist(fileName);
        while(true) {
            rsName = judgeFileExist(rsName);
            if (rsName.equals(judgeFileExist(rsName)))
                break;
        }
        writeExcel(result, rsName);
    }
}
