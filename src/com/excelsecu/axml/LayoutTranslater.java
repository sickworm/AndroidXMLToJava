package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;

import android.view.ViewGroup;

import com.excelsecu.axml.dbbuilder.AndroidDocConverter;

/**
 * Translate a Android XML Node to a Java method block.
 * @author ch
 *
 */
public class LayoutTranslater {
    private HashMap<String, String> map = null;
    private String extraMethod = "";
    private List<String> idList = new ArrayList<String>();
    private List<Class<?>> importList = new ArrayList<Class<?>>();
    private int num = 1;
    /** record of {@link LayoutTranslater#extraHandle(String attrName , String attrValue)} **/
    private static boolean scale = false;
    
	public LayoutTranslater() {
        map = AndroidDocConverter.getMap();
	}
	
	/**
	 * Translate a XML attritube to a Java method.
	 * @param attrName the name of attribute
	 * @param attrValue the value of attribute
	 * @return
	 */
	public String translate(AXMLNode node) {
        String javaBlock = "";
	    try {
	        node.getType();
	    } catch (AXMLException e) {
	        if (e.getErrorCode() == AXMLException.CLASS_NOT_FOUND) {
	            System.out.println("<" + node.getName() + "/>" + " label not support");
	            javaBlock = "//<" + node.getName() + "/>\n";
	            return javaBlock;
	        }
	        e.printStackTrace();
	    }
	    
	    String nodeName = Util.classToObject(node.getName()) + num;
        String newMethod = node.getName() + " " + nodeName + " = new " + nodeName + "();\n";
        javaBlock += newMethod;
        AXMLSpecialTranslater specialTranslater = new AXMLSpecialTranslater(nodeName, node, num);
        for (Attribute a : node.getAttributes()) {
            String attrMethod = "";
            String attrName = a.getQualifiedName();
            String attrValue = a.getValue();
            try {
                String methodName = transAttrToMethod(attrName, node.getType());
                String methodValue = translateValue(attrValue);
                attrMethod = methodName + "(" + methodValue + ")";
                attrMethod = nodeName + "." + attrMethod + ";\n";
                addImport(node.getType());
            } catch (AXMLException e) {
                try {
                    //deal with the attributes that doesn't match the XML attributes table
                    attrMethod = specialTranslater.translate(a);
                } catch (AXMLException e1) {
                    //translater can not translate this attribute
                    attrMethod = "//" + attrName + "=\"" + attrValue + "\";\n";
                }
            }
            javaBlock += attrMethod;
        }
        javaBlock += "\n";
        num++;
        
        return javaBlock;
	}
	
	/**
	 * Translate XML element's attribute to Android method without parameters.
	 * @param attrName	The name of attribute.
	 * @return Android method matches the attribute without parameters.
	 */
	private String transAttrToMethod(String attrName, Class<?> type) {
	    //find the conversion between XML attribute and Java method in the match map.
	    String key = type.getSimpleName() + "$" + attrName;
        if (!map.containsKey(key)) {
            //find the conversion from its super class
            while (Util.isSupportClass(type.getSuperclass())) {
                type = type.getSuperclass();
                key = type.getSimpleName() + "$" + attrName;
                if (map.containsKey(key))
                    break;
            }
            if (!map.containsKey(key)) {
                throw new AXMLException(AXMLException.METHOD_NOT_FOUND, key);
            }
        }
        String methodName = map.get(key);
        if (methodName.equals(null) || methodName.equals("")) {
            throw new AXMLException(AXMLException.METHOD_NOT_FOUND, key);
        }
        methodName = methodName.substring(0, methodName.indexOf("("));
        return methodName;
	}
	
	protected String translateValue(String value) {
        extraHandle(value);
	    //dp, px, sp
	    //not strict enough, should check attrName both
	    if (value.matches("[0-9]+dp")) {
            value = value.substring(0, value.length() - 2);
            value = value + " / scale + 0.5f";
	    } else if (value.matches("[0-9]+sp")) {
	        value = value.substring(0, value.length() - 2);
	    } else if (value.matches("[0-9]+px")) {
            value = value.substring(0, value.length() - 2);
        } else if (value.equals("fill_parent") || value.equals("match_parent")) {
            value = "ViewGroup.LayoutParams.MATCH_PARENT";
        } else if (value.equals("wrap_content")) {
            value = "ViewGroup.LayoutParams.WRAP_CONTENT";
        }
	    
	    if (value.contains("@+id/") || value.contains("@id/")) {
	        value = value.substring(value.indexOf('/') + 1);
	        value = "R.id." + value;
	    } else if (value.contains("@string/")) {
	        value = value.substring(value.indexOf('/') + 1);
            value = "R.id." + value;
	        value = "getResources().getString(" + value + ")";
        }
        return value;
	}
	
	/**
	 * Find out what extra settings or buildings need to be added.
	 * @param attrName
	 * @param attrValue
	 */
	private void extraHandle(String attrValue) {
        if (attrValue.matches("[0-9]+dp")) {
            if (!scale) {
                scale = true;
                extraMethod += "final float scale = this.getResources().getDisplayMetrics().density;\n";
            }
        } else if (attrValue.equals("fill_parent") || attrValue.equals("match_parent")
                || attrValue.equals("wrap_content")) {
            addImport(android.view.ViewGroup.LayoutParams.class);
        } else if (attrValue.contains("@+id/")) {
            idList.add(attrValue.substring(attrValue.indexOf('/') + 1));
        }
	}
    
	/**
	 *  Add the class to the import list. If already exists, ignore. 
	 *  @param className the class try to be added in import list
	 */
    protected void addImport(Class<?> className) {
        if (!importList.contains(className)) {
            importList.add(className);
        }
    }
    
    public String getExtraMethod() {
        return extraMethod;
    }
    
    public List<String> getIdList() {
        return idList;
    }
    
    public List<Class<?>> getImportList() {
        return importList;
    }
    
    public class AXMLSpecialTranslater {
        private int num;
        private String nodeName;
        @SuppressWarnings("unused")
        private AXMLNode node;
        List<Attribute> attrList;
        /** set up width and height just need one setting **/
        private boolean widthAndHeight = false;
        
        public AXMLSpecialTranslater(String nodeName, AXMLNode node, int num) {
            this.nodeName = nodeName;
            this.node = node;
            this.num = num;
            attrList = node.getAttributes();
        }
        
        public String translate(Attribute attr) throws AXMLException {
            String attrName = attr.getQualifiedName();
            String javaBlock = "";
            if (attrName.equals("android:layout_width") || attrName.equals("android:layout_height")) {
                if (!widthAndHeight) {
                    String paramName = Util.classToObject(ViewGroup.LayoutParams.class.getSimpleName()) + num;
                    String width = findValueByName("android:layout_width");
                    String height = findValueByName("android:layout_height");
                    width = translateValue(width);
                    height = translateValue(height);
                    javaBlock = "ViewGroup.LayoutParams " + paramName + " = new ViewGroup.LayoutParams(" +width + ", " + height + ");\n";
                    javaBlock += nodeName + ".setLayoutParams(" + paramName + ");\n";
                    widthAndHeight = true;
                }
                return javaBlock;
            }
            throw new AXMLException(AXMLException.METHOD_NOT_FOUND);
        }
        
        private String findValueByName(String attrName) {
            for (Attribute a : attrList) {
                if (a.getQualifiedName().equals(attrName)) {
                    return a.getValue();
                }
            }
            throw new AXMLException(AXMLException.ATTRIBUTE_NOT_FOUND);
        }
    }
}