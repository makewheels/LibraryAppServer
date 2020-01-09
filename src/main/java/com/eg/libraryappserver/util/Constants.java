package com.eg.libraryappserver.util;

/**
 * 常量
 * 
 * @author Administrator
 *
 */
public class Constants {
	// 字符集
	public static String CHARSET = "utf-8";
	// 等待时间
	public static long WAIT_TIME_MILLIS = 7000;
	// 超时时间
	public static int TIME_OUT_MILLIS = 15000;
	// 重试次数
	public static int RETRY_TIMES = 3;
	// 多线程数量
	public static int THREAD_AMOUNT = 4;
	// 最大并发任务数量
	public static long MAX_MISSION_AMOUNT = 200;
	// 本地资源根目录
	public static String RESOURCES_BASE_PATH = "D:\\workSpace\\sts\\BookPosition\\src\\main\\resources";
	// 外网
	public static String BASE_URL = "http://60.218.184.234:8091";
	// 内网查询图书定位
	public static String BASE_URL_INTERNAL_QUERY_LOCATION = "http://10.0.15.12/TSDW/GotoFlash.aspx?szBarCode=";
	// 外网图书简单详情，返回html
	public static String SIMPLE_INFO_URL_START = BASE_URL + "/opac/book/";
	public static String SIMPLE_INFO_URL_END = "?view=simple";
}
