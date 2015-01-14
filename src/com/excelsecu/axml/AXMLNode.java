package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import com.excelsecu.axml.dbbuilder.Config;

public class AXMLNode implements Cloneable {
    private AXMLNode parent;
    private List<AXMLNode> children;
    /**the layer of AXMLNode tree, start with 0**/
    private int layer;
    private Element e;
    private List<Attribute> attrList;
    private Class<?> type;
    
    public AXMLNode(AXMLNode parent, Element e, int layer) {
        this.parent = parent;
        this.layer = layer;
        this.children = new ArrayList<AXMLNode>();
        //Add it to this parent's chilren list
        if (parent != null) {
            parent.addChild(this);
        }
        if (e == null) {
            throw new AXMLException(AXMLException.PARAMETER_NOT_INITIALIZE);
        } else {
            this.e = e;
        }
        this.type = matchClass();
        if (this.type == null) {
            throw new AXMLException(AXMLException.CLASS_NOT_FOUND);
        }
        attrList = new ArrayList<Attribute>();
        for (int i = 0; i < e.attributeCount(); i++) {
            attrList.add(e.attribute(i));
        }
    }
    
    public void addChild(AXMLNode child) {
        children.add(child);
    }
    
    private Class<?> matchClass() {
        String name = e.getName();
        for (int i = 0; i < Config.CLASSES_LIST.length; i++) {
            String className = "";
            if (name.contains("support")) {
                className = Config.CLASSES_LIST[i].getName();
            } else {
                className = Config.CLASSES_LIST[i].getSimpleName();
            }
            if (className.equals(name)) {
                return Config.CLASSES_LIST[i];
            }
        }
        return null;
    }
    
    public AXMLNode getParent() {
        return parent;
    }
    
    public List<AXMLNode> getChildren() {
        return children;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public String getName() {
        return e.getName();
    }
    
    public List<Attribute> getAttributes() {
        return attrList;
    }
    
    public int getLayer() {
        return layer;
    }
    
    protected AXMLNode clone() {
        return new AXMLNode(this.parent, this.e, this.layer);
    }
}