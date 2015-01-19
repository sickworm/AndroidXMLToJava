package com.excelsecu.axml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProjectConverter {
    private static List<String> dimenRList = new ArrayList<String>();
    private static List<String> drawableRList = new ArrayList<String>();
    private static List<String> idRList = new ArrayList<String>();
    private static List<String> layoutRList = new ArrayList<String>();
    private static List<String> stringRList = new ArrayList<String>();
    private static List<String> styleRList = new ArrayList<String>();
    private static String stringContent = "";
    private static final String[] LIST_ORDER = {"dimen", "drawable", "id"
        , "layout", "string", "style"};
    private static final List<List<String>> LIST_ORDER_LIST = new ArrayList<List<String>>();
    static {
        LIST_ORDER_LIST.add(dimenRList);
        LIST_ORDER_LIST.add(drawableRList);
        LIST_ORDER_LIST.add(idRList);
        LIST_ORDER_LIST.add(layoutRList);
        LIST_ORDER_LIST.add(stringRList);
        LIST_ORDER_LIST.add(styleRList);
    }
    private static final int[] LIST_BASE = {Config.DIMEN_BASE, Config.DRAWABLE_BASE,
        Config.ID_BASE, Config.LAYOUT_BASE, Config.STRING_BASE, Config.STYLE_BASE};
    
    public static void main(String[] argv) {
        File res = new File(Config.PROJECT_RES_PATH);
        if (!res.isDirectory()) {
            throw new AXMLException(AXMLException.PROJECT_DIR_NOT_FOUND);
        }
        File resOut = new File(Config.PROJECT_OUT_ROOT);
        if (resOut.exists()) {
            Utils.deleteDir(resOut);
        }
        
        File[] dirList = res.listFiles();
        for (File f : dirList) {
            String path = f.getPath();
            if (f.isFile()) {
                continue;
            }
            
            if (path.matches(".+layout")) {
                LayoutOutput(f);
            } else if (path.matches(".+anim")) {
            } else if (path.matches(".+drawable.*")) {
            } else if (path.matches(".+menu.*")) {
            } else if (path.matches(".+values.*")) {
                ValueOutput(f);
            }
        }
        GenerateR();
        GenerateString();
        System.out.println("Done! Output path: " + new File(Config.PROJECT_OUT_ROOT).getAbsolutePath());
    }
    
    private static void LayoutOutput(File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            System.out.println("Analysing " + f.getPath() + "...");
            if (!Utils.getFileExtension(f).equals("xml")) {
                continue;
            }
            if (f.isFile() && f.getName().endsWith(".xml")) {
                try {
                    LayoutConverter converter = new LayoutConverter(f.getPath());
                    String content = converter.convertAsString();
                    content = converter.getExtraMethod() + "\n" + content;
                    try {
                        content = Utils.buildJavaFile(f, content, converter.getImportList());
                    } catch (AXMLException e) {
                        System.out.println(f.getName() + " build Java file error: " +
                                e.getErrorCode() + " " + e.getDetails() + "");
                        content = "//Temp file. Error occurred when building this file.\n" +
                                "//Error: " + Integer.toHexString(e.getErrorCode()) + " " +
                                e.getDetails() + "\n\n" + content;
                    }
                    Utils.generateFile(f, content);
                } catch (AXMLException e) {
                    System.out.println(f.getName() + " convert error: " +
                            e.getErrorCode() + " " + e.getDetails() + "");
                    e.printStackTrace();
                }
            }
            System.out.println("");
        }
        idRList.addAll(LayoutConverter.getIdList());
    }
    
    private static void ValueOutput(File valueFile) {
        File[] fileList = valueFile.listFiles();
        for (File valuesF : fileList) {
            System.out.println("Analysing " + valuesF.getPath() + "...");
            StringOutput(valuesF);
            System.out.println("");
        }
    }
    
    private static void StringOutput(File stringFile) {
        Document document = null;
        try {
            document = new SAXReader().read(stringFile).getDocument();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.AXML_PARSE_ERROR);
        }
        Element root = document.getRootElement();
        if (!root.getName().equals("resources"))
            return;
        @SuppressWarnings("unchecked")
        List<Element> list = root.elements();
        for (Element e : list) {
            if (e.getName().equals("string")) {
                stringContent += "public static final String " + e.attributeValue("name") +
                        " = \"" + e.getText() + "\";\n";
                stringRList.add(e.attributeValue("name"));
            }
        }
    }
    
    private static void GenerateR() {
        String content = "";
        content += "package " + Config.PACKAGE_NAME + ";\n\npublic final class R {\n";
        for (int i =0; i < LIST_ORDER.length; i++) {
            List<String> list = LIST_ORDER_LIST.get(i);
            content += "\tpublic static final class " + LIST_ORDER[i] + "{\n";
            for (int j = 0; j < list.size(); j++) {
                content += "\t\tpublic static final int " + list.get(j) +
                        " = 0x" + Integer.toHexString(LIST_BASE[i] + j) + ";\n";
            }
            content += "\t}\n\n";
        }
        content += "}";
        
        String rPath = Config.PROJECT_OUT_PATH + "R.java";
        System.out.println("Generating " + rPath.replace('/', File.separatorChar) + "...");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(rPath));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.FILE_BUILD_ERROR, rPath);
        }
        System.out.println("");
    }
    
    private static void GenerateString() {
        File file = new File("res/values/strings.xml");
        stringContent = Utils.buildJavaFile(file, stringContent, null);
        Utils.generateFile(file, stringContent);
        System.out.println("");
    }
    
}
