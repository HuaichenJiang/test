package com.jhc.util;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Title: ExcelUtil工具类
 * @Description: ExcelUtil工具类
 * @author: wenqiang.zhang@hand-china.com
 * @date 2017-02-10 19:45
 */

/* 
 * ExcelUtil工具类实现功能: 
 * 导出时传入list<T>,即可实现导出为一个excel,其中每个对象Ｔ为Excel中的一条记录. 
 * 导入时读取excel,得到的结果是一个list<T>.T是自己定义的对象. 
 * 需要导出的实体对象只需简单配置注解就能实现灵活导出,通过注解您可以方便实现下面功能: 
 * 1.实体属性配置了注解就能导出到excel中,每个属性都对应一列. 
 * 2.列名称可以通过注解配置. 
 * 3.导出到哪一列可以通过注解配置. 
 * 4.鼠标移动到该列时提示信息可以通过注解配置. 
 * 5.用注解设置只能下拉选择不能随意填写功能. 
 * 6.用注解设置是否只导出标题而不导出内容,这在导出内容作为模板以供用户填写时比较实用. 
 * 本工具类以后可能还会加功能,请关注我的博客: http://blog.csdn.net/lk_blog 
 */

public class ExcelUtil<T> {
	public static final int MAX_ROWS = 65536;

	private org.slf4j.Logger logger = LoggerFactory.getLogger(ExcelUtil.class);


	private List<String> errMsg = new ArrayList<String>();

	Class<T> clazz;

	private String psw;

	private String downloadPath = "/ImportHistory/";

	private Integer rowTotal;

	private Integer startRow = 1;

	private boolean isHeaderLine = false;

	private boolean isAddNumber = false;
	
	private Object object = null;

	public ExcelUtil(Class<T> clazz) {
		this.clazz = clazz;
	}

	public boolean hasErr() {
		return errMsg.size() > 0;
	}

	public List<String> getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(List<String> errMsg) {
		this.errMsg = errMsg;
	}

	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}

	/**
	 * Excel导入后更改Excel的内容
	 * @param sheetName Sheet页名称 为空时抓第一页
	 * @param rowErrInfo 错误信息写入行
	 * @param colErrInfo 错误信息写入列
	 * @throws Exception
	 */
	public File addErrorInfo(MultipartFile file,Map<Long,String> errorInfos,String sheetName,String name,int rowErrInfo, String colErrInfo) throws IOException {

		InputStream input = file.getInputStream();
		FileOutputStream out = null;
		Workbook book = null;
		File temp = new File(file.getOriginalFilename());

		try {
			out = new FileOutputStream(temp);
			book = selectVrsion(name,input);
			Sheet sheet = getSheet(book,sheetName);

			if (sheet == null) {
				errMsg.add(!sheetName.trim().equals("") ? sheetName : "第一页" + "sheet页为空");
				logger.info("sheet页为空");
			} else{
				String maxString = "";

				Row rowTitle;
				rowTitle = sheet.getRow(rowErrInfo-1);
				Cell cellTitle = rowTitle.createCell(getExcelCol(colErrInfo));
				cellTitle.setCellType(XSSFCell.CELL_TYPE_STRING);
				cellTitle.setCellValue("错误信息");
				cellTitle.setCellStyle(setStyle(book,true));

				for(Long errorRow : errorInfos.keySet()){
					Row row;
					row = sheet.getRow(errorRow.intValue());
					Cell cell = row.createCell(getExcelCol(colErrInfo));
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(errorInfos.get(errorRow));
					cell.setCellStyle(setStyle(book,false));
					if(errorInfos.get(errorRow).length() > maxString.length()){
						maxString = errorInfos.get(errorRow);
					}
				}
				sheet.setColumnWidth(this.getExcelCol(colErrInfo),maxString.getBytes().length*256);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			out.flush();
			book.write(out);
			out.close();
			book.close();
			input.close();
		}
		return temp;
	}
	
	/**
	 * 当自定义开始抓取数据的行数时，调用此方法
	 * @param sheetName 为空时抓第一页
	 * @param input InputStream 字节流
	 * @param isHeaderLine 是否是头行结构
	 * @return
	 * @throws Exception
	 * @returnType List<T>
	 */
	public List<List<T>> importExcel(String path, String sheetName, InputStream input,boolean isHeaderLine) throws Exception {
		if(isHeaderLine){
			this.isHeaderLine = isHeaderLine;
		}
		return importExcel(path, sheetName, input,startRow);
	}
	
	/**
	 * 当自定义开始抓取数据的行数时，调用此方法
	 * @param sheetName 为空时抓第一页
	 * @param input InputStream 字节流
	 * @param startRow sheet页中抓取数据开始的行数
	 * @return
	 * @throws Exception
	 * @returnType List<T>
	 */
	public List<List<T>> importExcel(String path, String sheetName, InputStream input,Integer startRow) throws Exception {
		if (startRow != null) {
			this.startRow = startRow;
		}
		return importExcel(path, sheetName, input);
	}

	public int getRealNumberOfCells(Row cells) {
		int count = 0;

		for(int i = 0; i < cells.getLastCellNum(); ++i) {
			if(cells.getCell(i) != null && cells.getCell(i).getCellType() != Cell.CELL_TYPE_BLANK) {
				++count;
			}
		}

		return count;
	}

	/**
	 * Excel数据导入得到List列表
	 * @param sheetName Sheet页名称 为空时抓第一页
	 * @param input 输入流
	 * @throws Exception
	 */
	public List<List<T>> importExcel(String path, String sheetName, InputStream input) throws IOException {
		String className = clazz.getSimpleName();
		List<List<T>> fatherList = new ArrayList<List<T>>();
		List<T> sonList = new ArrayList<T>();
		Workbook book = null;
		int lastRow = 0;
		try {
			book = selectVrsion(path,input);
			Sheet sheet = getSheet(book,sheetName);

			if (sheet == null) {
				errMsg.add(!sheetName.trim().equals("") ? sheetName : "第一页" + "sheet页为空");
				logger.info("sheet页为空");
			}else{
				if(isHeaderLine){
					lastRow = 1;
				}else{
					rowTotal = sheet.getLastRowNum();
					int count = 0;
					//排除掉所有空的行以及所有CELL_TYPE_BLANK的行
					for (int i = startRow; i <= rowTotal; i++) {
						Row cells = sheet.getRow(i);// 得到一行中的所有单元格对象.
						if(cells == null){
							continue;
						}
//						if(cells.getPhysicalNumberOfCells() == 0){
//							continue;
//						}
						if(getRealNumberOfCells(cells) == 0){
							continue;
						}
//						int idxF = cells.getFirstCellNum();
//						int idxL = cells.getLastCellNum();
//						//如果这一行的有效单元格下标以及单元格格式为空白
//						if(idxF == (idxL - 1) && cells.getCell(idxF).getCellType() == Cell.CELL_TYPE_BLANK){
//							continue;
//						}
						count++;
					}
					rowTotal = count + startRow;
					lastRow = rowTotal - 1;
				}
				logger.info("数据总数为"+lastRow);

//				if(rowTotal > 10000){
//				throw new VerifyError();
//					throw new RuntimeException("导入的excel数据总数大于10000条！");
//				}
				if (lastRow > 0) {// 有数据时才处理
					Field[] allFields = clazz.getDeclaredFields();// 得到类的所有field.
					Map<Integer, Field> fieldsMap = new HashMap<>();// 定义一个map用于存放列的序号和field.
					for (Field field : allFields) {
						// 将有注解的field存放到map中.
						if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
							ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
							int col = 0;
							if (ExcelVOAttribute.LAST_COL.equals(attr.column()))
								col = getLastExcelCol(sheet);// 获得列号
							else {
								col = getExcelCol(attr.column());// 获得列号
							}
							field.setAccessible(true);// 设置类的私有字段属性可访问.
							fieldsMap.put(col, field);
						}
					}
					for (int i = startRow; i <= lastRow; i++) {// 从第2行开始取数据,默认第一行是表头.
						Row cells = sheet.getRow(i);// 得到一行中的所有单元格对象.
						T entity = null;
						for(Integer j : fieldsMap.keySet()){
							Cell cell = cells.getCell(j);// 单元格中的内容.
							Field field = fieldsMap.get(j);// 从map中得到对应列的field.
							if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {//判断当存在行但是行内为空数据时
								if(!(field.getName().equals("errorRow") && entity != null)){
									continue;
								}
							}
							entity = (entity == null ? clazz.newInstance() : entity);// 如果不存在实例则新建.
							if (field != null){
								this.setEntity(field, cell, entity, i);
							}
						}
						if (entity != null) {
							sonList.add(entity);
						}
						logger.info("--------------------rowtotal:"+i+"startrow"+startRow+"sonList"+fatherList.size()+"---------------------------");
						if(lastRow-i+startRow+2 < 10000){
							if(i == lastRow){
								fatherList.add(sonList);
								sonList= new ArrayList<T>();
							}
						}else {
							if(sonList.size() == 10000){
								fatherList.add(sonList);
								sonList= new ArrayList<T>();
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} finally {
			book.close();
		}
		return fatherList;
	}

	/**
	 * 导出头行结构excel时调用
	 * @param sheetName 工作表的名称
	 * @param sheetSize 每个sheet中数据的行数,此数值必须小于65536
	 * @param output java输出流
	 * @param packageName 头行包名
	 * @param object 头表Dto
	 * @return
	 * @throws Exception
	 * @returnType List<T>
	 */
	public boolean exportExcel(List<T> list, String sheetName, int sheetSize,Object object, OutputStream output,String packageName) throws IOException {
		if (object != null) {
			this.object = object;
		}
		return exportExcel(list, sheetName, sheetSize,output,packageName);
	}

	/**
	 * excel导出时回写流水号
	 * @param sheetName 工作表的名称
	 * @param sheetSize 每个sheet中数据的行数,此数值必须小于65536
	 * @param output ava输出流
	 * @param packageName 头行包名
	 * @param object 头表Dto
	 * @param isAddNumber 是否回写流水号
	 * @return
	 * @throws Exception
	 * @returnType List<T>
	 */
	public boolean exportExcel(List<T> list, String sheetName, int sheetSize,Object object, OutputStream output,String packageName,boolean isAddNumber) throws IOException {
		if (object != null) {
			this.object = object;
		}
		if (isAddNumber){
			this.isAddNumber = isAddNumber;
		}
		return exportExcel(list, sheetName, sheetSize,output,packageName);
	}
	/**
	 * 对list数据源将其里面的数据导入到excel表单
	 * @param sheetName 工作表的名称
	 * @param sheetSize 每个sheet中数据的行数,此数值必须小于65536
	 * @param output java输出流
	 * @param packageName 头行包名
	 */
	public boolean exportExcel(List<T> list, String sheetName, int sheetSize, OutputStream output,String packageName) throws IOException {
		Field[] allFields = clazz.getDeclaredFields();// 得到所有定义字段
		List<Field> fields = new ArrayList<Field>();
		// 得到所有field并存放到一个list中.
		for (Field field : allFields) {
			if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
				fields.add(field);
			}
		}
		XSSFWorkbook workbook = new XSSFWorkbook();// 产生工作薄对象
		// excel2003中每个sheet中最多有65536行,为避免产生错误所以加这个逻辑.
		if (sheetSize > 65536 || sheetSize < 1) {
			errMsg.add("当前sheet长度超过65536");
			sheetSize = 65536;
		}
		double sheetNo = Math.ceil((double)list.size() / (double)sheetSize);// 取出一共有多少个sheet.
		
		for (int index = 0; index < sheetNo; index++) {
			XSSFSheet sheet = workbook.createSheet();// 产生工作表对象
			workbook.setSheetName(index, sheetName + index);// 设置工作表的名称.
			XSSFRow row;
			XSSFCell cell;// 产生单元格
			
			if(packageName.equals("")){
				row = sheet.createRow(0);// 产生一行				
			}else{
				row = sheet.createRow(2);// 产生一行
			}
				// 写入各个字段的列头名称
			for(Field field : fields){
				ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
				int col = getExcelCol(attr.column());// 获得列号
				cell = row.createCell(col);// 创建列
					// 设置列中写入内容为String类型
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				String name = attr.name();
				cell.setCellValue(name);// 写入列名
				if (attr.ColumnWidth() != null) {
					if(!attr.ColumnWidth().equals("3000")){
						sheet.setColumnWidth(this.getExcelCol(attr.column()), Integer.parseInt(attr.ColumnWidth()));
					}else{
						sheet.setColumnWidth(this.getExcelCol(attr.column()),name.getBytes().length*256);
					}
				}
				if (attr.enabledEditColName() == false) {
					cell.setCellStyle(setStyle(workbook,false));
				}
					// 如果设置了提示信息则鼠标放上去提示.
				if (!attr.prompt().trim().equals("")) {
					setXSSFPrompt(sheet, "", attr.prompt(), 1, 100, col, col);// 这里默认设了2-101列提示.
					cell.setCellStyle(setStyle(workbook,false));
				}
					// 如果设置了combo属性则本列只能选择不能输入
				if (attr.combo().length > 0) {
					setXSSFValidation(sheet, attr.combo(), 1, 100, col, col);// 这里默认设了2-101列只能选择不能输入.
					cell.setCellStyle(setStyle(workbook,false));
				}
				cell.setCellStyle(setStyle(workbook,true));
			}

			int startNo = index * sheetSize;
			int endNo = Math.min(startNo + sheetSize, list.size());
				// 写入各条记录,每条记录对应excel表中的一行
			for (int i = startNo; i < endNo; i++) {
				if(packageName.equals("")){
					row = sheet.createRow(i + 1 - startNo);					
				}else{
					row = sheet.createRow(i + 3 - startNo);
				}
				T vo = (T) list.get(i); // 得到导出对象.
				for(Field field : fields){
					field.setAccessible(true);// 设置实体类私有属性可访问
					ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
						// 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
					if (attr.isExport()) {
						cell = row.createCell(getExcelCol(attr.column()));// 创建cell
						setCellType(field, cell);
						try {
							if (field.get(vo) == null) {
								cell.setCellValue("");
							} else if (Date.class == field.getType()) {
								SimpleDateFormat dateFormat = new SimpleDateFormat(attr.dateFormat());
								cell.setCellValue(dateFormat.format((Date) field.get(vo)));
							} else {
								cell.setCellValue(String.valueOf(field.get(vo)));
							}
						} catch (IllegalAccessException e) {
							errMsg.add("发生反射机制的安全权限异常");
						}
						cell.setCellStyle(setStyle(workbook,false));
					}
				}
			}
			
			if(!packageName.equals("")){
				exportHeaderLine(workbook,sheet,packageName);
			}else if(packageName.equals("frpf.cs.dto.RefundDetailLine")){
				exportHeaderLine(workbook,sheet,packageName);
			}
			if(isAddNumber){
				addNumber(workbook,sheet,list.size(),2,"A");
			}
			if (!(psw == null || psw.trim().equals(""))) {
					// 开启excel保护
				sheet.protectSheet(psw);
			}
		}

		output.flush();
		workbook.write(output);
		output.close();
		workbook.close();

		return true;
	}
	
	/**
	 * 导出头行结构的excel
	 * @param workbook 工作表的名称
	 * @param sheet sheet页
	 * @param packageName 头行包名
	 */
	public boolean exportHeaderLine(XSSFWorkbook workbook,XSSFSheet sheet,String packageName){
		Field[] allHeaderFields;
		List<Field> headerFields = new ArrayList<Field>();
		Class<?> headerClazz = null;
		try {
			headerClazz = Class.forName(packageName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}	
		allHeaderFields = headerClazz.getDeclaredFields();
			// 得到头表所有字段
		for (Field field : allHeaderFields) {
			if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
				headerFields.add(field);
			}
		}

			// 得到所有字段
		Field[] allFields = headerClazz.getDeclaredFields();
			// 得到所有加了excel注解的字段.
		List<Field> fields = new ArrayList<>();
		for (Field field : allFields) {
			if (field.isAnnotationPresent(ExcelVOAttribute.class)) {
				fields.add(field);
			}
		}
		
		XSSFRow rowTitle;
		XSSFCell cell;// 产生单元格
		rowTitle = sheet.createRow(0);// 产生一行
			// 写入各个字段的列头名称
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
			int col = getExcelCol(attr.column());// 获得列号
			cell = rowTitle.createCell(col);// 创建列
				// 设置列中写入内容为String类型
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
			cell.setCellStyle(setStyle(workbook,true));
			String name = attr.name();
			cell.setCellValue(name);// 写入列名
			if (attr.ColumnWidth() != null) {
				if(!attr.ColumnWidth().equals("3000")){
					sheet.setColumnWidth(this.getExcelCol(attr.column()), Integer.parseInt(attr.ColumnWidth()));
				}
				else{
					sheet.setColumnWidth(this.getExcelCol(attr.column()),name.getBytes().length*256);
				}
			}
		}

		XSSFRow row;
		row = sheet.createRow(1);// 产生一行
		for(Field field : fields){
			field.setAccessible(true);// 设置实体类私有属性可访问
			ExcelVOAttribute attr = field.getAnnotation(ExcelVOAttribute.class);
				// 根据ExcelVOAttribute中设置情况决定是否导出,有些情况需要保持为空,希望用户填写这一列.
			if (attr.isExport()) {
				cell = row.createCell(getExcelCol(attr.column()));// 创建cell
				setCellType(field, cell);
				try {
					if (field.get(object) == null) {
						cell.setCellValue("");
					} else if (Date.class == field.getType()) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(attr.dateFormat());
						cell.setCellValue(dateFormat.format((Date) field.get(object)));
					} else {
						cell.setCellValue(String.valueOf(field.get(object)));
					}
				} catch (IllegalAccessException e) {
					errMsg.add("发生反射机制的安全权限异常");
				}
				cell.setCellStyle(setStyle(workbook,false));
			}
		}
		return true;
	}
	
	/**
	 * 导出excel时增加一列流水号
	 * @param workbook 工作表的名称
	 * @param sheet sheet页
	 * @param size 数据条数
	 * @param row 流水号写入的行号
	 * @param col 流水号写入的列号
	 */
	public void addNumber(XSSFWorkbook workbook,XSSFSheet sheet,int size,int row, String col){
		XSSFRow rowTitle;	
		rowTitle = (XSSFRow) sheet.getRow(row);
		XSSFCell cellTitle = rowTitle.createCell(getExcelCol(col));
		cellTitle.setCellType(XSSFCell.CELL_TYPE_STRING);
		cellTitle.setCellValue("商户流水号");
		cellTitle.setCellStyle(setStyle(workbook,true));


		for(int i = 1;i <= size;i++){
			XSSFRow rowNum;				
			rowNum = (XSSFRow) sheet.getRow(i+row);			
			XSSFCell cellNum = rowNum.createCell(getExcelCol(col));
		
			cellNum.setCellType(XSSFCell.CELL_TYPE_STRING);
			cellNum.setCellValue(i);
			cellNum.setCellStyle(setStyle(workbook,false));
		}
	}

	/**
	 * 对每一个cell进行类型定义
	 * @param field 字段
	 * @param cell excel表格单元格(数据来源)
	 * @return cell 设置好的表格单元格
	 */
	protected XSSFCell setCellType(Field field, XSSFCell cell) {
		Class<?> fieldType = field.getType();
		if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if (String.class == fieldType) {
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
			cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
		} else {
			cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		}
		if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			XSSFCellStyle style = cell.getCellStyle();
			style.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
			cell.setCellStyle(style);
		}
		return cell;
	}

	/**
	 * 将表格中的单元格set到实体类中
	 * @param field 字段
	 * @param cell excel表格单元格(数据来源)
	 * @param entity 待填充的实体
	 * @return entity 填充好的实体
	 */
	protected T setEntity(Field field, Cell cell, T entity,Integer errorRow) {

		try {
			if(field.getName().equals("errorRow")){
				field.set(entity,errorRow.longValue());
			}else{
				Class<?> fieldType = field.getType();
				if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String c = cell.getStringCellValue();
					if (null != c && "".equals(c.trim())) {
						field.set(entity, Integer.parseInt(c));
					}
				} else if (String.class == fieldType) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					field.set(entity, cell.getStringCellValue());
				} else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String c = cell.getStringCellValue();
					if (c != null && c.trim() != "") {
						field.set(entity, Long.parseLong(c));
					}
				} else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String c = cell.getStringCellValue();
					if (c != null && c.trim() != "") {
						field.set(entity, Float.parseFloat(c));
					}
				} else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String c = cell.getStringCellValue();
					if (c != null && c.trim() != "") {
						field.set(entity, Short.parseShort(c));
					}
				} else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
					cell.setCellType(Cell.CELL_TYPE_STRING);
					String c = cell.getStringCellValue();
					if (c != null && c.trim() != "") {
						field.set(entity, Double.parseDouble(c));
					}
				} else if (Character.TYPE == fieldType) {
					String c = cell.getStringCellValue();
					if ((c != null) && (c.length() > 0)) {
						field.set(entity, Character.valueOf(c.charAt(0)));
					}
				} else if (Date.class == fieldType) {
					if (cell != null) {
						try {
							field.set(entity, cell.getDateCellValue());
						}catch(IllegalStateException e){
							logger.warn("Excel中输入的时间格式错误");
							field.set(entity,new Date(0L));
						}
					}
				} else if (BigDecimal.class == fieldType) {
					if (cell != null) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						if (cell.getStringCellValue() != null && cell.getStringCellValue().trim() != "") {
							field.set(entity, new BigDecimal(cell.getStringCellValue()));
						}
					}
				} else if(boolean.class == fieldType){
					String test = cell.getStringCellValue();
					field.set(entity, test.equals("是")?true:false);
				} else if(Boolean.class == fieldType){
					String test = cell.getStringCellValue();
					field.set(entity, test.equals("是")?true:false);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entity;
	}

	/**
	 * 将EXCEL中A,B,C,D,E列映射成0,1,2,3
	 * @param col 列
	 */
	public int getExcelCol(String col) {

		col = col.toUpperCase();
			// 从-1开始计算,字母重1开始运算。这种总数下来算数正好相同。
		int count = -1;
		char[] cs = col.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			count += (cs[i] - 64) * Math.pow(26, cs.length - 1 - i);
		}
		return count;
	}

	/**
	 * 获得当前sheet页中最后列(此处是采用第一行作为标题的最后一个)
	 * @param sheet
	 * @return
	 */
	public int getLastExcelCol(Sheet sheet) {
		return sheet.getRow(0).getLastCellNum() - 1;
	}

	/**
	 * 设置单元格上提示
	 * @param sheet 要设置的sheet.
	 * @param promptTitle 标题
	 * @param promptContent 内容
	 * @param firstRow 开始行
	 * @param endRow 结束行
	 * @param firstCol 开始列
	 * @param endCol 结束列
	 * @return 设置好的sheet.
	 */
	public XSSFSheet setXSSFPrompt(XSSFSheet sheet, String promptTitle, String promptContent, int firstRow, int endRow,int firstCol, int endCol) {
		XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
		DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
			// 四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
			// 数据有效性对象
		XSSFDataValidation data_validation_view = (XSSFDataValidation) helper.createValidation(constraint, regions);
		data_validation_view.createPromptBox(promptTitle, promptContent);
		sheet.addValidationData(data_validation_view);
		return sheet;
	}
 
	/**
	 * 设置某些列的值只能输入预制的数据,显示下拉框.
	 * @param sheet 要设置的sheet.
	 * @param textlist 下拉框显示的内容
	 * @param firstRow 开始行
	 * @param endRow 结束行
	 * @param firstCol 开始列
	 * @param endCol 结束列
	 * @return 设置好的sheet.
	 */ 
	public XSSFSheet setXSSFValidation(XSSFSheet sheet, String[] textlist, int firstRow, int endRow, int firstCol,
			int endCol) {
			// 加载下拉列表内容
		XSSFDataValidationHelper helper = new XSSFDataValidationHelper(sheet);
		DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
			// 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
		CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
			// 数据有效性对象
		XSSFDataValidation data_validation_list = (XSSFDataValidation) helper.createValidation(constraint, regions);
		sheet.addValidationData(data_validation_list);
		return sheet;
	}

	/**
	 * 用于判断Excel是03的还是07的
	 * @author jianping.huo@hand-china.com
	 * @date 2017/1/10 16:32
	 * @param filePath Excel完整路径
	 * @return
	 */
	public boolean isExcel2003(String filePath)
	{
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	/**
	 * 用于判断Excel是03的还是07的
	 * @author jianping.huo@hand-china.com
	 * @date 2017/1/10 16:32
	 * @param filePath Excel完整路径
	 * @return
	 */
	public boolean isExcel2007(String filePath)
	{
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}	
	
	/**
	 * 设置Excel单元格风格
	 * @param book 为空时抓第一页
	 * @param cellStyle true返回标题风格，false返回普通风格
	 * @return
	 * @returnType CellStyle
	 */
	public CellStyle setStyle(Workbook book, boolean cellStyle){
		CellStyle rowStyle = book.createCellStyle();
		Font fontStyle = book.createFont();
/*		rowStyle.setBorderTop(CellStyle.BORDER_THIN);
		rowStyle.setBorderLeft(CellStyle.BORDER_THIN);
		rowStyle.setBorderRight(CellStyle.BORDER_THIN);
		rowStyle.setBorderBottom(CellStyle.BORDER_THIN);*/
		rowStyle.setAlignment(CellStyle.ALIGN_CENTER);//居中对其
		fontStyle.setFontHeightInPoints((short)11);//设置字体大小
		rowStyle.setLocked(false);//是否只读
		if(cellStyle){
			fontStyle.setFontHeightInPoints((short)12);//设置字体大小
			fontStyle.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
			rowStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());//设置背景色
			rowStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);//填充图案
		}
		rowStyle.setFont(fontStyle);
		return rowStyle;
	}
	
	/**
	 * 判断excel文件的版本
	 * @param path 文件名
	 * @param input 文件输入流
	 * @return
	 * @throws IOException 
	 * @returnType Workbook
	 */
	public Workbook selectVrsion(String path, InputStream input) throws IOException{
		Workbook book = null;
		if (isExcel2003(path)){
			book = new HSSFWorkbook(input);
		}else if (isExcel2007(path)){
			book = new XSSFWorkbook(input);
		}
		return book;
	}
	
	/**
	 * 获得excel的sheet页
	 * @param book excel对象
	 * @param sheetName sheet页名字
	 * @return
	 * @returnType Sheet
	 */
	public Sheet getSheet(Workbook book, String sheetName){
		Sheet sheet = null;
		if (!sheetName.trim().equals("")) {
			sheet = book.getSheet(sheetName);// 如果指定sheet名,则取指定sheet中的内容.
		} else {
			sheet = book.getSheetAt(0);// 如果传入的sheet名不存在则默认指向第1个sheet.
		}
		return sheet;
	}
}
