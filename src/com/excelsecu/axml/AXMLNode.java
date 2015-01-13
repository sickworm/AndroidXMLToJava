package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.excelsecu.axml.dbbuilder.AndroidDocConfig;

public class AXMLNode implements Cloneable {
    private AXMLNode parent;
    private Element e;
    private List<Attribute> attrList;
    private Class<?> type;
    
    public AXMLNode(AXMLNode parent, Element e) {
        this.parent = parent;
        this.e = e;
        String name = this.getName();
        this.type = matchClass(name);
        if (this.type == null) {
            throw new AXMLException(AXMLException.CLASS_NOT_FOUND);
        }
        attrList = new ArrayList<Attribute>();
        for (int i = 0; i < e.attributeCount(); i++) {
            attrList.add(e.attribute(i));
        }
    }
    
    private Class<?> matchClass(String name) {
        for (int i = 0; i < AndroidDocConfig.CLASSES_LIST.length; i++) {
            if (name.contains("support")) {
                name = name.substring(0, name.lastIndexOf('.'));
                name = name.substring(name.lastIndexOf('.'));
            }
            String className = AndroidDocConfig.CLASSES_LIST[i].getSimpleName();
            if (className.equals(name)) {
                return AndroidDocConfig.CLASSES_LIST[i];
            }
        }
        return null;
    }
    
    public AXMLNode getParent() {
        return parent;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public String getName() {
        return e.getName();
    }
    
    protected AXMLNode clone() {
        return new AXMLNode(this.parent, this.e);
    }
}