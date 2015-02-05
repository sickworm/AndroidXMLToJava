package com.excelsecu.androidx2j;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.excelsecu.androidx2j.dbbuilder.AndroidDocConverter;

public class AX2JStyle {
    private static HashMap<String, AX2JStyle> themeMap = new HashMap<String, AX2JStyle>();
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
        if (parent.equals("") && name.contains(".")) {
            parent = name.substring(0, name.lastIndexOf('.'));
        }
        
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
    
    /** style **/
    public static void addStyle(AX2JNode ax2jNode) {
        AX2JStyle styleNode = buildNode(ax2jNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, ax2jNode.asXML());
        }
        styleMap.put(styleNode.name, styleNode);
    }
    
    public static void buildSystemStyle() {
        Element systemStyleElement = AndroidDocConverter.getSystemStyles();
        if (systemStyleElement == null) {
            throw new AX2JException(AX2JException.DAT_SYSTEM_STYLE_ERROR, "can not get it from data.dat");
        }
        AX2JNode systemStyle = new AX2JParser(systemStyleElement).parse();
        if (!systemStyle.getLabelName().equals("resources")) {
            throw new AX2JException(AX2JException.DAT_SYSTEM_STYLE_ERROR, "not a resources block");
        }
        for (AX2JNode n : systemStyle.getChildren()) {
            if (!n.getLabelName().equals("style")) {
                continue;
            }
            addStyle(n);
        }
    }
    
    public static AX2JStyle getStyle(String name) {
        return styleMap.get(name);
    }
    
    public static HashMap<String, AX2JStyle> getStyles() {
        return styleMap;
    }

    /** theme **/
    public static void addTheme(AX2JNode ax2jNode) {
        AX2JStyle styleNode = buildNode(ax2jNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, ax2jNode.asXML());
        }
        themeMap.put(styleNode.name, styleNode);
    }
    
    public static void buildSystemTheme() {
        Element systemStyleElement = AndroidDocConverter.getSystemStyles();
        if (systemStyleElement == null) {
            throw new AX2JException(AX2JException.DAT_SYSTEM_STYLE_ERROR, "can not get it from data.dat");
        }
        AX2JNode systemStyle = new AX2JParser(systemStyleElement).parse();
        if (!systemStyle.getLabelName().equals("resources")) {
            throw new AX2JException(AX2JException.DAT_SYSTEM_STYLE_ERROR, "not a resources block");
        }
        for (AX2JNode n : systemStyle.getChildren()) {
            if (!n.getLabelName().equals("style")) {
                continue;
            }
            addTheme(n);
        }
    }

    public static AX2JStyle getTheme(String name) {
        return themeMap.get(name);
    }
    
    public static HashMap<String, AX2JStyle> getThemes() {
        return themeMap;
    }
}