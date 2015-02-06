package com.excelsecu.androidx2j;

import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.excelsecu.androidx2j.dbbuilder.AndroidDocConverter;

public class AX2JStyle {
    private static HashMap<String, AX2JStyle> styleMap = new HashMap<String, AX2JStyle>();
    private static HashMap<String, AX2JStyle> systemStyleMap = new HashMap<String, AX2JStyle>();
    private static HashMap<String, AX2JStyle> systemThemeMap = new HashMap<String, AX2JStyle>();
    private static AX2JStyle projectTheme = null;
    
    /** origin node. e.g. <style><item name="android:textSize">16sp</item></style> **/
    public AX2JNode originNode;
    /** style node with normal attribute. e.g. <container>android:textSize="16sp"</container> **/
    public AX2JNode styleNode;
    public String parent;
    public String name;
    public List<Attribute> attrList;
    
    public AX2JStyle(AX2JNode originNode, String name, String parent, AX2JNode styleNode) {
        this.originNode = originNode;
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
    
    /** project styles **/
    public static void addStyle(AX2JNode originNode) {
        AX2JStyle styleNode = buildNode(originNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, originNode.asXML());
        }
        styleMap.put(styleNode.name, styleNode);
    }
    
    /** get the style from project style and system style **/
    public static AX2JStyle getStyle(String styleValue) {
        String type = styleValue.substring(0, styleValue.indexOf('/'));
        String styleName = styleValue.substring(styleValue.indexOf('/') + 1);
        AX2JStyle style = null;
        
        if (type.equals("@android:style")) {
        	style = getSystemStyle(styleValue);
        } else if (type.equals("@style")) {
	    	style = styleMap.get(styleValue);
        } else if (type.equals("?android:attr")) {
            //it's a project theme attribute
            styleName = getProjectThemeStyleName(styleName);
            if (styleName != null) {
                style = getStyle(styleName);
            }
        }
        return style;
    }
    
    public static HashMap<String, AX2JStyle> getStyles() {
        return styleMap;
    }
    
    /** system styles **/
    public static void buildSystemStyles() {
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
            addSystemStyle(n);
        }
    }
    
    public static void addSystemStyle(AX2JNode originNode) {
        AX2JStyle styleNode = buildNode(originNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, originNode.asXML());
        }
        systemStyleMap.put(styleNode.name, styleNode);
    }
    
    public static AX2JStyle getSystemStyle(String styleValue) {
        String type = styleValue.substring(0, styleValue.indexOf('/'));
        String styleName = styleValue.substring(styleValue.indexOf('/') + 1);
        
        if (type.equals("@android:style")) {
            return systemStyleMap.get(styleName);
        }
        return null;
    }
    
    public static HashMap<String, AX2JStyle> getSystemStyles() {
        return systemStyleMap;
    }

    /** system themes **/
    public static void addTheme(AX2JNode originNode) {
        AX2JStyle styleNode = buildNode(originNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, originNode.asXML());
        }
        systemThemeMap.put(styleNode.name, styleNode);
    }
    
    public static void buildSystemThemes() {
        Element systemThemeElement = AndroidDocConverter.getSystemThemes();
        if (systemThemeElement == null) {
            throw new AX2JException(AX2JException.DAT_SYSTEM_THEME_ERROR, "can not get it from data.dat");
        }
        AX2JNode systemTheme = new AX2JParser(systemThemeElement).parse();
        if (!systemTheme.getLabelName().equals("resources")) {
            throw new AX2JException(AX2JException.DAT_SYSTEM_THEME_ERROR, "not a resources block");
        }
        for (AX2JNode n : systemTheme.getChildren()) {
            if (!n.getLabelName().equals("style")) {
                continue;
            }
            addTheme(n);
        }
        projectTheme = getSystemTheme(Config.DEFAULT_THEME);
        if (projectTheme == null) {
            throw new AX2JException(AX2JException.THEME_NOT_FOUND, Config.DEFAULT_THEME);
        }
    }

    public static AX2JStyle getSystemTheme(String name) {
        return systemThemeMap.get(name);
    }
    
    public static HashMap<String, AX2JStyle> getSystemThemes() {
        return systemThemeMap;
    }
    
    public static String getProjectThemeStyleName(String itemName) {
    	if (projectTheme == null) {
    		throw new AX2JException(AX2JException.THEME_NOT_FOUND, itemName);
    	}
    	return getProjectThemeStyleName(projectTheme, itemName);
    }
    
    public static String getProjectThemeStyleName(AX2JStyle theme, String itemName) {
		String styleName = projectTheme.styleNode.attributeValue(itemName);
		if (styleName == null) {
			if (theme.parent != null) {
				AX2JStyle parentTheme = getSystemTheme(theme.parent);
				styleName = getProjectThemeStyleName(parentTheme, itemName);
			}
		}
    	return styleName;
    }
    
    public static void setProjectTheme(String projectThemeName) {
    	AX2JStyle newProjectTheme = getStyle(projectThemeName);
    	if (newProjectTheme == null) {
    		newProjectTheme = getSystemTheme(projectThemeName);
    	}
    	if (newProjectTheme == null) {
    		throw new AX2JException(AX2JException.THEME_NOT_FOUND, projectThemeName);
    	}
    	
    	projectTheme = newProjectTheme;
    }
    
    public static AX2JStyle getProjectTheme() {
    	return projectTheme;
    }
}