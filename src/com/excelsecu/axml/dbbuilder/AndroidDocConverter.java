package com.excelsecu.axml.dbbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * In current test version, this program will run everytime to generate the table.
 * @author ch
 *
 */
public class AndroidDocConverter {
    public static HashMap<String, String> attrToMethodMap = new HashMap<String, String>();
    
	public static void main(String[] argv) {
		List<String> listPage = listPage();
		for (String path: listPage) {
		    System.out.println(path);
		    HashMap<String, String> sublist = AndroidDocFilter.filterDoc(path);
		    Iterator<Entry<String, String>> iter = sublist.entrySet().iterator(); 
	            while (iter.hasNext()) {
	                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
	                String key = (String) entry.getKey();
	                Object value = (String) entry.getValue();
	                System.out.println(key + "\n\t" + value + "\n");
	            }
            System.out.println("");
            attrToMethodMap.putAll(sublist);
		}
	}
	
	public static HashMap<String, String> getMap() {
	    if (attrToMethodMap.size() == 0) {
	        List<String> listPage = listPage();
	        for (String path: listPage) {
	            HashMap<String, String> sublist = AndroidDocFilter.filterDoc(path);
	            attrToMethodMap.putAll(sublist);
	        }
	    }
        return attrToMethodMap;
    }
	
	/**
	 * Include all the html path which has xml attribute to java method table.
	 * @return The list of XML attribute to Java method table.
	 */
	public static List<String> listPage() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < AndroidDocConfig.CLASSES_LIST.length; i++) {
		    String name = AndroidDocConfig.CLASSES_LIST[i].getName();
		    name = name.replace('.', '/');
		    name = AndroidDocConfig.BASE_PATH + name + ".html";
	        list.add(name);
		}
		return list;
	}
}
