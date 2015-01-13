package com.excelsecu.axml;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


public class AXMLParse {
    private final String path;
    
    public static void main(String[] argv) {
        System.out.println("--------XMLParse start-------");
        AXMLNode rootNode = new AXMLParse("test.xml").parse();
        printNode(rootNode, 0);
        System.out.println("--------XMLParse end---------");
    }
    
    public AXMLParse(String path) {
        this.path = path;
    }
    
    private AXMLNode parse() {
        AXMLNode rootNode = null;
        try {
            Document document;
            document = new SAXReader().read(path).getDocument();
            rootNode = parseElements(null, document.getRootElement(), 0);
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.AXML_PARSE_ERROR);
        }
        return rootNode;
    }
    
    private AXMLNode parseElements(AXMLNode rootNode, Element rootElement, int layer) {
        AXMLNode node = new AXMLNode(rootNode, rootElement, layer);
        
        @SuppressWarnings("unchecked")
        List<Element> list = rootElement.elements();
        if (list == null || list.size() == 0) {
            return null;
        }
        for(Element e : list){
            parseElements(node, e, layer + 1);
        }
        return node;
    }
    
    private static void printNode(AXMLNode rootNode, int layer) {
        if (rootNode == null) {
            return;
        }
        String tab = "";
        int i = layer;
        while(i > 0) {
            tab += "\t";
            i--;
        }
        System.out.println(tab + "<" + rootNode.getName() + ">");
        List<Attribute> attrList = rootNode.getAttributes();
        for (Attribute a : attrList) {
            System.out.println(tab + "\t" + a.getQualifiedName() + " = " + a.getValue());
        }
        
        List<AXMLNode> children = rootNode.getChildren();
        for (AXMLNode n : children) {
            printNode(n, layer + 1);
        }
    }
}