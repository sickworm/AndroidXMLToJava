package com.excelsecu.axml;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class AXmlParse {
    private final String path;
    
    public static void main(String[] argv) {
        System.out.println("--------XmlParse start-------");
        new AXmlParse("test.xml").parse();
        System.out.println("--------XmlParse end---------");
    }
    
    public AXmlParse(String path) {
        this.path = path;
    }
    
    private void parse() {
        Document document;
        try {
            document = new SAXReader().read(path).getDocument();
            getElements(document.getRootElement(), 0);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new AXmlException(AXmlException.FILE_NOT_FOUND);
        }
    }
    
    private void getElements(Element rootElement, int layer) {
        @SuppressWarnings("unchecked")
        List<Element> list = rootElement.elements();
        if (list == null || list.size() == 0) {
            return;
        }
        String tab = "";
        for (int i = 0; i < layer; i++) {
            tab += "\t";
        }
            
        for(Element e : list){
            System.out.println(tab + e.getName());
            getElements(e, layer + 1);
        }
    }
}