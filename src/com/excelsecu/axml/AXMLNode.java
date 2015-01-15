package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

public class AXMLNode implements Cloneable {
    private AXMLNode parent;
    private List<AXMLNode> children;
    /**the layer of AXMLNode tree, start with 0**/
    private int layer;
    private Element e;
    private List<Attribute> attrList;
    private String objectName = "";
    private Class<?> type = null;
    
    public AXMLNode(AXMLNode parent, Element e, int layer) {
        this.parent = parent;
        this.layer = layer;
        this.children = new ArrayList<AXMLNode>();
        //Add it to this parent's chilren list
        if (parent != null) {
            parent.addChild(this);
        }
        if (e == null) {
            throw new AXMLException(AXMLException.PARAMETER_NOT_INITIALIZE,
                    "AXMLNode constructor Element object is null");
        } else {
            this.e = e;
        }
        
        this.type = Util.matchClass(e.getName());
        
        this.attrList = new ArrayList<Attribute>();
        for (int i = 0; i < e.attributeCount(); i++) {
            attrList.add(e.attribute(i));
        }
    }
    
    public void addChild(AXMLNode child) {
        children.add(child);
    }
    
    public AXMLNode getParent() {
        return parent;
    }
    
    public List<AXMLNode> getChildren() {
        return children;
    }
    
    public Class<?> getType() {
        if (type == null) {
            throw new AXMLException(AXMLException.CLASS_NOT_FOUND, getLabelName());
        }
        return type;
    }
    
    public String getLabelName() {
        return e.getName();
    }
    
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public String getObjectName() {
        return objectName;
    }
    
    public List<Attribute> getAttributes() {
        return attrList;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public String asXML() {
        return e.asXML();
    }
    
    public AXMLNode clone() {
        return new AXMLNode(this.parent, this.e, this.layer);
    }
}