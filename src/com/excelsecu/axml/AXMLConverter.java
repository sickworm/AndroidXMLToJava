package com.excelsecu.axml;

import java.util.List;

/**
 * Convert an Android XML file to Java method block
 * @author ch
 *
 */
public class AXMLConverter {
    private String path;
    private AXMLParser parser;
    private AXMLNode root;
    private AXMLTranslater translater;
    
    public static void main(String[] argv) {
        String path = "test.xml";
        AXMLConverter converter = new AXMLConverter(path);
        String javaBlock = converter.convertAsString();
        String extraMethod = converter.getExtraMethod();
        System.out.println(extraMethod);
        System.out.println(javaBlock);

        System.out.println("-----id start-----");
        AXMLTranslater trans = AXMLTranslater.getInstance();
        List<String> idList = trans.getIdList();
        for (String id : idList) {
            System.out.println("R.id." + id);
        }
        System.out.println("-----id end------");
        System.out.println("");
        System.out.println("-----import start-----");
        List<Class<?>> classList = trans.getImportList();
        for (Class<?> c : classList) {
            String className = c.getName();
            className = className.replace('$', '.');
            System.out.println("import " + className + ";");
        }
        System.out.println("-----import end------");
    }
    
    public AXMLConverter(String path) {
        this.path = path;
        this.parser = new AXMLParser(path);
        this.translater = AXMLTranslater.getInstance();
        root = parser.parse();
    }
    
    public String convertAsString() {
        return convert(root);
    }
    
    public String convert(AXMLNode root) {
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
    
    public List<String> getIdList() {
        return translater.getIdList();
    }
    
    public List<Class<?>> getImportList() {
        return translater.getImportList();
    }
}