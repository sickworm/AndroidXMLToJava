package com.excelsecu.axml.dbbuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Filter {
    private Class<?> type;
    
	public static void main(String[] argc) {
		String path = Config.VIEW_PATH;
		Filter docFilter = new Filter(android.view.View.class);
		HashMap<String, String> attrToMethodList = docFilter.filterDoc(path);
		Iterator<Entry<String, String>> iter = attrToMethodList.entrySet().iterator();
		while (iter.hasNext()) {
		    Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
		    String key = (String) entry.getKey();
		    Object value = (String) entry.getValue();
		    System.out.println(key + "\n\t" + value + "\n");
		}
	}
	
	public Filter(Class<?> type) {
	    this.type = type;
	}
	
	public HashMap<String, String> filterDoc(String fileName) {
        String docContent = readDoc(fileName);
        HashMap<String, String> attrToMethodList = filter(docContent);
        return attrToMethodList;
	}
	
    private String readDoc(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), Config.ENCODE));
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
                	if (buf.isEmpty())
                		continue;
                	//XML Attributes table end, stupid Android doc source HTML don't have </table> here
            		if (buf.contains("<table") && !content.equals("")) {      //avoid "<table id=\"lattrs\""
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
	
    private HashMap<String, String> filter(String content) {
        try {
        	HashMap<String, String> attrMap = new HashMap<String, String>();
            Parser parser = Parser.createParser(content, Config.ENCODE);
            AndFilter andFilter1 =
                    new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("class","alt-color api apilevel-"));
            AndFilter andFilter2 =
                    //kill me, the " api apilevel-" has a space at the start
                    new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("class"," api apilevel-"));
            OrFilter orFilter = new OrFilter(andFilter1, andFilter2);
            NodeList tableNodeList = parser.parse(orFilter);
            NodeIterator tableIt = tableNodeList.elements();
            while(tableIt.hasMoreNodes()) {
            	Node trNode = tableIt.nextNode();
            	NodeList trNodeList = trNode.getChildren();
            	/**
            	 * ***** trNodeList example *****
            	 *	Txt (268[6,37],269[7,0]): \nTag (269[7,0],292[7,23]): td class="jd-linkcol"
				 *	  Tag (292[7,23],381[7,112]): a href="../../../reference/android/view/View.html...
				 *	    Txt (381[7,112],412[7,143]): android:accessibilityLiveRegion
				 *	    End (412[7,143],416[7,147]): /a
				 *	  End (416[7,147],421[7,152]): /td
				 *	Txt (421[7,152],422[8,0]): \nTag (422[8,0],445[8,23]): td class="jd-linkcol"
				 *	  Txt (445[8,23],446[9,0]): \n
				 *	  Tag (446[9,0],530[9,84]): a href="../../../reference/android/view/View.html#s...
				 *	    Txt (530[9,84],561[9,115]): setAccessibilityLiveRegion(int)
				 *	    End (561[9,115],565[9,119]): /a
				 *	  Txt (565[9,119],566[10,0]): \n
				 *	  End (566[10,0],571[10,5]): /td
				 *	Txt (571[10,5],572[11,0]): \nTag (572[11,0],609[11,37]): td class="jd-descrcol" width="100%"
				 *	  Txt (609[11,37],712[14,0]): \nIndicates to accessibility services whether the...
				 *	  End (712[14,0],717[14,5]): /td
				 *	Txt (717[14,5],718[15,0]): \n
				 * ***** trNodeList example *****
            	 */
                if (trNodeList.size() != 7) {
                	throw new AndroidDocException(AndroidDocException.ATM_FORMAT_ERROR);
                }
                String className = type.getSimpleName();
                String attr = trNodeList.elementAt(1).toPlainTextString();
                attr = attr.replace("\n", "");
                String method = trNodeList.elementAt(3).toPlainTextString();
                method = method.replace("\n", "");
                attrMap.put(className + "$" + attr, method);
                //System.out.println(attr + " " + method);
            }
			return attrMap;
		} catch (ParserException e) {
        	throw new AndroidDocException(AndroidDocException.AXML_FORMAT_ERROR);
		}
	}
}
