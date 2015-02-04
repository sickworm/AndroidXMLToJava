package com.excelsecu.androidx2j.dbbuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.excelsecu.androidx2j.Utils;

/**
 * Convert Offline Androd Doc in SDK manager to the conversion table(HashMap<String, String>).
 * In current test version, this program will run everytime to generate the HashMap.<p>
 * The table will storage like "View$set.orientation=setOrientation(int)".
 * 
 * @author ch
 *
 */
public class AndroidDocConverter {
    private static HashMap<String, String> attrToMethodMap = new HashMap<String, String>();
    
	public static void main(String[] argv) {
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
        
        System.out.println("Generating data.dat...\n");
        generateDat();
        System.out.println("Done!\n");
	}
	
	private static void generateDat() {
	    File dat = new File(Config.DAT_PATH);
	    if (dat.isFile()) {
	        dat.delete();
	    }
	    Utils.writeFile(dat.getPath(), Config.DAT_COMMAND + "\n" + attrToMethodMap.toString());
	}
	
	public static HashMap<String, String> getMap() throws AndroidDocException {
        if (attrToMethodMap.size() == 0) {
            attrToMethodMap = getMapFromDat();
        }
        
        return attrToMethodMap;
	}
	
	private static HashMap<String, String> getMapFromDat() {
        HashMap<String, String> map = new HashMap<String, String>(); 
        File dat = new File(Config.DAT_PATH);
        if (!dat.isFile()) {
            throw new AndroidDocException(AndroidDocException.DAT_READ_ERROR);
        }
        
        String content = Utils.readFile(dat.getPath());
        content = content.substring(content.indexOf('{') + 1, content.indexOf('}'));
        String[] list = content.split(", ");
        for (String s : list) {
            String[] split = s.split("=");
            if (split.length == 1) {
                map.put(split[0], "");
            } else {
                map.put(split[0], split[1]);
            }
        }
        
        return map;
	}
	
	/**
	 * Include all the html path which has xml attribute to java method table.
	 * @return The list of XML attribute to Java method table.
	 */
	public static String[] listPage() {
	    String[] list = new String[Config.CLASSES_LIST.length];
		for (int i = 0; i < Config.CLASSES_LIST.length; i++) {
		    String name = Config.CLASSES_LIST[i].getName();
		    name = name.replace('.', '/');
		    name = Config.BASE_PATH + name + ".html";
	        list[i] = name;
		}
		return list;
	}
}
