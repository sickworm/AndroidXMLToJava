package com.excelsecu.axml;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;

public class SelectorConverter {
    private static final int TYPE_NOT_INITIALIZE = -1;
    private static final int TYPE_NOTHING = 0;
    private static final int TYPE_COLOR = 1;
    private static final int TYPE_DRAWABLE = 2;
    private AXMLNode node;
    private int type = TYPE_NOT_INITIALIZE;
    private List<String> importList = new ArrayList<String>(); 
    
    public static void main(String[] argv) {
        System.out.println(new SelectorConverter(new AXMLParser("res/drawable/color_selector.xml").parse()).convert());
    }
    
    public SelectorConverter(AXMLNode node) {
        this.node = node;
    }
    
    public String convert() {
        if (typeOfSelector() == TYPE_COLOR) {
            return convertToColorStateList();
        } else if (typeOfSelector() == TYPE_DRAWABLE) {
            return convertToStateListDrawable();
        } else {
            throw new AXMLException(AXMLException.AXML_PARSE_ERROR, "not a selector type");
        }
    }
    
    private int typeOfSelector() {
        if (type != TYPE_NOT_INITIALIZE) {
            return type;
        }
        
        for (AXMLNode n : node.getChildren()) {
            for (Attribute a : n.getAttributes()) {
                if (a.getQualifiedName().equals("android:color")) {
                    return (type = TYPE_COLOR);
                } else if (a.getQualifiedName().equals("android:drawable")) {
                    return (type = TYPE_DRAWABLE);
                }
            }
        }
        return (type = TYPE_NOTHING);
    }
    
    private String convertToColorStateList() {
        addImport(Context.class.getName());
        addImport(ColorStateList.class.getName());
        String javaBlock = "";
        String stateSetList = "";
        String colorList = "";
        for (AXMLNode n : node.getChildren()) {
            if (!n.getLabelName().equals("item")) {
                continue;
            }
            String stateSet = "";
            String color = "";
            for (Attribute a : n.getAttributes()) {
                String attrName = a.getQualifiedName();
                if (attrName.equals("android:color")) {
                    color = LayoutTranslater.translateValue(a);
                    if (color.contains("AXMLResources")) {
                        addImport(Config.PACKAGE_NAME + ".AXMLResources");
                    }
                } else {
                    String state = "android.R.attr." + a.getName();
                    if (a.getValue().equals("false")) {
                        state = "-" + state;
                    }
                    if (stateSet.equals("")) {
                        stateSet = state;
                    } else {
                        stateSet += ", " + state;
                    }
                }
            }
            if (colorList.equals("")) {
                colorList = color;
            } else {
                colorList += ", " + color;
            }
            stateSetList += "\n\t{" + stateSet + "},";
        }
        //remove comma
        stateSetList = stateSetList.substring(0, stateSetList.length() - 1);

        javaBlock += "int[][] stateSet" + " = new int[][] {" + stateSetList + "};\n";
        javaBlock += "int[] colorSet" + " = new int[] {" + colorList + "};\n";
        javaBlock += "ColorStateList colorStateList = new ColorStateList(stateSet, colorSet);\n";
        return javaBlock;
    }
    
    private String convertToStateListDrawable() {
        addImport(StateListDrawable.class.getName());
        addImport(Context.class.getName());
        addImport(Config.PACKAGE_NAME + ".R");
        int num = 0;
        String javaBlock = "";
        javaBlock += "StateListDrawable stateListDrawable = new StateListDrawable();\n";
        for (AXMLNode n : node.getChildren()) {
            if (!n.getLabelName().equals("item")) {
                continue;
            }
            String stateSet = "";
            String drawable = "";
            for (Attribute a : n.getAttributes()) {
                String attrName = a.getQualifiedName();
                if (attrName.equals("android:drawable")) {
                    drawable = LayoutTranslater.translateValue(a);
                    if (drawable.contains("AXMLResources")) {
                        addImport(Config.PACKAGE_NAME + ".AXMLResources");
                    }
                } else {
                    String state = "android.R.attr." + a.getName();
                    if (a.getValue().equals("false")) {
                        state = "-" + state;
                    }
                    if (stateSet.equals("")) {
                        stateSet = state;
                    } else {
                        stateSet += ", " + state;
                    }
                }
            }
            
            String setName = "stateSet" + num;
            javaBlock += "int[] " + setName + " = new int[] {" + stateSet + "};\n";
            javaBlock += "stateListDrawable.addState(" + setName + ", " + drawable + ");\n";
            num++;
        }
        return javaBlock;
    }
    
    private void addImport(String className) {
        if (!Utils.hasString(importList, className)) {
            importList.add(className);
        }
    }
    
    public List<String> getImportList() {
        return importList;
    }
}