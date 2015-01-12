package com.excelsecu.axml;

/**
 * Translate a XML attritube to a java method.
 * @author ch
 *
 */
public class AXmlTranslate {
	public static void main(String argv) {
		System.out.println(translate("android:id", "abc"));
	}
	
	public static String translate(String attrName , String... attrValue) {
		String methodName = transAttrToMethod(attrName);
		return methodName + "(" + attrValue + ")";
	}
	
	/**
	 * Translate XML element's attribute to Android method without parameters.
	 * @param attrName	The name of attribute.
	 * @return Android method matches the attribute without parameters.
	 */
	public static String transAttrToMethod(String attrName) {
		String method = matchList(attrName);
		return trimTheParam(method);
	}
	
	private static String matchList(String attrName) {
		return AXmlDatabase.find("");
	}
	
	/**
	 * Remove the method param, keep the method name
	 * @param method
	 * @return
	 */
	public static String trimTheParam(String method) {
		return method.substring(0, method.indexOf("("));
	}
}