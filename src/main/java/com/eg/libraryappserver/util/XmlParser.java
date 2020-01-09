package com.eg.libraryappserver.util;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;

/**
 * xml解析器
 * 
 * @author Administrator
 *
 */
public class XmlParser {

	/**
	 * 返回根节点
	 * 
	 * @param file
	 * @return
	 */
	public static Element parseFile(File file) {
		try {
			return new SAXReader().read(file).getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 返回根节点
	 * 
	 * @param xml
	 * @return
	 */
	public static Element parseText(String xml) {
		try {
			return DocumentHelper.parseText(xml).getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}

}
