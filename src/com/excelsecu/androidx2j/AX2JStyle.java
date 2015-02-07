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
    private static HashMap<String, AX2JStyle> systemStyleMap = AndroidDocConverter.getSystemStyles();
    private static HashMap<String, AX2JStyle> systemThemeMap = AndroidDocConverter.getSystemThemes();
    private static AX2JStyle projectTheme = null;
    
    /** style node with normal attribute. e.g. <container>android:textSize="16sp"</container> **/
    public AX2JNode styleNode;
    public String parent;
    public String name;
    public List<Attribute> attrList;
    
    public AX2JStyle(String name, String parent, AX2JNode styleNode) {
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
        
        return new AX2JStyle(name, parent, new AX2JNode(null, element));
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
    public static AX2JStyle getSystemStyle(String styleValue) {
        String type = styleValue.substring(0, styleValue.indexOf('/'));
        String styleName = styleValue.substring(styleValue.indexOf('/') + 1);
        
        if (type.equals("@android:style")) {
            return systemStyleMap.get(styleName);
        }
        return null;
    }
    
    /** system themes **/
    
    public static AX2JStyle getSystemTheme(String name) {
        return systemThemeMap.get(name);
    }
    
    /** project theme **/
    public static String getProjectThemeStyleName(String itemName) {
    	if (projectTheme == null) {
    		throw new AX2JException(AX2JException.THEME_NOT_FOUND, itemName);
    	}
    	String projectThemeName = getProjectThemeStyleName(projectTheme, itemName);
    	if (projectThemeName == null) {
    		throw new AX2JException(AX2JException.THEME_NOT_FOUND, itemName);
    	}
    	return projectThemeName;
    }
    
    public static String getProjectThemeStyleName(AX2JStyle theme, String itemName) {
		String themeName = projectTheme.styleNode.attributeValue(itemName);
		if (themeName == null) {
			if (theme.parent != null) {
				AX2JStyle parentTheme = getSystemTheme(theme.parent);
				themeName = getProjectThemeStyleName(parentTheme, itemName);
			}
		}
    	return themeName;
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
    
    public String toString() {
    	StringBuffer attrString = new StringBuffer();
    	if (attrList.size() != 0) {
	        for (Attribute a : attrList) {
	        	attrString.append(a.asXML() + ",");
	        }
	        attrString.deleteCharAt(attrString.length() - 1);
    	}
        return name + "," + parent + "," + attrString;
    }
}