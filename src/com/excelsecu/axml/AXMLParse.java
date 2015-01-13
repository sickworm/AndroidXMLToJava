package com.excelsecu.axml;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class AXMLParse {
    private final String path;
    
    public static void main(String[] argv) {
        System.out.println("--------XMLParse start-------");
        new AXMLParse("test.xml").parse();
        System.out.println("--------XMLParse end---------");
    }
    
    public AXMLParse(String path) {
        this.path = path;
    }
    
    private void parse() {
        Document document;
        try {
            document = new SAXReader().read(path).getDocument();
            getElements(null, document.getRootElement(), 0);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.FILE_NOT_FOUND);
        }
    }
    
    private void getElements(AXMLNode rootNode, Element rootElement, int layer) {
        @SuppressWarnings("unchecked")
        List<Element> list = rootElement.elements();
        if (list == null || list.size() == 0) {
            return;
        }
        for(Element e : list){
            AXMLNode node = new AXMLNode(rootNode, e);
            getElements(node, e, layer + 1);
        }
        //print the element
//        String tab = "";
//        for (int i = 0; i < layer; i++) {
//            tab += "\t";
//        }
//            
//        for(Element e : list){
//            System.out.println(tab + e.getName());
//            getElements(e, layer + 1);
//        }
    }
}