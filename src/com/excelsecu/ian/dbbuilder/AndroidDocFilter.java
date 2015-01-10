package com.excelsecu.ian.dbbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class AndroidDocFilter {
	private static String ENCODE = "UTF8";
	
	public static void main(String[] argc) {
		String path = "D:/adt-bundle-windows-x86_64-20140702/sdk/docs/reference/android/view/View.html";
		String docContent = readDoc(path);
		HashMap<String, String> attrList = filter(docContent);
		Iterator<Entry<String, String>> iter = attrList.entrySet().iterator(); 
		while (iter.hasNext()) {
		    Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next(); 
		    String key = (String) entry.getKey(); 
		    Object value = (String) entry.getValue(); 
		    System.out.println(key + "\t\t" + value);
		}
	}
	
    public static String readDoc(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), ENCODE));
            String content = "";
            String buf;
            
            boolean start = false;
            while ((buf = reader.readLine())!= null) {
            	//all we need is <table id="lattrs"> ... </table>, the XML Attributes
            	if (buf.contains("<table id=\"lattrs\"")) {
            		start = true;
            	}
            	if (start) {
                	buf = buf.trim();
                	//remove useless line
                	if (buf.isEmpty() || buf.equals("\r"))
                		continue;
                	//XML Attributes table end, stupid Android doc source HTML don't have </table> here
            		if (buf.contains("<table")) {
                    	//System.out.println(content);
            			break;
            		}
            		content += buf + "\n";
            	}
            }
            reader.close();
            return content;
        }
        catch( Exception e ) {
        	e.printStackTrace();
            return "";
        }
    }
	
	public static HashMap<String, String> filter(String content) {
        try {
            Parser parser = Parser.createParser(content, ENCODE);
            AndFilter filter =   
                    new AndFilter(new TagNameFilter("tr"),new HasAttributeFilter("id","lattrs")); 
            NodeList nodeList = parser.parse(filter);
            System.out.println("---------------------------------------------------------");
            //System.out.println(nodeList.asString());
			return new HashMap<String, String>();
		} catch (ParserException e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		}
	}
}
