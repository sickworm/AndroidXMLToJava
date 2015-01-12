package com.excelsecu.axml;

import java.util.HashMap;

import com.excelsecu.axml.dbbuilder.AndroidDocConverter;

/**
 * Translate a XML attritube to a java method.
 * @author ch
 *
 */
public class AXmlTranslate {
    private static HashMap<String, String> map;
    
	public static void main(String[] argv) {
	    String attrName = "android:id";
        String attrValue = "abc";
        String method;
        try {
            method = translate(attrName, attrValue);
        } catch (AXmlException e) {
            method = "//" + attrName + "=\"" + attrValue + "\"";
        }
		System.out.println(method);
	}
	
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
		String method = matchList(attrName);
		return trimTheParam(method);
	}
	
	private static String matchList(String attrName) {
        //current version don't have database, use HashMap instead
        //return AXmlDatabase.find("");
	    if (!map.containsKey(attrName)) {
	        throw new AXmlException(AXmlException.METHOD_NOT_FOUND);
	    }
	    String methodName = map.get(attrName);
	    if (methodName.equals("") || methodName.equals(null)) {
            throw new AXmlException(AXmlException.METHOD_NOT_FOUND);
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