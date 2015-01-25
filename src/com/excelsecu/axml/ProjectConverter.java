package com.excelsecu.axml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.graphics.Color;
import android.widget.EditText;
import android.widget.FrameLayout;

public class ProjectConverter {
    private static List<String> animRList = new ArrayList<String>();
    private static List<String> attrRList = new ArrayList<String>();
    private static List<String> colorRList = new ArrayList<String>();
    private static List<String> dimenRList = new ArrayList<String>();
    private static List<String> drawableRList = new ArrayList<String>();
    private static List<String> idRList = new ArrayList<String>();
    private static List<String> layoutRList = new ArrayList<String>();
    private static List<String> menuRList = new ArrayList<String>();
    private static List<String> stringRList = new ArrayList<String>();
    private static List<String> styleRList = new ArrayList<String>();
    private static final String[] LIST_ORDER = {"anim", "attr", "color", "dimen",
        "drawable", "id", "layout", "menu", "style", "string"};
    private static final List<List<String>> LIST_ORDER_LIST = new ArrayList<List<String>>(
            Arrays.asList(animRList, attrRList, colorRList, dimenRList, drawableRList,
                    idRList, layoutRList, menuRList, styleRList, stringRList));
    
    private static String stringContent = "";
    private static String colorContent = "";
    
    public static void main(String[] argv) {
    	System.out.println("Adding custom widget...");
    	CustomWidget.addCustomWidget("com.excelsecu.mgrtool.view.ESDropdownList",
    			"com.excelsecu.mgrtool.view.ESDropdownList", FrameLayout.class);
    	CustomWidget.addCustomWidget("com.safeview.safeEditText",
    			"com.safeview.safeEditText", EditText.class);
    	
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
                DrawableOutput(f);
            } else if (path.matches(".+menu.*")) {
            } else if (path.matches(".+values.*")) {
                ValueOutput(f);
            }
        }
        GenerateR();
        GenerateString();
        GenerateColor();
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
                	LayoutTranslator translator = new LayoutTranslator(f);
                    String content = translator.translate();
                    if (!translator.getExtraMethod().equals("")) {
                        content = translator.getExtraMethod() + "\n" + content;
                    }
                    
                    try {
                        content = Utils.buildJavaFile(f, content, translator.getImportList());
                    } catch (AXMLException e) {
                        System.out.println(f.getName() + " build Java file error: " +
                                e.getErrorCode() + " " + e.getDetails() + "");
                        content = "//Temp file. Error occurred when building this file.\n" +
                                "//Error: " + Integer.toHexString(e.getErrorCode()) + " " +
                                e.getDetails() + "\n\n" + content;
                    }
                    Utils.generateFile(f, content);
                    layoutRList.add(Utils.getClassName(f));
                } catch (AXMLException e) {
                    System.out.println(f.getName() + " convert error: " +
                            e.getErrorCode() + " " + e.getDetails() + "");
                    e.printStackTrace();
                }
            }
            System.out.println("");
        }
        idRList.addAll(LayoutTranslator.getIdList());
    }
    
    private static void ValueOutput(File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            System.out.println("Analysing " + f.getPath() + "...");
            Document document = null;
            try {
                document = new SAXReader().read(f).getDocument();
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
                    String text = e.getText();
                    text = text.replace("\"", "\\\"");
                    stringContent += "public static final String " + e.attributeValue("name") +
                            " = \"" + text + "\";\n";
                    stringRList.add(e.attributeValue("name"));
                }
                if (e.getName().equals("color")) {
                    colorContent += "public static final int " + e.attributeValue("name") +
                            " = Color.parseColor(\"" + e.getText() + "\");\n";
                    colorRList.add(e.attributeValue("name"));
                }
            }
            System.out.println("");
        }
    }

    private static void DrawableOutput(File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            System.out.print("Analysing " + f.getPath() + "...");
        	DrawableTranslator translator = new DrawableTranslator(f);
        	String content = translator.translate();

            String id = Utils.getClassName(f);
            if (!Utils.hasString(drawableRList, id)) {
                drawableRList.add(id);
            }
        	if (content.equals(""))
        		continue;
        	
            //package name can't use '-'
            File outFile = new File(f.getPath().replace('-', '_'));
            content = Utils.buildJavaFile(outFile, content, translator.getImportList());
            Utils.generateFile(outFile, content);
            System.out.println("");
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
                        " = 0x" + Integer.toHexString(Config.BASE + (i * 0x10000) + j) + ";\n";
            }
            content += "\t}\n\n";
        }
        content += "}";
        
        String rPath = Config.JAVA_OUT_PATH + "R.java";
        System.out.println("Generating " + new File(rPath).getPath() + "...");
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
    
    private static void GenerateColor() {
        List<String> importList = new ArrayList<String>();
        importList.add(Color.class.getName());
        File file = new File("res/values/colors.xml");
        colorContent = Utils.buildJavaFile(file, colorContent, importList);
        Utils.generateFile(file, colorContent);
        System.out.println("");
    }
}
