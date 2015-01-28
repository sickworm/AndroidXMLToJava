package com.excelsecu.androidx2j;
import org.dom4j.Attribute;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;

public class SelectorTranslator extends BaseTranslator {
    
    public static void main(String[] argv) {
        System.out.println(new SelectorTranslator(new AX2JParser("res/drawable/color_selector.xml").parse()).translate());
    }
    
    public SelectorTranslator(AX2JNode node) {
        super(node);
    }
    
    public String translate() {
        if (getType() == ColorStateList.class) {
            return translateToColorStateList();
        } else if (getType() == StateListDrawable.class) {
            return translateToStateListDrawable();
        } else {
            throw new AX2JException(AX2JException.AXML_PARSE_ERROR, "not a selector type");
        }
    }
    
    private String translateToColorStateList() {
        addImport(Context.class.getName());
        addImport(ColorStateList.class.getName());
        String javaBlock = "";
        String stateSetList = "";
        String colorList = "";
        for (AX2JNode n : getRoot().getChildren()) {
            if (!n.getLabelName().equals("item")) {
                continue;
            }
            String stateSet = "";
            String color = "";
            for (Attribute a : n.getAttributes()) {
                String attrName = a.getQualifiedName();
                if (attrName.equals("android:color")) {
                    color = translateValue(a);
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
                extraHandle(getRoot(), a);
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
    
    private String translateToStateListDrawable() {
        addImport(Context.class.getName());
        addImport(StateListDrawable.class.getName());
        int num = 0;
        String javaBlock = "";
        javaBlock += "StateListDrawable stateListDrawable = new StateListDrawable();\n";
        for (AX2JNode n : getRoot().getChildren()) {
            if (!n.getLabelName().equals("item")) {
                continue;
            }
            String stateSet = "";
            String drawable = "";
            for (Attribute a : n.getAttributes()) {
                String attrName = a.getQualifiedName();
                if (attrName.equals("android:drawable")) {
                    drawable = translateValue(a);
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
                extraHandle(getRoot(), a);
            }
            
            String setName = "stateSet" + num;
            javaBlock += "int[] " + setName + " = new int[] {" + stateSet + "};\n";
            javaBlock += "stateListDrawable.addState(" + setName + ", " + drawable + ");\n";
            num++;
        }
        return javaBlock;
    }
}