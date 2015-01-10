package com.excelsecu.ian.axml;

public class AXmlTranslate {
	public static void main(String argv) {
		System.out.println(translate("id", "abc"));
	}
	
	public static String translate(String attrName , String attributeValue) {
		String methodName = transAttrToMethod(attrName);
		return methodName + "(" + attributeValue + ")";
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
	
	public static String trimTheParam(String method) {
		return method.substring(0, method.indexOf("("));
	}
}