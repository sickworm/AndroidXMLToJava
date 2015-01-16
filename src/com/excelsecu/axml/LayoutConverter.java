package com.excelsecu.axml;

import java.util.List;

/**
 * Convert an Android XML file to Java method block
 * @author ch
 *
 */
public class LayoutConverter {
    private String path;
    private Parser parser;
    private AXMLNode root;
    private LayoutTranslater translater;
    
    public static void main(String[] argv) {
        String path = "test.xml";
        LayoutConverter converter = new LayoutConverter(path);
        String javaBlock = converter.convertAsString();
        String extraMethod = converter.getExtraMethod();
        System.out.println(extraMethod);
        System.out.println(javaBlock);

        System.out.println("-----id start-----");
        List<String> idList = LayoutConverter.getIdList();
        for (String id : idList) {
            System.out.println("R.id." + id);
        }
        System.out.println("-----id end------");
        System.out.println("");
        System.out.println("-----import start-----");
        List<Class<?>> classList = converter.getImportList();
        for (Class<?> c : classList) {
            String className = c.getName();
            className = className.replace('$', '.');
            System.out.println("import " + className + ";");
        }
        System.out.println("-----import end------");
    }
    
    public LayoutConverter(String path) {
        this.path = path;
        this.parser = new Parser(path);
        this.translater = new LayoutTranslater();
    }
    
    public String convertAsString() {
        root = parser.parse();
        return convert(root);
    }
    
    private String convert(AXMLNode root) {
        String javaBlock = "";
        String nodeJavaBlock = translater.translate(root);
        javaBlock += nodeJavaBlock;
        for (AXMLNode n : root.getChildren()) {
            javaBlock += convert(n);
        }
        return javaBlock;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getExtraMethod() {
        return translater.getExtraMethod();
    }
    
    public static List<String> getIdList() {
        return LayoutTranslater.getIdList();
    }
    
    public List<Class<?>> getImportList() {
        return translater.getImportList();
    }
}