package com.excelsecu.androidx2j.dbbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
    private static Element systemStyles = null;
    private static Element systemThemes = null;
    
	public static void main(String[] argv) throws DocumentException {
		String[] listPage = listPage();
        for (int i = 0; i < listPage.length; i++) {
            String path = listPage[i];
		    System.out.println(path + "\n");
		    HashMap<String, String> sublist = new Filter(Config.CLASSES_LIST[i]).filterDoc(path);
		    Iterator<Entry<String, String>> iter = sublist.entrySet().iterator(); 
	            while (iter.hasNext()) {
	                Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
	                String key = (String) entry.getKey();
	                String value = (String) entry.getValue();
	                System.out.println(key + "\n\t" + value + "\n");
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
        
        Document document;
        document = new SAXReader().read(Config.SYSTEM_STYLES_PATH).getDocument();
        systemStyles = document.getRootElement();
        document = new SAXReader().read(Config.SYSTEM_THEMES_PATH).getDocument();
        systemThemes = document.getRootElement();
        
        System.out.println("Generating data.dat...\n");
        generateDat();
        System.out.println("Done!");
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
	    writeFile(dat.getPath(), mapString + "\n\n", true);
	    
	    String styleString = systemStyles.asXML();
	    styleString = Config.STYLE_BLOCK + "\n" + styleString + "\n" + Config.STYLE_BLOCK;
	    writeFile(dat.getPath(), styleString + "\n\n", true);

	    String themeString = systemThemes.asXML();
	    themeString = Config.THEME_BLOCK + "\n" + themeString + "\n" + Config.THEME_BLOCK;
	    writeFile(dat.getPath(), themeString, true);
	}
	
	public static HashMap<String, String> getMap() throws AndroidDocException {
        if (attrToMethodMap.size() == 0) {
            File dat = new File(Config.DAT_PATH);
            if (!dat.isFile()) {
                throw new AndroidDocException(AndroidDocException.DAT_READ_ERROR);
            }
            
            String content = readFile(dat.getPath(), Config.DAT_BLOCK);
            String[] list = content.split(", ");
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
	
	public static Element getSystemStyles() throws AndroidDocException {
        if (systemStyles == null) {
        }
        
        return systemStyles;
	}
	
	public static Element getSystemThemes() throws AndroidDocException {
        if (systemThemes == null) {
        }
        
        return systemThemes;
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
