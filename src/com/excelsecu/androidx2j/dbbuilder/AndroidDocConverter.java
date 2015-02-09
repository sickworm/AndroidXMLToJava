package com.excelsecu.androidx2j.dbbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.excelsecu.androidx2j.AX2JNode;
import com.excelsecu.androidx2j.AX2JParser;
import com.excelsecu.androidx2j.AX2JStyle;

/**
 * Convert off-line Android Doc in SDK manager to the conversion table(HashMap<String, String>).
 * In current test version, this program will run every time to generate the HashMap.<p>
 * The table will storage like "View$set.orientation=setOrientation(int)".
 * 
 * @author ch
 *
 */
public class AndroidDocConverter {
    private static HashMap<String, String> attrToMethodMap = new HashMap<String, String>();
    private static HashMap<String, AX2JStyle> systemStylesMap = new HashMap<String, AX2JStyle>();
    private static HashMap<String, AX2JStyle> systemThemesMap = new HashMap<String, AX2JStyle>();
    
	public static void main(String[] argv) throws DocumentException {
        System.out.println("Prasering Android documents...\n");
        
		String[] listPage = listPage();
        for (int i = 0; i < listPage.length; i++) {
            String path = listPage[i];
		    System.out.println(path + "\n");
		    HashMap<String, String> sublist = new Filter(Config.CLASSES_LIST[i]).filterDoc(path);
		    //remove the empty method, some methods were shown in super class but were not shown in this class. e.g. ProgressBar$minWidth
            List<String> removelist = new ArrayList<String>();
		    Iterator<Entry<String, String>> iter = sublist.entrySet().iterator();
	            while (iter.hasNext()) {
	                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
	                String key = (String) entry.getKey();
	                String value = (String) entry.getValue();
	                if (value.equals("")) {
	                    removelist.add(key);
	                } else {
	                    System.out.println(key + "\n\t" + value + "\n");
	                }
	            }
	            for (String key : removelist) {
	                sublist.remove(key);
	            }
            System.out.println("");
            attrToMethodMap.putAll(sublist);
		}
        
        //some attributes don't shown in Android doc (like setEnabled), add them in here.
        attrToMethodMap.putAll(Config.ADDITION_MAP);
        Iterator<Entry<String, String>> iter = Config.ADDITION_MAP.entrySet().iterator();
        System.out.println("Additional\n");
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            System.out.println(key + "\n\t" + value + "\n");
        }
        
        //some attributes can't translate directly (like padding), remove them in here.
        for (String removal : Config.REMOVAL_LIST) {
            attrToMethodMap.remove(removal);
        }

        System.out.println("Prasering system styles XML...\n");
        buildSystem(Config.SYSTEM_STYLES_PATH, systemStylesMap);
        
        System.out.println("Prasering system themes XML...\n");
        buildSystem(Config.SYSTEM_THEMES_PATH, systemThemesMap);
        
        System.out.println("Generating data.dat...\n");
        generateDat();
        System.out.println("Done!");
	}
	
	private static void buildSystem(String path, HashMap<String, AX2JStyle> map) throws DocumentException {
	    Document document;
        document = new SAXReader().read(path).getDocument();
        AX2JNode systemStylesNode = new AX2JParser(document.getRootElement()).parse();
        for (AX2JNode n : systemStylesNode.getChildren()) {
            if (n.getLabelName().equals("style")) {
                AX2JStyle style = AX2JStyle.buildNode(n);
                if (style == null) {
                    System.out.println("Parse style failed. XML:" + n.asXML());
                }
                if (!(style.parent.equals("") || style.parent.startsWith("android:"))) {
                    style.parent = "android:" + style.parent;
                }
                //some items don't add "android" package in value, add for recognition
                List<Attribute> attrList = style.attrList;
                for (Attribute a : attrList) {
                    String value = a.getValue();
                    if (value.matches("@\\w+/\\w+")) {
                        a.setValue(value.replaceFirst("@", "@android:"));
                    } else if (value.matches("\\?\\w+")) {
                        a.setValue(value.replaceFirst("\\?", "\\?android:attr/"));
                    }
                    if (value.contains("\n")) {
                        a.setValue(value.replace("\n", ""));
                    }
                }
                map.put(style.name, style);
            }
        }
	}
	
	private static void generateDat() {
	    File dat = new File(Config.DAT_PATH);
	    if (dat.isFile()) {
	        dat.delete();
	    }
	    
	    writeFile(dat.getPath(), Config.DAT_COMMENT + "\n", false);
	    
	    String mapString = attrToMethodMap.toString();
	    mapString = mapString.replace("{", Config.DAT_BLOCK + "\n");
	    mapString = mapString.replace("}", "\n" + Config.DAT_BLOCK);
	    mapString = mapString.replace(" ", "");
	    writeFile(dat.getPath(), mapString + "\n\n", true);
	    
	    Iterator<Entry<String, AX2JStyle>> styleIterator = systemStylesMap.entrySet().iterator();
	    StringBuffer styleString = new StringBuffer();
	    while (styleIterator.hasNext()) {
	    	AX2JStyle style = styleIterator.next().getValue();
	    	styleString.append(style.toString() + "\n");
	    }
	    styleString.insert(0, Config.STYLE_BLOCK + "\n");
	    styleString.insert(styleString.length(), Config.STYLE_BLOCK);
	    writeFile(dat.getPath(), styleString + "\n\n", true);
	    
	    Iterator<Entry<String, AX2JStyle>> themeIterator = systemThemesMap.entrySet().iterator();
	    StringBuffer themeString = new StringBuffer();
	    while (themeIterator.hasNext()) {
	    	AX2JStyle theme = themeIterator.next().getValue();
	    	themeString.append(theme.toString() + "\n");
	    }
	    themeString.insert(0, Config.THEME_BLOCK + "\n");
	    themeString.insert(themeString.length(), Config.THEME_BLOCK);
	    writeFile(dat.getPath(), themeString.toString(), true);
	}
	
	public static HashMap<String, String> getMap() {
        if (attrToMethodMap.size() == 0) {
            File dat = new File(Config.DAT_PATH);
            if (!dat.isFile()) {
                throw new AndroidDocException(AndroidDocException.DAT_READ_ERROR);
            }
            
            String content = readFile(dat.getPath(), Config.DAT_BLOCK);
            String[] list = content.split(",");
            for (String s : list) {
                String[] split = s.split("=");
                if (split.length == 1) {
                	attrToMethodMap.put(split[0], "");
                } else {
                	attrToMethodMap.put(split[0], split[1]);
                }
            }
        }
        
        return attrToMethodMap;
	}
	
	public static HashMap<String, AX2JStyle> getSystemStyles() {
		return getSystem(systemStylesMap, Config.STYLE_BLOCK);
	}
	
	public static HashMap<String, AX2JStyle> getSystemThemes() {
		return getSystem(systemThemesMap, Config.THEME_BLOCK);
	}
	
	public static HashMap<String, AX2JStyle> getSystem(HashMap<String, AX2JStyle> map, String block) {
        if (map.size() == 0) {
            File dat = new File(Config.DAT_PATH);
            if (!dat.isFile()) {
                throw new AndroidDocException(AndroidDocException.DAT_READ_ERROR);
            }
            
            Document document = DocumentHelper.createDocument();
            String content = readFile(dat.getPath(), block);
            String[] stylesString = content.split("\n");
            for (String styleString : stylesString) {
            	int index1 = styleString.indexOf(',');
            	int index2 = styleString.indexOf(',', index1 + 1);
            	String name = styleString.substring(0, index1);
            	String parent = styleString.substring(index1 + 1, index2);
            	String attrs = styleString.substring(index2 + 1);
            	
                Element element = document.addElement("container");
            	if (!attrs.equals("")) {
                	String[] attrsArray = attrs.split(",");
                    for (int i = 0; i < attrsArray.length; i++) {
                    	String attrName = attrsArray[i].substring(0, attrsArray[i].indexOf('='));
                    	String attrValue = attrsArray[i].substring(attrsArray[i].indexOf('=') + 1);
                    	attrValue = attrValue.substring(1, attrValue.length() - 1);
                        element.addAttribute(attrName, attrValue);
                    }
            	}
                @SuppressWarnings("unchecked")
				AX2JStyle theme = new AX2JStyle(name, parent, element.attributes());
                map.put(name, theme);
                document.remove(element);
            }
        }
        
        return map;
	}
	
	/**
	 * Include all the HTML path which has XML attribute to java method table.
	 * @return The list of XML attribute to Java method table.
	 */
	public static String[] listPage() {
	    String[] list = new String[Config.CLASSES_LIST.length];
		for (int i = 0; i < Config.CLASSES_LIST.length; i++) {
		    String name = Config.CLASSES_LIST[i].getName();
		    name = name.replace('.', '/');
		    name = Config.ANDROID_DOCS_PATH + name + ".html";
	        list[i] = name;
		}
		return list;
	}
	
	/**
     * Write a file
     * @param fileName
     * @param content
     * @return true if success, return false if error occurs
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath), append), Config.ENCODE));
            writer.write(content);
            writer.close();
            return true;
        }
        catch( Exception e ) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Read a file between the specific block
     * @param filePath
     * @param block
     * @return content of file between the block, return "" if error occurs
     */
    public static String readFile(String filePath, String block) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), Config.ENCODE));
            String content = "";
            String buf;
            
            boolean start = false;
            while ((buf = reader.readLine())!= null) {
                if (buf.equals(block)) {
                    if (start) {
                        break;
                    } else {
                        start = true;
                        continue;
                    }
                }
                if (start) {
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
}