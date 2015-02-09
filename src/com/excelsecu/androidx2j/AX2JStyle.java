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
    
    public String parent;
    public String name;
    public List<Attribute> attrList;
    
    public AX2JStyle(String name, String parent, List<Attribute> attrList) {
        this.name = name;
        this.parent = parent;
        this.attrList = attrList;
    }
    
    @SuppressWarnings("unchecked")
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
        
        return new AX2JStyle(name, parent, element.attributes());
    }
    
    /** get the style from project style and system style **/
    public static AX2JStyle getStyle(String styleValue) {
    	String type = "";
        String styleName = "";
        //e.g. style="@android:style/Animation.PopupWindow"
    	if (styleValue.indexOf('/') != -1) {
            type = styleValue.substring(0, styleValue.indexOf('/'));
            styleName = styleValue.substring(styleValue.indexOf('/') + 1);
        //e.g. parent="android:Animation.PopupWindow"
    	} else {
            type = styleValue.substring(0, styleValue.indexOf(':'));
            styleName = styleValue.substring(styleValue.indexOf(':') + 1);
    	}
    	
        AX2JStyle style = null;
        if (type.equals("@android:style") || type.equals("android")) {
        	style = getSystemStyle(styleName);
        } else if (type.equals("@style")) {
	    	style = styleMap.get(styleName);
        } else if (type.equals("?android:attr")) {
            //it's a project theme attribute
            styleName = getProjectThemeStyleName(styleName);
            if (styleName != null) {
                style = getStyle(styleName);
            }
        }
        
        if (style == null) {
        	throw new AX2JException(AX2JException.STYLE_NOT_FOUND, styleValue);
        }
        return style;
    }
    
    /** custom styles **/
    public static void addStyle(AX2JNode originNode) {
        AX2JStyle styleNode = buildNode(originNode);
        if (styleNode == null) {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, originNode.asXML());
        }
        styleMap.put(styleNode.name, styleNode);
    }
    
    public static AX2JStyle getCustomStyle(String styleName) {
        return styleMap.get(styleName);
    }
    
    /** system styles **/
    public static AX2JStyle getSystemStyle(String styleName) {
    	if (systemStyleMap.size() == 0) {
    		systemStyleMap = AndroidDocConverter.getSystemStyles();
    	}
        return systemStyleMap.get(styleName);
    }
    
    /** system themes **/
    
    public static AX2JStyle getSystemTheme(String themeName) {
    	if (systemThemeMap.size() == 0) {
    		systemThemeMap = AndroidDocConverter.getSystemThemes();
    	}
        return systemThemeMap.get(themeName);
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
		String themeName = projectTheme.attributeValue(itemName);
		if (themeName == null) {
			if (theme.parent != null) {
				AX2JStyle parentTheme = getSystemTheme(theme.parent);
				themeName = getProjectThemeStyleName(parentTheme, itemName);
			}
		}
    	return themeName;
    }
    
    public String attributeValue(String itemName) {
    	for (Attribute a : attrList) {
    		if (a.getQualifiedName().equals(itemName)) {
    			return a.getValue();
    		}
    	}
    	return null;
    }
    
    public static void setProjectTheme(String projectThemeValue) {
        AX2JStyle newProjectTheme = null;
        int index = projectThemeValue.indexOf(':');
        if (index == -1) {
            newProjectTheme = getCustomStyle(projectThemeValue);
        } else {
            newProjectTheme = getSystemTheme(projectThemeValue.substring(index + 1));
        }
        
    	if (newProjectTheme == null) {
    		throw new AX2JException(AX2JException.THEME_NOT_FOUND, projectThemeValue);
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