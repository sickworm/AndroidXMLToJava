package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.excelsecu.axml.dbbuilder.AndroidDocConverter;

/**
 * Translate a XML attritube to a Java method.
 * @author ch
 *
 */
public class AXMLTranslater {
    private static AXMLTranslater translater = null;
    private HashMap<String, String> map = null;
    /** record of {@link AXMLTranslater#extraMethod(String attrName , String attrValue)} **/
    private boolean scale = false;
    private String extraMethod = "";
    private List<String> idList = new ArrayList<String>();
    
	public static void main(String[] argv) {
	    String attrName = "android:id";
        String attrValue = "abc";
        String method;
        method = AXMLTranslater.getInstance().translate(attrName, attrValue);
		System.out.println(method);
	}
	
	public AXMLTranslater() {
	    if (map == null) {
	        map = AndroidDocConverter.getMap();
	    }
	}
	//AXMLConverter works. But too many attributes don't have related methods.
	public static AXMLTranslater getInstance() {
	    if (translater == null) {
	        translater = new AXMLTranslater();
	    }
	    return translater;
	}
	
	/**
	 * Translate a XML attritube to a Java method.
	 * @param attrName the name of attribute
	 * @param attrValue the value of attribute
	 * @return
	 */
	public String translate(String attrName , String attrValue) {
        String method = "";
        try {
            String methodName = transAttrToMethod(attrName);
            String methodValue = translateValue(attrValue);
            extraMethod(attrName, attrValue);
            method = methodName + "(" + methodValue + ")";
        } catch (AXMLException e) {
            return "//" + attrName + "=\"" + attrValue + "\"";
        }
        return method;
	}
	
	/**
	 * Translate XML element's attribute to Android method without parameters.
	 * @param attrName	The name of attribute.
	 * @return Android method matches the attribute without parameters.
	 */
	private String transAttrToMethod(String attrName) {
		String method = matchMap(attrName);
		return trimTheParam(method);
	}
	
	/**
	 * Find the conversion between XML attribute and Java method in the match map.
	 * @param attrName
	 * @return the method matches attribute or throws AXMLException
	 */
	private String matchMap(String attrName) {
        //current version don't support database, use HashMap instead
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
	private String trimTheParam(String method) {
		return method.substring(0, method.indexOf("("));
	}
	
	private String translateValue(String value) {
	    //dp, px, sp
	    //not strict enough, should check attrName both
	    if (value.matches("[0-9]+dp")) {
            value = value.substring(0, value.length() - 2);
            value = value + " / scale + 0.5f";
	    } else if (value.matches("[0-9]+sp")) {
	        value = value.substring(0, value.length() - 2);
	    } else if (value.matches("[0-9]+px")) {
            value = value.substring(0, value.length() - 2);
	    }
	    
	    if (value.contains("@+id/") || value.contains("@id/")) {
	        value = value.substring(value.indexOf('/') + 1);
	        value = "R.id." + value;
	    } else if (value.contains("@string/")) {
	        value = value.substring(value.indexOf('/') + 1);
            value = "R.id." + value;
	        value = "getResources().getString(" + value + ")";
        }
        return value;
	}
	
	/**
	 * Find out what extra Java method have to add.
	 * @param attrName
	 * @param attrValue
	 */
	private void extraMethod(String attrName , String attrValue) {
        if (attrValue.contains("dp")) {
            if (!scale) {
                scale = true;
                extraMethod += "final float scale = context.getResources().getDisplayMetrics().density;\n";
            }
        } else if (attrValue.contains("@+id/")) {
            idList.add(attrValue.substring(attrValue.indexOf('/') + 1));
        }
	}
    
    public String getExtraMethod() {
        return extraMethod;
    }
    
    public List<String> getIdList() {
        return idList;
    }
}