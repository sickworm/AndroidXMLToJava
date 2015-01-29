package com.excelsecu.androidx2j;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;

public class AX2JNode implements Cloneable {
    private AX2JNode parent;
    private List<AX2JNode> children;
    private Element element;
    private List<Attribute> attrList;
    private String objectName = "object";
    private Class<?> type = null;
    
    @SuppressWarnings("unchecked")
	public AX2JNode(AX2JNode parent, Element element) {
        if (element == null) {
            throw new AX2JException(AX2JException.PARAMETER_NOT_INITIALIZE,
                    "AXMLNode constructor Element object is null");
        } else {
            this.element = element;
        }
        
        this.parent = parent;
        this.children = new ArrayList<AX2JNode>();
        this.attrList = element.attributes();
        //Add it to this parent's children list
        if (parent != null) {
            parent.addChild(this);
        }
        initType();
    }
	
    /**
     * Find out the Java class relative to the XML label
     */
	@SuppressWarnings("unchecked")
	protected void initType() {
        type = Utils.matchClass(element.getName());
        if (type.equals(Void.class)) {
        	Class <?> parentType = CustomWidget.findParentByLabelName(getLabelName());
        	if (!parentType.equals(Void.class)) {
        		type = parentType;
        	} else if (getLabelName().equals("include")) {
        		type = View.class;
        	} else if (getLabelName().equals("selector")){
                for (Element e : (List<Element>)element.elements()) {
                    for (Attribute a : (List<Attribute>)e.attributes()) {
                        if (a.getQualifiedName().equals("android:color")) {
                            type = ColorStateList.class;
                        } else if (a.getQualifiedName().equals("android:drawable")) {
                        	type = StateListDrawable.class;
                        }
                    }
                }
        	} else if (getLabelName().equals("shape") ||
        	        getLabelName().equals("corners") ||
        	        getLabelName().equals("gradient") ||
        	        getLabelName().equals("padding") ||
        	        getLabelName().equals("size") ||
        	        getLabelName().equals("solid") ||
        	        getLabelName().equals("stroke")) {
                type = GradientDrawable.class;
                setObjectName(Utils.classToObject(GradientDrawable.class.getSimpleName()));
            }
        }
	}
	
    public Attribute findAttrByName(String attrName) {
        List<Attribute> attrList = this.getAttributes(); 
        for (Attribute a : attrList) {
            if (a.getQualifiedName().equals(attrName)) {
                return a;
            }
        }
        return null;
    }
	
	public String toString() {
		return asXML();
	}
    
    public void addChild(AX2JNode child) {
        children.add(child);
    }
    
    public AX2JNode getParent() {
        return parent;
    }
    
    public List<AX2JNode> getChildren() {
        return children;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public void setType(Class<?> type) {
        this.type = type;
    }
    
    public String getLabelName() {
        return element.getName();
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
    
    public String getText() {
        return element.getText();
    }
    
    public String attributeValue(String name) {
        return element.attributeValue(name);
    }
    
    public String asXML() {
        return element.asXML();
    }
    
    public Element getElement() {
        return element;
    }
    
    public AX2JNode clone() {
        return new AX2JNode(this.parent, this.element);
    }
}