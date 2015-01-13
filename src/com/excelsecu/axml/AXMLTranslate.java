package com.excelsecu.axml;

import java.util.HashMap;

import com.excelsecu.axml.dbbuilder.AndroidDocConverter;

/**
 * Translate a XML attritube to a Java method.
 * @author ch
 *
 */
public class AXMLTranslate {
    private static HashMap<String, String> map;
    
	public static void main(String[] argv) {
	    String attrName = "android:id";
        String attrValue = "abc";
        String method;
        try {
            method = translate(attrName, attrValue);
        } catch (AXMLException e) {
            method = "//" + attrName + "=\"" + attrValue + "\"";
        }
		System.out.println(method);
	}
	
	/**
	 * Translate a XML attritube to a Java method.
	 * @param attrName the name of attribute
	 * @param attrValue the value of attribute
	 * @return
	 */
	public static String translate(String attrName , String... attrValue) {
        map = AndroidDocConverter.getMap();
        String methodName = transAttrToMethod(attrName);
		return methodName + "(" + attrValue[0] + ")";
	}
	
	/**
	 * Translate XML element's attribute to Android method without parameters.
	 * @param attrName	The name of attribute.
	 * @return Android method matches the attribute without parameters.
	 */
	public static String transAttrToMethod(String attrName) {
		String method = matchMap(attrName);
		return trimTheParam(method);
	}
	
	/**
	 * Find the conversion between XML attribute and Java method in the match map.
	 * @param attrName
	 * @return
	 */
	private static String matchMap(String attrName) {
        //current version don't have database, use HashMap instead
        //return AXMLDatabase.find("");
	    if (!map.containsKey(attrName)) {
	        throw new AXMLException(AXMLException.METHOD_NOT_FOUND);
	    }
	    String methodName = map.get(attrName);
	    if (methodName.equals("") || methodName.equals(null)) {
            throw new AXMLException(AXMLException.METHOD_NOT_FOUND);
	    }
	    return methodName;
	}
	
	/**
	 * Remove the method paramaters, keep the method name
	 * @param method
	 * @return
	 */
	public static String trimTheParam(String method) {
		return method.substring(0, method.indexOf("("));
	}
}