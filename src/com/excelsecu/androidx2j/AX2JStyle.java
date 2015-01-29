package com.excelsecu.androidx2j;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class AX2JStyle {
    private static HashMap<String, AX2JStyle> styleMap = new HashMap<String, AX2JStyle>();
    
    public AX2JNode ax2jNode;
    public AX2JNode styleNode;
    public String parent;
    public String name;
    public List<Attribute> attrList;
    
    public AX2JStyle(AX2JNode ax2jNode, String name, String parent, AX2JNode styleNode) {
        this.ax2jNode = ax2jNode;
        this.name = name;
        this.parent = parent;
        this.styleNode = styleNode;
        this.attrList = styleNode.getAttributes();
    }
    
    public static AX2JStyle buildNode(AX2JNode node) {
        if (!node.getLabelName().equals("style")) {
            return null;
        }
        String name = node.attributeValue("name");
        if (name == null) {
            return null;
        }
        String parent = node.attributeValue("parent");
        parent = (parent == null)? "" : parent;
        
        Document document = DocumentHelper.createDocument();
        Element element = document.addElement("container");
        for (AX2JNode n : node.getChildren()) {
            if (!n.getLabelName().equals("item")) {
                continue;
            }

            String attrName = n.attributeValue("name");
            if (attrName == null) {
                continue;
            }
            element.addAttribute(attrName, n.getText());
        }
        
        return new AX2JStyle(node, name, parent, new AX2JNode(null, element));
    }
    
    public static void addStyle(AX2JNode ax2jNode) {
        AX2JStyle styleNode = buildNode(ax2jNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, ax2jNode.asXML());
        }
        styleMap.put(styleNode.name, styleNode);
    }
    
    public static AX2JStyle getStyle(String name) {
        return styleMap.get(name);
    }
    
    public static HashMap<String, AX2JStyle> getStyles() {
        return styleMap;
    }
}