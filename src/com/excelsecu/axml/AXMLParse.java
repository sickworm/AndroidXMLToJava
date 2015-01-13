package com.excelsecu.axml;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Single Android XML file parse, export the root of AXMLNode.
 * @author ch
 *
 */
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
    
    /**
     * Parse Android XML file info AXMLNode structure.
     * @return the root of AXMLNode
     */
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
    
    /**
     * Recursion to iterate the Android XML elements.
     * @param parent the parent of the return node, null if it has no parent
     * @param rootElement the root elements in this function
     * @param layer the layer of this node to be created
     * @return the AXMLNode of the {@param rootElement}, the child of {@param rootElement}
     */
    private AXMLNode parseElements(AXMLNode parent, Element rootElement, int layer) {
        AXMLNode node = new AXMLNode(parent, rootElement, layer);
        
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
    
    /**
     * Print the Android XML tree structure.
     * @param rootNode the root in this function, the real root elements in the first time
     * @param layer
     */
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