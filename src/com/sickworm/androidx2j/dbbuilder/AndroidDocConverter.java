package com.sickworm.androidx2j.dbbuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.sickworm.androidx2j.AX2JClassTranslator;
import com.sickworm.androidx2j.AX2JException;
import com.sickworm.androidx2j.AX2JNode;
import com.sickworm.androidx2j.AX2JParser;
import com.sickworm.androidx2j.AX2JStyle;
import com.sickworm.androidx2j.AX2JTranslatorMap;
import com.sickworm.androidx2j.Utils;

/**
 * Convert off-line Android Doc in SDK manager to the conversion table(HashMap<String, String>).
 * In current test version, this program will run every time to generate the HashMap.<p>
 * The table will storage like "View$set.orientation=setOrientation(int)".
 *
 * @author sickworm
 *
 */
public class AndroidDocConverter {
    private static AX2JTranslatorMap attrToMethodMap = AX2JTranslatorMap.getInstance();
    private static HashMap<String, AX2JStyle> systemStylesMap = new LinkedHashMap<String, AX2JStyle>();
    private static HashMap<String, AX2JStyle> systemThemesMap = new LinkedHashMap<String, AX2JStyle>();
    private static boolean hasInitialize = false;

    public static void main(String[] argv) throws DocumentException {
        generateTranslateData();
    }
    
    private static void generateTranslateData() throws DocumentException {
        System.out.println("Prasering Android documents...\n");

        String[] listPage = listPage();
        for (int i = 0; i < listPage.length; i++) {
            String path = listPage[i];
            System.out.println(path + "\n");
            AX2JClassTranslator translator = new Filter(Config.CLASSES_LIST[i]).filterDoc(path);
            System.out.println(translator + "\n");
            attrToMethodMap.add(translator);
        }

        //some attributes in Android doc is useless, or the arg value should be set as constant, remove them in here
        System.out.println("Removal\n");
        for (String attribute : Config.REMOVAL_LIST) {
            System.out.println(attribute);
            attrToMethodMap.remove(attribute);
        }
        System.out.println();

        //some attributes don't shown in Android doc, or the type value should be updated, add them in here
        System.out.println("Addition\n");
        for (String attribute : Config.ADDITION_LIST) {
            System.out.println(attribute);
            attrToMethodMap.add(attribute);
        }
        System.out.println();

        System.out.println("Prasering system styles XML...\n");
        buildSystem(Config.SYSTEM_STYLES_PATH, systemStylesMap);

        System.out.println("Prasering system themes XML...\n");
        buildSystem(Config.SYSTEM_THEMES_PATH, systemThemesMap);

        System.out.println("Generating data.dat...\n");
        generateDataFile();
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

    private static void generateDataFile() {
        File dat = new File(Config.DAT_PATH);
        if (dat.isFile()) {
            dat.delete();
        }

        appendFile(dat.getPath(), Config.DAT_COMMENT + "\n");

        StringBuffer mapString = new StringBuffer();
        mapString.append(attrToMethodMap.toString());
        mapString = mapString.insert(0, Config.DAT_BLOCK + "\n");
        mapString = mapString.insert(mapString.length(), "\n" + Config.DAT_BLOCK);
        appendFile(dat.getPath(), mapString + "\n\n");

        generateSystem(systemStylesMap, Config.STYLE_BLOCK);

        generateSystem(systemThemesMap, Config.THEME_BLOCK);
    }

    private static void generateSystem(HashMap<String, AX2JStyle> map, String block) {
        File dat = new File(Config.DAT_PATH);
        Iterator<Entry<String, AX2JStyle>> styleIterator = map.entrySet().iterator();
        StringBuffer styleString = new StringBuffer();
        while (styleIterator.hasNext()) {
            AX2JStyle style = styleIterator.next().getValue();
            styleString.append(style.toString() + "\n");
        }
        styleString.insert(0, block + "\n");
        styleString.insert(styleString.length(), block);
        appendFile(dat.getPath(), styleString + "\n\n");
    }

    public static AX2JTranslatorMap getMap() {
        if (attrToMethodMap.getMap().size() == 0) {
            File dat = new File(Config.DAT_PATH);
            if (!dat.isFile()) {
                throw new AndroidDocException(AndroidDocException.DAT_READ_ERROR);
            }

            String content = readFile(dat.getPath(), Config.DAT_BLOCK);
            String[] list = content.split("\n");
            for (String s : list) {
                if (s.startsWith("//")) {
                    String typeString = s.replace("//", "");
                    Class<?> type = Utils.matchClass(typeString);
                    if (!type.equals(Void.class)) {
                        attrToMethodMap.add(new AX2JClassTranslator(type));
                    }
                } else if (!s.equals("")) {
                    attrToMethodMap.add(s);
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
    public static boolean appendFile(String filePath, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath), true), Config.ENCODE));
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
    
    /**
     * initialize the translate resources. Need to be called before translating start.
     */
    public static boolean init() {
        if (hasInitialize) {
            System.out.print("Translate resources has initialized.");
            return true;
        }
        System.out.println("Initializing resources...\n");
        try {
            AndroidDocConverter.getMap();
            AndroidDocConverter.getSystemStyles();
            AndroidDocConverter.getSystemThemes();
            hasInitialize = true;
            return true;
        } catch (AX2JException e) {
            System.out.println("Error code: " + e.getErrorCode() + ", " + ": " + e.getLocalizedMessage());
            System.out.println("Failed to parse translate table, please check data.dat. Redownload it if nessesary.");
            return false;
        }
    }
}