package com.excelsecu.axml;

import java.util.List;

import org.dom4j.Attribute;

import android.view.ViewGroup;

public class AXMLSpecialTranslater {
    int num = 1;
    private String nodeName;
    @SuppressWarnings("unused")
    private AXMLNode node;
    List<Attribute> attrList;
    /** set up width and height just need one setting **/
    private boolean widthAndHeight = false;
    
    public AXMLSpecialTranslater(String nodeName, AXMLNode node) {
        this.nodeName = nodeName;
        this.node = node;
        attrList = node.getAttributes();
    }
    
    public String translate(Attribute attr) throws AXMLException {
        String attrName = attr.getQualifiedName();
        String javaBlock = "";
        if (attrName.equals("android:layout_width") || attrName.equals("android:layout_height")) {
            if (!widthAndHeight) {
                String paramName = AXMLUtil.classToObject(ViewGroup.LayoutParams.class.getSimpleName()) + num;
                String width = findValueByName("android:layout_width");
                String height = findValueByName("android:layout_height");
                width = AXMLTranslater.translateValue(width);
                height = AXMLTranslater.translateValue(height);
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
