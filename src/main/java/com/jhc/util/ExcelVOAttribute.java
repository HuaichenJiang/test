package com.jhc.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/*
 * 注解实现类
 */
@Retention(RetentionPolicy.RUNTIME)  
@Target( { java.lang.annotation.ElementType.FIELD }) 
public @interface ExcelVOAttribute {
	public static final String LAST_COL = "last_col";
	/**
	 * 导出到Excel中的名字.
	 */
	public abstract String name();
	/**
	 * 导出到Excel中的列宽度
	 */
	public abstract String ColumnWidth() default "3000";
	/**
	 * 导出到列的时间格式
	 * update 0925 by zhongwen.liu  hh-->HH
	 */
	public abstract String dateFormat() default "yyyy/MM/dd HH:mm:ss";

	/**
	 * 配置列的名称,对应A,B,C,D....
	 */
	public abstract String column();

	/**
	 * 提示信息
	 */
	public abstract String prompt() default "";

	/**
	 * 设置只能选择不能输入的列内容.
	 */
	public abstract String[] combo() default {};

	/**
	 * 是否导出数据,应对需求:有时我们需要导出一份模板,这是标题需要但内容需要用户手工填写.
	 */
	public abstract boolean isExport() default true;

	/**
	 * 是否可编辑,默认可编辑.
	 */
	public abstract boolean enabledEdit() default true;

	/**
	 * 是否可编辑列名,默认不可编辑.
	 */
	public abstract boolean enabledEditColName() default false;
}
