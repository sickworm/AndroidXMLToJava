package com.excelsecu.axml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProjectConverter {
    private static List<String> idList = new ArrayList<String>();
    public static void main(String[] argv) {
        File res = new File(Config.PROJECT_RES_PATH);
        if (!res.isDirectory()) {
            throw new AXMLException(AXMLException.PROJECT_DIR_NOT_FOUND);
        }
        File resOut = new File(Config.PROJECT_OUT_PATH);
        if (resOut.exists()) {
            resOut.delete();
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
    }
    
    private static void LayoutOutput(File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            System.out.println("Analysing " + f.getName() + "...");
            if (!Util.getFileExtension(f).equals("xml")) {
                continue;
            }
            if (f.isFile() && f.getName().endsWith(".xml")) {
                try {
                    LayoutConverter converter = new LayoutConverter(f.getPath());
                    String content = converter.convertAsString();
                    idList.addAll(converter.getIdList());
                    Util.generateFile(f, content, false);
                } catch (AXMLException e) {
                    System.out.println(f.getName() + " convert error: " +
                            e.getErrorCode() + " " + e.getDetails() + "");
                    e.printStackTrace();
                }
            }
            System.out.println("");
        }
    }
    
    private static void ValueOutput(File valueFile) {
        File[] fileList = valueFile.listFiles();
        for (File valuesF : fileList) {
            System.out.println("Analysing " + valuesF.getName() + "...");
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
        String content = "";
        for (Element e : list) {
            if (e.getName().equals("string")) {
                content += "String " + e.attributeValue("name") + " = \"" + e.getText() + "\";\n";
            }
        }
        
        if (content.equals("")) {
            return;
        }
        String path = stringFile.getPath();
        path = path.substring(0, path.lastIndexOf(File.separator) + 1);
        path += "strings.xml";
        File file = new File(path);
        Util.generateFile(file, content, true);
    }
    
}
